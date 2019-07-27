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
package com.example.android.architecture.blueprints.todoapp.data.source.local

import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Tpp
import com.example.android.architecture.blueprints.todoapp.data.source.TppsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class TppsLocalDataSource internal constructor(
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TppsDataSource {

    override suspend fun getTpps(): Result<List<Tpp>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(tppsDao.getTpps())
        } catch (e: Exception) {
            Error(e)
        }
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

    override suspend fun completeTpp(tpp: Tpp) = withContext(ioDispatcher) {
        tppsDao.updateCompleted(tpp.id, true)
    }

    override suspend fun completeTpp(tppId: String) {
        tppsDao.updateCompleted(tppId, true)
    }

    override suspend fun activateTpp(tpp: Tpp) = withContext(ioDispatcher) {
        tppsDao.updateCompleted(tpp.id, false)
    }

    override suspend fun activateTpp(tppId: String) {
        tppsDao.updateCompleted(tppId, false)
    }

    override suspend fun clearCompletedTpps() = withContext<Unit>(ioDispatcher) {
        tppsDao.deleteCompletedTpps()
    }

    override suspend fun deleteAllTpps() = withContext(ioDispatcher) {
        tppsDao.deleteTpps()
    }

    override suspend fun deleteTpp(tppId: String) = withContext<Unit>(ioDispatcher) {
        tppsDao.deleteTppById(tppId)
    }
}
