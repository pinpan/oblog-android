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
package com.applego.oblog.tppwatch.data.source.remote.nca

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.TppsDao
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.eba.TppsListResponse
import kotlinx.coroutines.*
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Concrete implementation of a data source as a db.
 */
class TppsNcaDataSource internal constructor (
        private val tppsService: NcaService,
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteTppDataSource {

    var tppsList: List<Tpp> = emptyList() //List<Tpp>(0){ Tpp() }

    override suspend fun getAllTpps(): Result<TppsListResponse/*List<Tpp>*/> = withContext(ioDispatcher) {
        val call = tppsService.listTpps("BudgetBakers") // TODO: get filter parameters from UI
        call.enqueue(object: Callback<TppsListResponse/*List<Tpp>*/> {

            override fun onResponse(call: Call<TppsListResponse/*List<Tpp>*/>, response: Response<TppsListResponse/*List<Tpp>*/>) {
                if (response.isSuccessful()) {
                    //var tppsList : List<Tpp>
                    tppsList = response.body()?.tppsList!!
                    tppsList.forEach { tpp ->
                        System.out.println("Insert/Update tpp: " + tpp.getTitle() + " into database")

                        runBlocking<Unit> {
                            if (tppsDao.getTppById(tpp.getId()) == null) {
                                tppsDao.insertTpp(tpp.tppEntity)
                            } else {
                                tppsDao.updateTpp(tpp.tppEntity)
                            }
                        }
                    }
                } else {
                    System.out.println(response.errorBody())
                    //return@withContext  com.applego.oblog.tppwatch.data.Result.Error()
                }
            }

            override fun onFailure(call: Call<TppsListResponse/*List<Tpp>*/>, t: Throwable) {
                t.printStackTrace()
            }
        })

        return@withContext Result.Loading(Timeout())
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun filterTpps(filter: TppFilter): Result<TppsListResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**/
}
