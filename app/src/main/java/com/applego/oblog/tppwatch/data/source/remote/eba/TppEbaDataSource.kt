
package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppFilter
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.dao.TppsDao
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import kotlinx.coroutines.*
import okio.Timeout
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
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
    // Old key MyhCyIKQ0IlIG5dFVk6sjXcG2aHhFbj0
    var theApiKey : ApiKey = ApiKey("GaW42ue9mRsgvlL0eIrrD6biU1tlpr8Y")

    override suspend fun getAllTpps(): Result<TppsListResponse> = withContext(ioDispatcher) {
        var paging = Paging(10, 1, 10, true)

        launch {
            while (!paging.last) {
                paging.page +=1
                var result = loadTppsPage(paging)
                when (result) {
                    is Result.Success -> {
                        paging = result.data
                    }
                    is Result.Error -> {
                        // TODO: IMplement proper Error handing. For now, jump out
                        paging.last = true
                    }
                }
            }
        }

        return@withContext Result.Loading(Timeout())
    }

    // TODO: Refactor to single implementation <- This implementatiomn is exactly the same as for NcaDataSource
    override suspend fun getTppById(country: String, tppId: String): Result<Tpp> {
        val paging = Paging()

        val call = tppsService.findById(theApiKey.apiKey, tppId.toString(), paging.page, paging.size, paging.sortBy)
        var response: Response<List<Tpp>>?
        try {
            response = call.execute()
            var theTpp: Tpp? = null
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


        return Result.Loading(Timeout().timeout(100, TimeUnit.MILLISECONDS));
    }


    suspend fun updateTppEntity(ebaTpp: Tpp) : Tpp {
        val ebaEntity = ebaTpp.ebaEntity
        val dbEntity = tppsDao.getTppEntityByCode(ebaEntity.getEntityCode(), ebaEntity.ebaProperties.codeType)
        if (dbEntity == null) {
            tppsDao.insertTppEntity(ebaEntity)
        } else {
            dbEntity._description = ebaEntity._description
            dbEntity._entityName = ebaEntity._entityName
            dbEntity._ebaEntityVersion = ebaEntity._ebaEntityVersion
            dbEntity._ebaPassport = ebaEntity._ebaPassport
            dbEntity._status = ebaEntity._status
            tppsDao.updateTppEntity(dbEntity)
        }

        return ebaTpp
    }

    override suspend fun getTppByName(country: String, tppName: String): Result<Tpp> {
        if (tppName.isNullOrBlank()) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Result.Warn("TPP Not Found", "Cannot find a TPP with empty ID")
        }
        return Result.Loading(Timeout().timeout(100, TimeUnit.MILLISECONDS));
    }

    override suspend fun filterTpps(filter: TppFilter): Result<TppsListResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun loadTppsPage(paging: Paging): Result<Paging> {
        val call = tppsService.listTppsByName(theApiKey.apiKey,"", paging.page, paging.size, paging.sortBy)
        var response: Response<TppsListResponse>?
        try {
            response = call.execute()

            //enqueue(object: Callback<TppsListResponse> {

             //   override fun onResponse(call: Call<TppsListResponse>, response: Response<TppsListResponse>) {
            if (response.isSuccessful()) {
                val tppsListResponse = response.body()!!
                Timber.d("tppsList=" + tppsListResponse.tppsList)
                tppsListResponse.tppsList?.forEach { tpp ->
                    System.out.println("Insert/Update tpp: " + tpp.ebaEntity.getEntityName() + " into database")

                    runBlocking<Unit> {
                        val foundEntity = tppsDao.getTppEntityByCode(tpp.ebaEntity.getEntityCode(), tpp.ebaEntity.ebaProperties.codeType)
                        if (foundEntity == null) {
                            tppsDao.insertTppEntity(tpp.ebaEntity)
                        } else {
                            val updatedNumber = tppsDao.updateTppEntity(tpp.ebaEntity)
                            if (updatedNumber != 1) {
                                Timber.w("Update of TPP with ID %s was not successfull.", tpp.getEntityId())
                            }
                        }
                    }
                }
                return Result.Success(tppsListResponse.paging)
            } else {
                return Result.Warn(response.code().toString(),  response.errorBody().toString())
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
    }
}
