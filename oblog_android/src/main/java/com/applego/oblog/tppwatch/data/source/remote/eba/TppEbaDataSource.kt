package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.dao.TppsDao
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import kotlinx.coroutines.*
import okio.Timeout
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit


/**
 * Concrete implementation of a data source as a db.
 */
class TppEbaDataSource internal constructor (
        private val tppsService: OblogEbaService,
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteTppDataSource {

    // TODO: Get the String from config per Base URL
    var theApiKey : ApiKey = ApiKey("T11NOL41x0L7Cn4OAc1FNQogHAcpWvQA") // Old key "MyhCyIKQ0IlIG5dFVk6sjXcG2aHhFbj0", "2Dvgcj0W7sinv0mqtwm2CSQuYYsW79xb", "GaW42ue9mRsgvlL0eIrrD6biU1tlpr8Y"

    override suspend fun getAllTpps(): Result<TppsListResponse> {
        var allFetchedTpps = ArrayList<Tpp>()
        runBlocking {
            var paging = Paging(100, 1, 0, true)

            while (!paging.last) {
                var result = loadTppsPage(paging)
                when (result) {
                    is Result.Success -> {
                        allFetchedTpps.addAll(result.data.tppsList)
                        paging = result.data.paging
                        paging.page +=1
                        if (paging.totalPages == paging.page) {
                            paging.last = true
                        }
                    }
                    is Result.Error -> {
                        // TODO: IMplement proper Error handing. For now, jump out
                        paging.last = true
                    }
                }
            }
        }

        return Result.Success(TppsListResponse(allFetchedTpps))
    }

    override suspend fun getTpps(paging : Paging): Result<TppsListResponse> = withContext(ioDispatcher) {
        var result = loadTppsPage(paging)
        when (result) {
            is Result.Success -> {
                result.data.paging.page +=1
                if (paging.totalPages == paging.page) {
                    paging.last = true
                }
            }

            is Result.Error -> {
                // TODO: IMplement proper Error handing. For now, jump out
                paging.last = true
            }
        }

        return@withContext result
    }

    // TODO: Refactor to single implementation <- This implementatiomn is exactly the same as for NcaDataSource
    override suspend fun getTppById(country: String, tppId: String): Result<Tpp> {
        val paging = Paging()

        val call = tppsService.findById(theApiKey.apiKey, tppId.toString(), paging.page, paging.size, paging.sortBy)
        var response: Response<List<Tpp>>?
        try {
            response = call.execute()
            var theTpp: Tpp?
            if (response.isSuccessful()) {
                if (response.body().isNullOrEmpty()) {
                    return Result.Warn("HTTP response body is empty", "HTTP response code: $response.code(), response body: $response.body()")
                } else {
                    val tppList = response.body()
                    Timber.d("tppsList=" + tppList)
                    if (tppList?.size == 1) {
                        theTpp = updateTppEntity(tppList[0])
                    } else {
                        // Multiple entities matched by EBA entityCode - mess to be solved
                        return Result.Warn("HTTP response returned multiple entities", "HTTP response code: $response.code(), response body: $response.body()")
                    }
                    return Result.Success(theTpp)
                }
            } else {
                return Result.Error(Exception("HTTP response with code: $response.code().toString() and error body: $response.errorBody().toString()"))
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
    }

    suspend fun updateTppEntity(ebaTpp: Tpp) : Tpp {
        val ebaEntity = ebaTpp.ebaEntity
        val dbEntity = tppsDao.getTppEntityByCode(ebaEntity.getEntityCode(), ebaEntity.ebaProperties.codeType)
        if (dbEntity == null) {
            tppsDao.insertEbaEntity(ebaEntity)
        } else {
            dbEntity._description = ebaEntity._description
            dbEntity._entityName = ebaEntity._entityName
            dbEntity._ebaEntityVersion = ebaEntity._ebaEntityVersion
            dbEntity._ebaPassport = ebaEntity._ebaPassport
            dbEntity._status = ebaEntity._status
            tppsDao.updateEbaEntity(dbEntity)
        }

        return ebaTpp
    }

    override suspend fun getTppByName(country: String, tppName: String): Result<Tpp> {
        if (tppName.isNullOrBlank()) {
            Result.Warn("TPP Not Found", "Cannot find a TPP with empty ID")
        }
        return Result.Loading(Timeout().timeout(100, TimeUnit.MILLISECONDS));
    }

    private fun loadTppsPage(paging: Paging): Result<TppsListResponse> {
        val call = tppsService.listTppsByName(theApiKey.apiKey,"", paging.page, paging.size, paging.sortBy)
        var response: Response<TppsListResponse>?
        try { 
            response = call.execute()

            if (response.isSuccessful()) {
                val tppsListResponse = response.body()
                Timber.d("tppsList=" + tppsListResponse?.tppsList)

                if (tppsListResponse?.paging != null) {
                    return Result.Success(tppsListResponse)
                } else  {
                    return Result.Error(Exception("Rest call returned no data"))
                }
            } else {
                when (getErrorCodeCategory(response.code())) {
                    400 -> {
                        Timber.w("Update of TPPs directory was not successfull. Client error: code = %s, body = %s", response.code().toString(), response.body())
                        return Result.Error(Exception(response.errorBody().toString()))
                    }
                    500 -> {
                        Timber.w("Update of TPPs directory was not successfull. Server error: code = %s, body = %s", response.code().toString(), response.body())
                        return Result.Error(Exception(response.errorBody().toString()))
                    }
                    else -> {
                        Timber.w("Update of TPPs directory was not successfull. Server error: code = %s, body = %s", response.code().toString(), response.body())
                        return Result.Warn(response.code().toString(), response.body().toString())
                    }
                }
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
    }

    private fun getErrorCodeCategory(code: Int): Any {
        return (code/100)*100;
    }
}
