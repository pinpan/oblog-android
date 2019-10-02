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
package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.Tpp
import com.applego.oblog.tppwatch.data.source.TppsDataSource
import kotlinx.coroutines.delay

/**
 * Implementation of the data source that adds a latency simulating network.
 */
object TppsRemoteDataSource : TppsDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var TPPS_SERVICE_DATA = LinkedHashMap<String, Tpp>(2)

    init {
        addTpp("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTpp("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    override suspend fun getTpps(): Result<List<Tpp>> {
        // Simulate network by delaying the execution.
        val tpps = TPPS_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Success(tpps)
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        TPPS_SERVICE_DATA[tppId]?.let {
            return Success(it)
        }
        return Error(Exception("Tpp not found"))
    }

    private fun addTpp(title: String, description: String) {
        val newTpp = Tpp(title, description)
        TPPS_SERVICE_DATA[newTpp.id] = newTpp
    }

    override suspend fun saveTpp(tpp: Tpp) {
        TPPS_SERVICE_DATA[tpp.id] = tpp
    }

    override suspend fun unfollowTpp(tpp: Tpp) {
        val followedTpp = Tpp(tpp.title, tpp.description, true, tpp.id)
        TPPS_SERVICE_DATA[tpp.id] = followedTpp
    }

    override suspend fun unfollowTpp(tppId: String) {
        // Not required for the remote data source
    }

    override suspend fun activateTpp(tpp: Tpp) {
        val activeTpp = Tpp(tpp.title, tpp.description, false, tpp.id)
        TPPS_SERVICE_DATA[tpp.id] = activeTpp
    }

    override suspend fun activateTpp(tppId: String) {
        // Not required for the remote data source
    }

    override suspend fun clearFollowedTpps() {
        TPPS_SERVICE_DATA = TPPS_SERVICE_DATA.filterValues {
            !it.isFollowed
        } as LinkedHashMap<String, Tpp>
    }

    override suspend fun deleteAllTpps() {
        TPPS_SERVICE_DATA.clear()
    }

    override suspend fun deleteTpp(tppId: String) {
        TPPS_SERVICE_DATA.remove(tppId)
    }
}
