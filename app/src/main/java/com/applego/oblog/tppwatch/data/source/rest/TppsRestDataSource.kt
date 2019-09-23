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
package com.applego.oblog.tppwatch.data.source.rest

import android.content.ContentValues.TAG
import android.provider.Contacts
import android.util.Log
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.Tpp
import com.applego.oblog.tppwatch.data.source.TppsDataSource
import com.applego.oblog.tppwatch.data.source.local.TppsDao
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Concrete implementation of a data source as a db.
 */
class TppsRestDataSource internal constructor (
        private val tppsService: EbaService,
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TppsDataSource {

    /*
        return@withContext try {
            Success(tppsService.getTpps())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> = withContext(ioDispatcher) {
        try {
            val tpp = tppsService.getTppById(tppId)
            if (tpp != null) {
                return@withContext Success(tpp)
            } else {
                return@withContext Error(Exception("Tpp not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }


        val call = ebaAPI.getTpps()
        call.enqueue(object: Callback< List<Tpp>!>! {
            //this)

            override fun onResponse(call: Call<List<Tpp>>, response: Response<List<Tpp>>) {
                if (response.isSuccessful()) {
                    val tppsList = response.body()
                    tppsList?.forEach { tpp ->
                        System.out.println(tpp.title)
                    }
                } else {
                    System.out.println(response.errorBody())
                }
            }

            override fun onFailure(call: Call<List<Tpp>>, t: Throwable) {
                t.printStackTrace()
            }
        })

    */



    /*override suspend fun getTpps(): Result<List<Tpp>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(tppsDao.getTpps())
        } catch (e: Exception) {
            Error(e)
        }
    }*/

    override suspend fun getTpps(): Result<List<Tpp>> = withContext(ioDispatcher) {
        var tppsList: List<Tpp> = List<Tpp>(0){ Tpp()}
        val call = tppsService.getTpps()
        call.enqueue(object: Callback<List<Tpp>> {

            override fun onResponse(call: Call<List<Tpp>>, response: Response<List<Tpp>>) {
                if (response.isSuccessful()) {
                    //var tppsList : List<Tpp>
                    tppsList = response.body()!!
                    tppsList?.forEach { tpp ->
                        System.out.println("Insert/Update tpp: " + tpp.title + " into database")

                        runBlocking<Unit> {
                            if (tppsDao.getTppById(tpp.id) == null) {
                                tppsDao.insertTpp(tpp)
                            } else {
                                tppsDao.updateTpp(tpp)
                            }
                        }
                    }
                } else {
                    System.out.println(response.errorBody())
                    //return@withContext  com.applego.oblog.tppwatch.data.Result.Error()
                }
            }

            override fun onFailure(call: Call<List<Tpp>>, t: Throwable) {
                t.printStackTrace()
            }
        })

        return@withContext Success(tppsList)
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun saveTpp(tpp: Tpp): Unit = withContext(ioDispatcher) {
        tppsService.insertTpp(tpp)
    }

    override suspend fun completeTpp(tpp: Tpp): Unit = withContext(ioDispatcher) {
        tppsService.updateCompleted(tpp.id, true)
    }

    override suspend fun completeTpp(tppId: String) {
        tppsService.updateCompleted(tppId, true)
    }

    override suspend fun activateTpp(tpp: Tpp) = withContext(ioDispatcher) {
        tppsService.updateCompleted(tpp.id, false)
    }

    override suspend fun activateTpp(tppId: String) {
        tppsService.updateCompleted(tppId, false)
    }

    override suspend fun clearCompletedTpps() = withContext<Unit>(ioDispatcher) {
        tppsService.deleteCompletedTpps()
    }

    override suspend fun deleteAllTpps(): Unit = withContext(ioDispatcher) {
        tppsService.deleteTpps()
    }

    override suspend fun deleteTpp(tppId: String) = withContext<Unit>(ioDispatcher) {
        tppsService.deleteTppById(tppId)
    }
/**/
}
