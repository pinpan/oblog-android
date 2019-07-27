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
package com.example.android.architecture.blueprints.todoapp.data

import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.source.TppsDataSource
import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeTppsRemoteDataSource : TppsDataSource {

    private var TPPS_SERVICE_DATA: LinkedHashMap<String, Tpp> = LinkedHashMap()

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        TPPS_SERVICE_DATA[tppId]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find tpp"))
    }

    override suspend fun getTpps(): Result<List<Tpp>> {
        return Success(TPPS_SERVICE_DATA.values.toList())
    }

    override suspend fun saveTpp(tpp: Tpp) {
        TPPS_SERVICE_DATA[tpp.id] = tpp
    }

    override suspend fun completeTpp(tpp: Tpp) {
        val completedTpp = Tpp(tpp.title, tpp.description, true, tpp.id)
        TPPS_SERVICE_DATA[tpp.id] = completedTpp
    }

    override suspend fun completeTpp(tppId: String) {
        // Not required for the remote data source.
    }

    override suspend fun activateTpp(tpp: Tpp) {
        val activeTpp = Tpp(tpp.title, tpp.description, false, tpp.id)
        TPPS_SERVICE_DATA[tpp.id] = activeTpp
    }

    override suspend fun activateTpp(tppId: String) {
        // Not required for the remote data source.
    }

    override suspend fun clearCompletedTpps() {
        TPPS_SERVICE_DATA = TPPS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Tpp>
    }

    override suspend fun deleteTpp(tppId: String) {
        TPPS_SERVICE_DATA.remove(tppId)
    }

    override suspend fun deleteAllTpps() {
        TPPS_SERVICE_DATA.clear()
    }
}
