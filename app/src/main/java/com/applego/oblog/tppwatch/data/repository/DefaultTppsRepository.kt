package com.applego.oblog.tppwatch.data.repository

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Loading
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Warn
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import com.applego.oblog.tppwatch.util.EspressoIdlingResource
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import okio.Timeout
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit

/**
 * Concrete implementation to load tpps from the data sources into a cache.
 *
 * To simplify the sample, this repository only uses the local data source only if the remote
 * data source fails. Remote is the source of truth.
 */
class DefaultTppsRepository (
        /*private */var tppsEbaDataSource: RemoteTppDataSource,
        /*private */var tppsNcaDataSource: RemoteTppDataSource,
                    var tppsLocalDataSource: LocalTppDataSource,
                    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : TppsRepository {

    private var cachedTpps: ConcurrentMap<String, Tpp> = ConcurrentHashMap()

    val waitTwoSeconds : Timeout = Timeout()

    override suspend fun getAllTpps(forceUpdate: Boolean): Result<List<Tpp>> {
        return filterTpps(TppsFilter(), forceUpdate)
    }

    override suspend fun filterTpps(filter: TppsFilter, forceUpdate: Boolean): Result<List<Tpp>> {

        wrapEspressoIdlingResource {

            return withContext(ioDispatcher) {

                if (forceUpdate) {
                    fetchTppsFromRemoteDatasource()
                }
                // TODO:
                //  Verify multiple fetches running in parallel are prevented:
                //          in the load.... call

                val newTpps = loadTppsFromLocalDatasource(filter)

                return@withContext newTpps
            }
        }
    }

    /**
     * This method ensures we have Up-To-Date repository version, according to set freshness requirements.
     * If @param forced is true, Do full refresh from remote source
     */
    private suspend fun fetchTppsFromRemoteDatasource() {
        // If forced to update -> Get remotes now, otherwise call
        val tppsListResponse: Result<TppsListResponse> = tppsEbaDataSource.getAllTpps()
        when (tppsListResponse) {
            is Success -> {
                refreshLocalDataSource(tppsListResponse.data.tppsList)
            }
            is Error -> Timber.w("Remote data source fetch failed: %s", tppsListResponse.exception)
        }
    }

    private suspend fun loadTppsFromLocalDatasource(filter: TppsFilter): Result<List<Tpp>> {

        val localTpps = tppsLocalDataSource.getTpps(filter)
        if (localTpps is Success) {
            return localTpps
        } else if (localTpps is Loading) {
            return Loading(waitTwoSeconds.timeout(2000, TimeUnit.MILLISECONDS));
        } else {
            return Error(Exception("Error loading Tpps from local datasource: " + localTpps))
        }
    }

    /**
     * Relies on [filterTpps] to fetch data and picks the tpp with the same ID.
     */
    override suspend fun getTpp(tppId: String, forceUpdate: Boolean): Result<Tpp> {

        wrapEspressoIdlingResource {

            return withContext(ioDispatcher) {
                // Respond immediately with cache if available
                getTppWithId(tppId)?.let {
                    EspressoIdlingResource.decrement() // Set app as idle.
                    return@withContext Success(it)
                }

                return@withContext fetchTppFromLocalOrRemote(tppId, forceUpdate)
            }
        }
    }

    private suspend fun fetchTppFromLocalOrRemote (
        tppId: String,
        forceUpdate: Boolean
    ): Result<Tpp> {

        var tpp : Tpp?= null

        // Local DB first
        val tppResult : Result<Tpp> = tppsLocalDataSource.getTpp(tppId)
        when (tppResult) {
            is Success -> {
                tpp = tppResult.data
            }
            is Error -> {
                Timber.w("Couldn't find TPP in local DB. a) TPP ID is invalid; b) TPP it doesn't exist; c) Local DB is not synchronized with EBA registry.")
                return tppResult
            }
        }

        // Local if local fails
        if (tpp != null) {
            if (forceUpdate) {
                var ebaUpdate: Boolean = false
                var ncaUpdate: Boolean = false

                val result = tppsEbaDataSource.getTppById(tpp.getCountry(), tpp.getEntityId())
                when (result) {
                    is Error -> {
                        Timber.w("Eba remote data source fetch failed with error: %s.", result.exception)
                    }
                    is Warn -> {
                        Timber.w("Eba remote data source fetch failed with warning: %s", result.warning)
                    }
                    is Success -> {
                        ebaUpdate = updateTppFromRemote(tpp, result.data)
                    }
                }

                val resultNca = tppsNcaDataSource.getTppById(tpp.getCountry(), tpp.getEntityId())
                when (resultNca) {
                    is Error -> {
                        Timber.w("Nca remote data source fetch failed with error: %s.", resultNca.exception)
                    }
                    is Warn -> {
                        Timber.w("Nca remote data source fetch failed with warning: %s.", resultNca.warning)
                    }
                    is Success -> {
                        ebaUpdate = updateTppFromRemote(tpp, resultNca.data)
                    }
                }

                if (ebaUpdate || ncaUpdate) {
                    refreshLocalDataSource(tpp)
                }
            }
            return tppResult
        }

        return Error(Exception("Error fetching from remote and local"))
    }

    private fun updateTppFromRemote(tpp: Tpp, updateFrom: Tpp): Boolean {
        tpp.ebaEntity._entityName = updateFrom.getEntityName()
        tpp.ebaEntity._description = updateFrom.getDescription()
        // TODO: Update what is relevant

        return true
    }

    override suspend fun saveTpp(tpp: Tpp) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            coroutineScope {
                launch { tppsLocalDataSource.saveTpp(it) }
            }
        }
    }

    override suspend fun setTppFollowedFlag(tpp: Tpp, f: Boolean) {
        cacheAndPerform(tpp) {
            tpp.let {
                it.ebaEntity.followed = f
                        coroutineScope {
                    launch { tppsLocalDataSource.udateFollowing(it, it.ebaEntity.isFollowed()) }
                }
            }
        }
    }

    override suspend fun setTppActivateFlag(tpp: Tpp, a: Boolean) = withContext(ioDispatcher) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            it.ebaEntity.used = a
            coroutineScope {
                launch { tppsLocalDataSource.setTppActivateFlag(it.ebaEntity.getId(), a) }
            }
        }
    }

    override suspend fun deleteAllTpps() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tppsLocalDataSource.deleteAllTpps() }
            }
        }
    }

    override suspend fun deleteTpp(tppId: String) {
        coroutineScope {
            launch { tppsLocalDataSource.deleteTpp(tppId) }
        }
    }

    private fun refreshCache(tpps: List<Tpp>) {
        tpps.sortedBy { it.ebaEntity.getId()}.forEach {
            cacheAndPerform(it) {}
        }
    }

    private suspend fun refreshLocalDataSource(tpps: List<Tpp>?) {
        //TODO: Restore: tppsLocalDataSource.deleteAllTpps()
        if (tpps != null) {
            for (tpp in tpps) {
                tppsLocalDataSource.saveTpp(tpp)
            }
        }
        // TODO: Check if ViewModel refresh is triggered then
    }

    private suspend fun refreshLocalDataSource(tpp: Tpp) {
        tppsLocalDataSource.saveTpp(tpp)
    }

    private fun getTppWithId(id: String) = cachedTpps.get(id)

    private inline fun cacheAndPerform(tpp: Tpp, perform: (Tpp) -> Unit) {
        perform(tpp)
    }


    override suspend fun saveApp(tpp: Tpp, app: App) {
        // Do in memory cache update to keep the app UI up to date
        //cacheAndPerform(аpp) {
            coroutineScope {
                launch { tppsLocalDataSource.saveАpp(app) }
            }
        //}
        cacheAndPerform(tpp) {
            coroutineScope {
                tpp.appsPortfolio.addApp(app)
                launch { tppsLocalDataSource.saveTpp(it) }
            }
        }
    }

    override suspend fun deleteApp(tpp: Tpp, app: App) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateApp(tpp: Tpp, app: App) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
