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

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.applego.oblog.tppwatch.data.FakeTppsRemoteDataSource
import com.applego.oblog.tppwatch.data.source.DefaultTppsRepository
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.data.source.local.TppsDaoDataSource
import com.applego.oblog.tppwatch.data.source.local.TppDatabase
import com.applego.oblog.tppwatch.data.source.remote.eba.EbaService
import com.applego.oblog.tppwatch.data.source.remote.eba.TppsEbaDataSource
import kotlinx.coroutines.runBlocking

/**
 * A Service Locator for the [TppsRepository]. This is the mock version, with a
 * [FakeTppsRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: TppDatabase? = null
    @Volatile
    var tppsRepository: TppsRepository? = null
        @VisibleForTesting set

    fun provideTppsRepository(context: Context): TppsRepository {
        synchronized(this) {
            return tppsRepository ?: tppsRepository ?: createTppsRepository(context)
        }
    }

    private fun createTppsRepository(context: Context): TppsRepository {
        return DefaultTppsRepository(createTppsRestDataSource(context), createTppLocalDataSource(context))
    }

    private fun createTppLocalDataSource(context: Context): LocalTppDataSource {
        val database = database ?: createDataBase(context)
        return TppsDaoDataSource(database.tppDao())
    }

    private fun createTppsRestDataSource(context: Context): TppsEbaDataSource {
        val database = database ?: createDataBase(context)
        return TppsEbaDataSource(EbaService.create(), database.tppDao())
    }

    private fun createDataBase(context: Context): TppDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            TppDatabase::class.java, "Tpps.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                FakeTppsRemoteDataSource.deleteAllTpps()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tppsRepository = null
        }
    }
}
