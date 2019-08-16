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

import androidx.annotation.VisibleForTesting
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.Tpp
import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeRepository : TppsRepository {

    var tppsServiceData: LinkedHashMap<String, Tpp> = LinkedHashMap()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getTpp(tppId: String, forceUpdate: Boolean): Result<Tpp> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        tppsServiceData[tppId]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find tpp"))
    }

    override suspend fun getTpps(forceUpdate: Boolean): Result<List<Tpp>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        return Success(tppsServiceData.values.toList())
    }

    override suspend fun saveTpp(tpp: Tpp) {
        tppsServiceData[tpp.id] = tpp
    }

    override suspend fun completeTpp(tpp: Tpp) {
        val completedTpp = Tpp(tpp.title, tpp.description, true, tpp.id)
        tppsServiceData[tpp.id] = completedTpp
    }

    override suspend fun completeTpp(tppId: String) {
        // Not required for the remote data source.
        throw NotImplementedError()
    }

    override suspend fun activateTpp(tpp: Tpp) {
        val activeTpp = Tpp(tpp.title, tpp.description, false, tpp.id)
        tppsServiceData[tpp.id] = activeTpp
    }

    override suspend fun activateTpp(tppId: String) {
        throw NotImplementedError()
    }

    override suspend fun clearCompletedTpps() {
        tppsServiceData = tppsServiceData.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Tpp>
    }

    override suspend fun deleteTpp(tppId: String) {
        tppsServiceData.remove(tppId)
    }

    override suspend fun deleteAllTpps() {
        tppsServiceData.clear()
    }

    @VisibleForTesting
    fun addTpps(vararg tpps: Tpp) {
        for (tpp in tpps) {
            tppsServiceData[tpp.id] = tpp
        }
    }
}
