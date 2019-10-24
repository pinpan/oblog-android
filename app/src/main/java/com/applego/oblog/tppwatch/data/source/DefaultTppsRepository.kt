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
        private val tppsRemoteDataSource: RemoteTppDataSource,
        private val tppsLocalDataSource: LocalTppDataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TppsRepository {

    private var cachedTpps: ConcurrentMap<String, Tpp> = ConcurrentHashMap()

    val waitTwoSeconds : Timeout = Timeout()

    override suspend fun getTpps(forceUpdate: Boolean): Result<List<Tpp>> {

        wrapEspressoIdlingResource {

            return withContext(ioDispatcher) {
                if (forceUpdate) {
                    fetchTppsFromRemoteDatasource()
                }
                // TODO: Avoid multiple fetches running in paralel: Make newTPPs a member so we can check if already Loading

                // Respond immediately with cache if available and not dirty
                val newTpps = loadTppsFromLocalDatasource()

                // Refresh the cache with the new tpps
                (newTpps as? Success)?.let {
                    if (!it.data.isEmpty()) {
                        refreshCache(it.data)
                        //return@withContext Success(it.data.sortedBy { it.id })
                    }
                }

                (newTpps as? Loading)?.let {
                    return@withContext Loading(waitTwoSeconds.timeout(2000, TimeUnit.MILLISECONDS))
                }

                cachedTpps.values.let { tpps ->
                    return@withContext Success(tpps.sortedBy { it.id })
                }

                //return@withContext Error(Exception("Illegal state"))
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
            /*is Loading -> {
                return remoteTpps
            }*/
            is Error -> Timber.w("Remote data source fetch failed: %s", tppsListResponse.exception)
            //else -> throw IllegalStateException()
        }
    }

    private suspend fun loadTppsFromLocalDatasource(): Result<List<Tpp>>? {

        val localTpps = tppsLocalDataSource.getTpps()
        if (localTpps is Success) {
            return localTpps
        } /*else {
            return localTpps;
        }*/
        return Error(Exception("Error loading Tpps from local datasource: " + localTpps))
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

                // Refresh the cache with the new tpps
                (newTpp as? Success)?.let { cacheTpp(it.data) }

                return@withContext newTpp
            }
        }
    }

    private suspend fun fetchTppFromRemoteOrLocal (
        tppId: String,
        forceUpdate: Boolean
    ): Result<Tpp> {
        // Remote first
        val remoteTpp = tppsRemoteDataSource.getTpp(tppId)
        when (remoteTpp) {
            is Error -> Timber.w("Remote data source fetch failed")
            is Success -> {
                refreshLocalDataSource(remoteTpp.data)
                return remoteTpp
            }
            is Loading -> {
            }
        }

        // Don't read from local if it's forced
        if (forceUpdate) {
            return Error(Exception("Refresh failed"))
        }

        // Local if remote fails
        val localTpps = tppsLocalDataSource.getTpp(tppId)
        if (localTpps is Success) return localTpps
        return Error(Exception("Error fetching from remote and local"))
    }

    override suspend fun saveTpp(tpp: Tpp) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            coroutineScope {
                //launch { tppsRemoteDataSource.saveTpp(it) }
                launch { tppsLocalDataSource.saveTpp(it) }
            }
        }
    }

    override suspend fun unollowTpp(tpp: Tpp) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            it.isFollowed = true
            coroutineScope {
                //launch { tppsRemoteDataSource.unfollowTpp(it) }
                launch { tppsLocalDataSource.unfollowTpp(it) }
            }
        }
    }

    override suspend fun followTpp(tppId: String) {
        withContext(ioDispatcher) {
            getTppWithId(tppId)?.let {
                unollowTpp(it)
            }
        }
    }

    override suspend fun activateTpp(tpp: Tpp) = withContext(ioDispatcher) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            it.isFollowed = false
            coroutineScope {
                //launch { tppsRemoteDataSource.activateTpp(it) }
                launch { tppsLocalDataSource.activateTpp(it) }
            }

        }
    }

    override suspend fun activateTpp(tppId: String) {
        withContext(ioDispatcher) {
            getTppWithId(tppId)?.let {
                activateTpp(it)
            }
        }
    }

    override suspend fun clearFollowedTpps() {
        coroutineScope {
            // TODO:
            //launch { tppsRemoteDataSource.clearFollowedTpps() }
            launch { tppsLocalDataSource.clearFollowedTpps() }
        }
        withContext(ioDispatcher) {
            cachedTpps.entries.removeAll { it.value.isFollowed }
        }
    }

    override suspend fun deleteAllTpps() {
        withContext(ioDispatcher) {
            coroutineScope {
                //launch { tppsRemoteDataSource.deleteAllTpps() }
                launch { tppsLocalDataSource.deleteAllTpps() }
            }
        }
        cachedTpps.clear()
    }

    override suspend fun deleteTpp(tppId: String) {
        coroutineScope {
            //launch { tppsRemoteDataSource.deleteTpp(tppId) }
            launch { tppsLocalDataSource.deleteTpp(tppId) }
        }

        cachedTpps.remove(tppId)
    }

    private fun refreshCache(tpps: List<Tpp>) {
        cachedTpps.clear()
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
    }

    private suspend fun refreshLocalDataSource(tpp: Tpp) {
        tppsLocalDataSource.saveTpp(tpp)
    }

    private fun getTppWithId(id: String) = cachedTpps.get(id)

    private fun cacheTpp(tpp: Tpp): Tpp {
        val cachedTpp = Tpp(tpp.entityCode, tpp.title, tpp.description, tpp.isFollowed, tpp.globalUrn, tpp.status, tpp.ebaEntityVersion, tpp.id)
        // Create if it doesn't exist.
        /* Declared as new object -> Test if not null and remove this check
        if (cachedTpps == null) {
            cachedTpps = ConcurrentHashMap()
        }*/
        cachedTpps.put(cachedTpp.id, cachedTpp)
        return cachedTpp
    }

    private inline fun cacheAndPerform(tpp: Tpp, perform: (Tpp) -> Unit) {
        val cachedTpp = cacheTpp(tpp)
        perform(cachedTpp)
    }
}
