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

package com.applego.oblog.tppwatch

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource

object FakeFailingTppsLocalDataSource : LocalTppDataSource {
    override suspend fun getTpps(filter: TppsFilter): Result<List<Tpp>> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun saveTpp(tpp: Tpp) {
        TODO("not implemented")
    }

    override suspend fun followTpp(tpp: Tpp) {
        TODO("not implemented")
    }

    override suspend fun followTpp(tppId: String) {
        TODO("not implemented")
    }

    override suspend fun activateTpp(tpp: Tpp) {
        TODO("not implemented")
    }

    override suspend fun activateTpp(tppId: String) {
        TODO("not implemented")
    }

    override suspend fun clearFollowedTpps() {
        TODO("not implemented")
    }

    override suspend fun deleteAllTpps() {
        TODO("not implemented")
    }

    override suspend fun deleteTpp(tppId: String) {
        TODO("not implemented")
    }
}
