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

class FakeDataSource(var tpps: MutableList<Tpp>? = mutableListOf()) : TppsDataSource {
    override suspend fun getTpps(): Result<List<Tpp>> {
        tpps?.let { return Success(it) }
        return Error(
            Exception("Tpps not found")
        )
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        tpps?.firstOrNull { it.id == tppId }?.let { return Success(it) }
        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun saveTpp(tpp: Tpp) {
        tpps?.add(tpp)
    }

    override suspend fun completeTpp(tpp: Tpp) {
        tpps?.firstOrNull { it.id == tpp.id }?.let { it.isCompleted = true }
    }

    override suspend fun completeTpp(tppId: String) {
        tpps?.firstOrNull { it.id == tppId }?.let { it.isCompleted = true }
    }

    override suspend fun activateTpp(tpp: Tpp) {
        tpps?.firstOrNull { it.id == tpp.id }?.let { it.isCompleted = false }
    }

    override suspend fun activateTpp(tppId: String) {
        tpps?.firstOrNull { it.id == tppId }?.let { it.isCompleted = false }
    }

    override suspend fun clearCompletedTpps() {
        tpps?.removeIf { it.isCompleted }
    }

    override suspend fun deleteAllTpps() {
        tpps?.clear()
    }

    override suspend fun deleteTpp(tppId: String) {
        tpps?.removeIf { it.id == tppId }
    }
}
