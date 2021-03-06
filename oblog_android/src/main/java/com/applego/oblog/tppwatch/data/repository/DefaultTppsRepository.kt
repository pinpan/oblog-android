package com.applego.oblog.tppwatch.data.repository

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Loading
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Warn
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.ListResponse
import com.applego.oblog.tppwatch.data.source.remote.Paging
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
import java.util.stream.Collectors

/**
 * Concrete implementation to load tpps from the data sources into a cache.
 *
 * To simplify the sample, this repository only uses the local data source only if the remote
 * data source fails. Remote is the source of truth.
 */
class DefaultTppsRepository (
        var tppsEbaDataSource: RemoteTppDataSource<EbaEntity>,
        var tppsNcaDataSource: RemoteTppDataSource<NcaEntity>,
        var tppsLocalDataSource: LocalTppDataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : TppsRepository {

    private var cachedTpps: ConcurrentMap<String, Tpp> = ConcurrentHashMap()

    val wait100MilliSeconds : Timeout = Timeout()

    override suspend fun getAllTpps(forceUpdate: Boolean): Result<List<Tpp>> {
        wrapEspressoIdlingResource {
            return withContext(ioDispatcher) {
                if (forceUpdate) {
                    var paging = Paging(100, 1, 0, true)
                    while (!paging.last) {
                        val tppsListResponse = fetchTppsPageFromRemoteDatasource(paging)
                        when (tppsListResponse) {
                            is Success -> {
                                updateLocalDataSource(tppsListResponse.data.aList)

                                paging = tppsListResponse.data.paging
                            }
                            is Error -> {
                                Timber.w("Remote data source fetch error: %s", tppsListResponse.exception)
                                paging.last = true
                            }
                        }
                    }
                }
                return@withContext loadTppsFromLocalDatasource()
            }
        }
    }

    override suspend fun fetchTppsPageFromRemoteDatasource(paging: Paging): Result<TppsListResponse> {
        val ebaEntitiesListResponse: Result<ListResponse<EbaEntity>> = tppsEbaDataSource.getEntitiesPage(paging)
        var tppsList: List<Tpp> = listOf()
        when (ebaEntitiesListResponse) {
            is Success -> {
                tppsList = ebaEntitiesListResponse.data.aList.stream().map {it -> Tpp(it, NcaEntity())}.collect(Collectors.toList())
                updateLocalDataSource(tppsList)

                val resultListResponse = TppsListResponse()
                resultListResponse.aList = tppsList
                resultListResponse.paging = ebaEntitiesListResponse.data.paging
                return Success(resultListResponse)
            }
            is Exception -> {
                Timber.w("Remote data source fetch failed: %s", ebaEntitiesListResponse)
                paging.last = true
                val emptyListResponse = ListResponse<Tpp>()
                emptyListResponse.paging = paging
                return Warn("Remote data source fetch failed: %s\", ebaEntitiesListResponse.exception", "")
            }
            else -> {
                return Warn("Remote data source fetch was not successful", "")
            }
        }
    }

    override suspend fun loadTppsFromLocalDatasource(): Result<List<Tpp>> {
        return loadTppsFromLocalDatasource("followed", true)
    }

    override suspend fun loadTppsFromLocalDatasource(orderBy: String, isAsc: Boolean): Result<List<Tpp>> {
        //TODO: dont use the parameters and remove method if not needed.
        val localTpps = tppsLocalDataSource.getTpps()
        if (localTpps is Success) {
            return localTpps
        } else if (localTpps is Loading) {
            return Loading(wait100MilliSeconds.timeout(100, TimeUnit.MILLISECONDS));
        } else {
            return Error(Exception("Error loading Tpps from local datasource: " + localTpps))
        }
    }

    /**
     * Relies on [filterTpps] to fetch data and picks the tpp with the same ID.
     */
    override suspend fun getTpp(tppId: String, forceUpdate: Boolean): Result<Tpp> {

        wrapEspressoIdlingResource {
            return getTppBlocking(tppId, forceUpdate)
        }
    }

    override suspend fun getTppBlocking(tppId: String, forceUpdate: Boolean): Result<Tpp> {
        return withContext(ioDispatcher) {
            getTppWithId(tppId)?.let {
                EspressoIdlingResource.decrement() // Set app as idle.
                return@withContext Success(it)
            }

            return@withContext fetchTppFromLocalOrRemote(tppId, forceUpdate)
        }
    }

    /*override*/ suspend fun getTppBlocking2(tppId: String, forceUpdate: Boolean): Result<Tpp> {
        /*return withContext(ioDispatcher) {
            getTppWithId(tppId)?.let {
                EspressoIdlingResource.decrement() // Set app as idle.
                return@withContext Success(it)
            }*/

        return/*@withContext*/ Success((fetchTppFromLocalOrRemote(tppId, forceUpdate) as Success).data)
        //}
    }

    private suspend fun fetchTppFromLocalOrRemote (
        tppId: String,
        forceUpdate: Boolean
    ): Result<Tpp> { //Result<Pair<Tpp, Tpp>> {

        var tpp : Tpp?= null

        val tppResultTuple: Pair<Tpp, Tpp>? = null

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

        if (tpp != null) {
            var ebaUpdate: Boolean = false
            if (forceUpdate) {
                // TODO: Consider other request parameters to achieve uniqueness of the result
                val result= tppsEbaDataSource.getEntityById(tpp.getCountry(), tpp.getEntityId())
                when (result) {
                    is Error -> {
                        Timber.w("Eba remote data source fetch failed with error: %s.", result.exception)
                    }
                    is Warn -> {
                        Timber.w("Eba remote data source fetch failed with warning: %s", result.warning)
                    }
                    is Success -> {
                        tpp.ebaEntity = result.data
                    }
                }
            }

            var ncaUpdate = false
            // TODO: Consider other request parameters to achieve uniqueness of the result
            val resultNca = tppsNcaDataSource.getEntityById(tpp.getCountry(), tpp.getEntityId())
            when (resultNca) {
                is Error -> {
                    Timber.w("Nca remote data source fetch failed with error: %s.", resultNca.exception)
                }
                is Warn -> {
                    Timber.w("Nca remote data source fetch failed with warning: %s.", resultNca.warning)
                }
                is Success -> {
                    tpp.ncaEntity = resultNca.data
                }
            }

/*
            if (ebaUpdate || ncaUpdate) {
                updateLocalDataSource(tpp)
            }

*/
            return /*Result<Pair<Tpp, Tpp>>(tppResultTuple)*/ tppResult
        }

        return Error(Exception("Error fetching from remote and local"))
    }

    private fun updateTppFromRemote(tpp: Tpp, updateFrom: Tpp): Boolean {
        return updateEbaEntityFromRemote(tpp.ebaEntity, tpp.ebaEntity) || updateNcaEntityFromRemote(tpp.ncaEntity, tpp.ncaEntity)
    }

    private fun updateEbaEntityFromRemote(ebaEntity: EbaEntity, updateFrom: EbaEntity): Boolean {
        var updated = false

        if (!ebaEntity._entityName.equals(updateFrom.getEntityName())) {
            ebaEntity._entityName = updateFrom.getEntityName()
            updated = true
        }

        if (!ebaEntity._description.equals(updateFrom.getDescription())) {
            ebaEntity._description = updateFrom.getDescription()
            updated = true
        }
        // TODO: Update what is relevant

        return updated
    }

    private fun updateNcaEntityFromRemote(ncaEntity: NcaEntity, updateFrom: NcaEntity): Boolean {
        var updated = false

        if (!ncaEntity._entityName.equals(updateFrom.getEntityName())) {
            ncaEntity._entityName = updateFrom.getEntityName()
            updated = true
        }

        if (!ncaEntity._description.equals(updateFrom.getDescription())) {
            ncaEntity._description = updateFrom.getDescription()
            updated = true
        }
        // TODO: Update what is relevant

        return updated
    }

    override suspend fun saveTpp(tpp: Tpp) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            coroutineScope {
                launch { tppsLocalDataSource.saveTpp(it) }
            }
        }
    }

    override suspend fun setTppFollowedFlag(tpp: Tpp, followed: Boolean) {
        cacheAndPerform(tpp) {
            tpp.let {
                it.ebaEntity.followed = followed
                coroutineScope {
                    launch {
                        tppsLocalDataSource.updateFollowing(it, it.ebaEntity.isFollowed())
                    }
                }
            }
        }
    }

    override suspend fun refreshTpp(tpp: Tpp)  = coroutineScope {
        var tppsResult: Result<Tpp> = Result.Loading(Timeout())
        val result = async {
            tppsResult = getTpp(tpp.getId())
        }
        result.await()

        if (tppsResult is Success<Tpp>) {
            tpp.appsPortfolio = (tppsResult as Success<Tpp>).data.appsPortfolio
            tpp.ebaEntity = (tppsResult as Success<Tpp>).data.ebaEntity
            tpp.ncaEntity = (tppsResult as Success<Tpp>).data.ncaEntity
            tpp.setFollowed((tppsResult as Success<Tpp>).data.isFollowed())
            //tpp.setUsed((tppsResult as Success<Tpp>).data.isUsed())
        }
    }

    /*override suspend fun setTppActivateFlag(tpp: Tpp, used: Boolean) = withContext(ioDispatcher) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            it.ebaEntity.used = used
            coroutineScope {
                launch { tppsLocalDataSource.setTppActivateFlag(it.ebaEntity.getId(), used) }
            }
        }
    }*/

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

    override suspend fun updateLocalDataSource(tpps: List<Tpp>?) {
        if (tpps != null) {
            for (tpp in tpps) {
                updateLocalDataSource(tpp)
            }
        }
    }

    override suspend fun updateLocalDataSource(tpp: Tpp) {
        tppsLocalDataSource.saveTpp(tpp)
    }

    private fun getTppWithId(id: String) = cachedTpps.get(id)

    private inline fun cacheAndPerform(tpp: Tpp, perform: (Tpp) -> Unit) {
        perform(tpp)
    }

    override suspend fun saveApp(tpp: Tpp, app: App) {
        app.tppId = tpp.getId()
        tpp.appsPortfolio.addApp(app)

        coroutineScope {
            tppsLocalDataSource.saveАpp(app)
            tppsLocalDataSource.saveTpp(tpp)
        }
    }

    override suspend fun deleteApp(app: App) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        tppsLocalDataSource.deleteАpp(app)
    }

    override suspend fun updateApp(tpp: Tpp, app: App) {
        coroutineScope {
            launch {
                tppsLocalDataSource.saveАpp(app)
            }
        }
    }
}
