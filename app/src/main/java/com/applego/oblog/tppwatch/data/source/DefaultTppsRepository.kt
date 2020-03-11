/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applego.oblog.tppwatch.data.source

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Loading
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.eba.TppsListResponse
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
        /*private */var tppsRemoteDataSource: RemoteTppDataSource,
        private val tppsLocalDataSource: LocalTppDataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : TppsRepository {

    private var cachedTpps: ConcurrentMap<String, Tpp> = ConcurrentHashMap()

    val waitTwoSeconds : Timeout = Timeout()

    override suspend fun getTpps(forceUpdate: Boolean): Result<List<Tpp>> {
        return getTpps(forceUpdate, TppsFilter())
    }

    override suspend fun getTpps(forceUpdate: Boolean, filter: TppsFilter): Result<List<Tpp>> {

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
        val tppsListResponse: Result<TppsListResponse> = tppsRemoteDataSource.getAllTpps()
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
     * Relies on [getTpps] to fetch data and picks the tpp with the same ID.
     */
    override suspend fun getTpp(tppId: String, forceUpdate: Boolean): Result<Tpp> {

        wrapEspressoIdlingResource {

            return withContext(ioDispatcher) {
                // Respond immediately with cache if available
                if (!forceUpdate) {
                    getTppWithId(tppId)?.let {
                        EspressoIdlingResource.decrement() // Set app as idle.
                        return@withContext Success(it)
                    }
                }

                val newTpp = fetchTppFromRemoteOrLocal(tppId, forceUpdate)

                return@withContext newTpp
            }
        }
    }

    private suspend fun fetchTppFromRemoteOrLocal (
        tppId: String,
        forceUpdate: Boolean
    ): Result<Tpp> {
        // Remote first
        val result = tppsRemoteDataSource.getTpp(tppId)
        when (result) {
            is Error -> Timber.w("Remote data source fetch failed")
            is Result.Warn -> Timber.w("Remote data source fetch failed die to '" + result.message + "'")
            is Success -> {
                refreshLocalDataSource(result.data)
                return result
            }
            is Loading -> {
                //return result
            }
        }

        // Don't read from local if refresh is forced but remote returned error or warning
        if (forceUpdate) {
            return result //Error(Exception("Refresh failed"))
        }

        // Local if remote fails with Warning
        val localTpps : Result<Tpp> = tppsLocalDataSource.getTpp(tppId)
        when (localTpps) {
            is Success -> return localTpps
            is Error -> return localTpps
            else -> return Error(Exception("Error fetching from remote and local"))
        }
    }

    override suspend fun saveTpp(tpp: Tpp) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            coroutineScope {
                launch { tppsLocalDataSource.saveTpp(it) }
            }
        }
    }

    override suspend fun setTppFollowedFlag(tppId: String, followed: Boolean) {
        (tppsLocalDataSource.getTpp(tppId) as? Success)?.let {
            setTppFollowedFlag(it.data, followed)
        }
    }

    override suspend fun setTppFollowedFlag(tpp: Tpp, followed: Boolean) {
        cacheAndPerform(tpp) {
            tpp.let {
                it.isFollowed = followed
                coroutineScope {
                    launch { tppsLocalDataSource.udateFollowing(it, it.isFollowed) }
                }
            }
        }
    }

    override suspend fun setTppActivateFlag(tpp: Tpp, active: Boolean) = withContext(ioDispatcher) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            it.isActive = active
            coroutineScope {
                launch { tppsLocalDataSource.setTppActivateFlag(it.id, active) }
            }
        }
    }

    override suspend fun setTppActivateFlag(tppId: String, active: Boolean) {
        withContext(ioDispatcher) {
            getTppWithId(tppId)?.let {
                setTppActivateFlag(it, active)
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
        tpps.sortedBy { it.id }.forEach {
            cacheAndPerform(it) {}
        }
    }

    private suspend fun refreshLocalDataSource(tpps: List<Tpp>?) {
        tppsLocalDataSource.deleteAllTpps()
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
}
