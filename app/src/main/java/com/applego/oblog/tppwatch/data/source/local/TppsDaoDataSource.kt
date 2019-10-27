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
package com.applego.oblog.tppwatch.data.source.local

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class TppsDaoDataSource internal constructor(
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalTppDataSource {

    override suspend fun getTpps(filter: TppsFilter): Result<List<Tpp>> = withContext(ioDispatcher) {
        return@withContext try {
            var tpps: List<Tpp>
            if (isOnlyCountry(filter)) {
                tpps = tppsDao.getTppsByCountry(filter.country)
            }
            Success(tppsDao.getTpps())
        } catch (e: Exception) {
            Error(e)
        }
    }

    private fun isOnlyCountry(filter: TppsFilter): Boolean {
        return (!filter.country.isNullOrBlank() && filter.pasportedTo.isNullOrEmpty() && filter.services.isNullOrEmpty() && filter.tppName.isNullOrBlank());
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> = withContext(ioDispatcher) {
        try {
            val tpp = tppsDao.getTppById(tppId)
            if (tpp != null) {
                return@withContext Success(tpp)
            } else {
                return@withContext Error(Exception("Tpp not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun saveTpp(tpp: Tpp) = withContext(ioDispatcher) {
        tppsDao.insertTpp(tpp)
    }

    override suspend fun unfollowTpp(tpp: Tpp) = withContext(ioDispatcher) {
        tppsDao.updateFollowed(tpp.id, true)
    }

    override suspend fun unfollowTpp(tppId: String) {
        tppsDao.updateFollowed(tppId, true)
    }

    override suspend fun activateTpp(tpp: Tpp) = withContext(ioDispatcher) {
        tppsDao.updateFollowed(tpp.id, false)
    }

    override suspend fun activateTpp(tppId: String) {
        tppsDao.updateFollowed(tppId, false)
    }

    override suspend fun clearFollowedTpps() = withContext<Unit>(ioDispatcher) {
        tppsDao.deleteUnfollowedTpps()
    }

    override suspend fun deleteAllTpps() = withContext(ioDispatcher) {
        tppsDao.deleteTpps()
    }

    override suspend fun deleteTpp(tppId: String) = withContext<Unit>(ioDispatcher) {
        tppsDao.deleteTppById(tppId)
    }
}
