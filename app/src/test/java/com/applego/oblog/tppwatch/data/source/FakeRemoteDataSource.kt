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
import com.applego.oblog.tppwatch.data.TppFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse

class FakeRemoteDataSource(var tppsListResponse: TppsListResponse? = TppsListResponse(mutableListOf())) : RemoteTppDataSource {
    override suspend fun getAllTpps(): Result<TppsListResponse> {
        tppsListResponse?.let { return Success(it) }
        return Error(
            Exception("Tpps not found")
        )
    }

    override suspend fun getTppById(country: String, tppId: String): Result<Tpp> {
        // #TODO: Get from a MAP the Country Specific EntityCodeSchema. For CZ it is stripping the parts of the EBA Entitiy Code which
        tppsListResponse?.tppsList?.firstOrNull { it.getCountry().equals(country) && it.getEntityId().equals(tppId)}?.let { return Success(it) }
        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun getTppByName(country: String, tppTitle: String): Result<Tpp> {
        tppsListResponse?.tppsList?.firstOrNull { it.getEntityName() == tppTitle}?.let { return Success(it) }
        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun filterTpps(filter: TppFilter): Result<TppsListResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*override suspend fun saveTpp(tppEntity: Tpp) {
        tpps?.add(tppEntity)
    }

    override suspend fun setTppFollowedFlag(tppEntity: Tpp) {
        tpps?.firstOrNull { it.id == tppEntity.id }?.let { it.isFollowed = true }
    }

    override suspend fun setTppFollowedFlag(tppId: String) {
        tpps?.firstOrNull { it.id == tppId }?.let { it.isFollowed = true }
    }

    override suspend fun updateActive(tppEntity: Tpp) {
        tpps?.firstOrNull { it.id == tppEntity.id }?.let { it.isFollowed = false }
    }

    override suspend fun updateActive(tppId: String) {
        tpps?.firstOrNull { it.id == tppId }?.let { it.isFollowed = false }
    }

    override suspend fun clearFollowedTpps() {
        tpps?.removeIf { it.isFollowed }
    }

    override suspend fun deleteAllTpps() {
        tpps?.clear()
    }

    override suspend fun deleteTpp(tppId: String) {
        tpps?.removeIf { it.id == tppId }
    }*/
}
