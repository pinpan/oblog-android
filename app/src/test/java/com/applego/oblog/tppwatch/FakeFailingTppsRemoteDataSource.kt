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
import com.applego.oblog.tppwatch.data.TppFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.eba.TppsListResponse

object FakeFailingTppsRemoteDataSource : RemoteTppDataSource {
    override suspend fun getAllTpps(): Result<TppsListResponse/*List<Tpp>*/> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun filterTpps(filter: TppFilter): Result<TppsListResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    /*override suspend fun saveTpp(tppEntity: Tpp) {
        TODO("not implemented")
    }

    override suspend fun setTppFollowedFlag(tppEntity: Tpp) {
        TODO("not implemented")
    }

    override suspend fun setTppFollowedFlag(tppId: String) {
        TODO("not implemented")
    }

    override suspend fun updateActive(tppEntity: Tpp) {
        TODO("not implemented")
    }

    override suspend fun updateActive(tppId: String) {
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
    }*/
}
