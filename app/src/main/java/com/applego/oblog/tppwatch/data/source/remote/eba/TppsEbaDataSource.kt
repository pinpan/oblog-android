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
package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.Paging
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.TppsDao
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import kotlinx.coroutines.*
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Concrete implementation of a data source as a db.
 */
class TppsEbaDataSource internal constructor (
        private val tppsService: OblogEbaService,
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteTppDataSource {

    override suspend fun getAllTpps(): Result<TppsListResponse> = withContext(ioDispatcher) {
        var paging = Paging(10, 1, 10, true)

        launch {
            while (!paging.last) {
                paging.page +=1
                var result = loadTppsPage(paging)
                if (result is Result.Success) {
                    paging = result.data
                }
            }
        }

        return@withContext Result.Loading(Timeout())
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        if (tppId == "") {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        return Result.Loading(Timeout().timeout(100, TimeUnit.MILLISECONDS));
    }

    override suspend fun filterTpps(filter: TppFilter): Result<TppsListResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun loadTppsPage(paging: Paging): Result<Paging> {
        val call = tppsService.listTpps("", paging.page, paging.size, paging.sortBy)
        var response: Response<TppsListResponse>?
        try {
            response = call.execute()

            //enqueue(object: Callback<TppsListResponse> {

             //   override fun onResponse(call: Call<TppsListResponse>, response: Response<TppsListResponse>) {
            if (response.isSuccessful()) {
                val tppsListResponse = response.body()!!
                Timber.d("tppsList=" + tppsListResponse.tppsList)
                tppsListResponse.tppsList?.forEach { tpp ->
                    System.out.println("Insert/Update tpp: " + tpp.title + " into database")

                    runBlocking<Unit> {
                        if (tppsDao.getTppByEntityCode(tpp.entityCode) == null) {
                            tppsDao.insertTpp(tpp)
                        } else {
                            tppsDao.updateTpp(tpp)
                        }
                    }
                }
                return Result.Success(tppsListResponse.paging)
            } else {
                //System.out.println(response.errorBody())
                return Result.Warn(response.code().toString(),  response.errorBody().toString())
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
        // }

            //override fun onFailure(call: Call<TppsListResponse>, t: Throwable) {
            //    t.printStackTrace()
            //}
        //})
    }
    /**/
}
