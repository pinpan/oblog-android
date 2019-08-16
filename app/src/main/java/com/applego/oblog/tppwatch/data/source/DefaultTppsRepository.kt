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
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.Tpp
import com.applego.oblog.tppwatch.util.EspressoIdlingResource
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Concrete implementation to load tpps from the data sources into a cache.
 *
 * To simplify the sample, this repository only uses the local data source only if the remote
 * data source fails. Remote is the source of truth.
 */
class DefaultTppsRepository(
        private val tppsRemoteDataSource: TppsDataSource,
        private val tppsLocalDataSource: TppsDataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TppsRepository {

    private var cachedTpps: ConcurrentMap<String, Tpp>? = null

    override suspend fun getTpps(forceUpdate: Boolean): Result<List<Tpp>> {

        wrapEspressoIdlingResource {

            return withContext(ioDispatcher) {
                // Respond immediately with cache if available and not dirty
                if (!forceUpdate) {
                    cachedTpps?.let { cachedTpps ->
                        return@withContext Success(cachedTpps.values.sortedBy { it.id })
                    }
                }

                val newTpps = fetchTppsFromRemoteOrLocal(forceUpdate)

                // Refresh the cache with the new tpps
                (newTpps as? Success)?.let { refreshCache(it.data) }

                cachedTpps?.values?.let { tpps ->
                    return@withContext Success(tpps.sortedBy { it.id })
                }

                (newTpps as? Success)?.let {
                    if (it.data.isEmpty()) {
                        return@withContext Success(it.data)
                    }
                }

                return@withContext Error(Exception("Illegal state"))
            }
        }
    }

    private suspend fun fetchTppsFromRemoteOrLocal(forceUpdate: Boolean): Result<List<Tpp>> {
        // Remote first
        val remoteTpps = tppsRemoteDataSource.getTpps()
        when (remoteTpps) {
            is Error -> Timber.w("Remote data source fetch failed")
            is Success -> {
                refreshLocalDataSource(remoteTpps.data)
                return remoteTpps
            }
            else -> throw IllegalStateException()
        }

        // Don't read from local if it's forced
        if (forceUpdate) {
            return Error(Exception("Can't force refresh: remote data source is unavailable"))
        }

        // Local if remote fails
        val localTpps = tppsLocalDataSource.getTpps()
        if (localTpps is Success) return localTpps
        return Error(Exception("Error fetching from remote and local"))
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

    private suspend fun fetchTppFromRemoteOrLocal(
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
            else -> throw IllegalStateException()
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
                launch { tppsRemoteDataSource.saveTpp(it) }
                launch { tppsLocalDataSource.saveTpp(it) }
            }
        }
    }

    override suspend fun completeTpp(tpp: Tpp) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            it.isCompleted = true
            coroutineScope {
                launch { tppsRemoteDataSource.completeTpp(it) }
                launch { tppsLocalDataSource.completeTpp(it) }
            }
        }
    }

    override suspend fun completeTpp(tppId: String) {
        withContext(ioDispatcher) {
            getTppWithId(tppId)?.let {
                completeTpp(it)
            }
        }
    }

    override suspend fun activateTpp(tpp: Tpp) = withContext(ioDispatcher) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(tpp) {
            it.isCompleted = false
            coroutineScope {
                launch { tppsRemoteDataSource.activateTpp(it) }
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

    override suspend fun clearCompletedTpps() {
        coroutineScope {
            launch { tppsRemoteDataSource.clearCompletedTpps() }
            launch { tppsLocalDataSource.clearCompletedTpps() }
        }
        withContext(ioDispatcher) {
            cachedTpps?.entries?.removeAll { it.value.isCompleted }
        }
    }

    override suspend fun deleteAllTpps() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tppsRemoteDataSource.deleteAllTpps() }
                launch { tppsLocalDataSource.deleteAllTpps() }
            }
        }
        cachedTpps?.clear()
    }

    override suspend fun deleteTpp(tppId: String) {
        coroutineScope {
            launch { tppsRemoteDataSource.deleteTpp(tppId) }
            launch { tppsLocalDataSource.deleteTpp(tppId) }
        }

        cachedTpps?.remove(tppId)
    }

    private fun refreshCache(tpps: List<Tpp>) {
        cachedTpps?.clear()
        tpps.sortedBy { it.id }.forEach {
            cacheAndPerform(it) {}
        }
    }

    private suspend fun refreshLocalDataSource(tpps: List<Tpp>) {
        tppsLocalDataSource.deleteAllTpps()
        for (tpp in tpps) {
            tppsLocalDataSource.saveTpp(tpp)
        }
    }

    private suspend fun refreshLocalDataSource(tpp: Tpp) {
        tppsLocalDataSource.saveTpp(tpp)
    }

    private fun getTppWithId(id: String) = cachedTpps?.get(id)

    private fun cacheTpp(tpp: Tpp): Tpp {
        val cachedTpp = Tpp(tpp.title, tpp.description, tpp.isCompleted, tpp.id)
        // Create if it doesn't exist.
        if (cachedTpps == null) {
            cachedTpps = ConcurrentHashMap()
        }
        cachedTpps?.put(cachedTpp.id, cachedTpp)
        return cachedTpp
    }

    private inline fun cacheAndPerform(tpp: Tpp, perform: (Tpp) -> Unit) {
        val cachedTpp = cacheTpp(tpp)
        perform(cachedTpp)
    }
}
