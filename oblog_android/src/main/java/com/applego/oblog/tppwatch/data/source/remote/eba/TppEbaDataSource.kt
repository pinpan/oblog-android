package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.dao.EbaEntityDao
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.source.remote.EbaEntitiesListResponse
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import kotlinx.coroutines.*
import okio.Timeout
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors


/**
 * Concrete implementation of a data source as a db.
 */
class TppEbaDataSource internal constructor (
        private val tppsService: OblogEbaService,
        private val ebaEntityDao: EbaEntityDao,
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
        val dbEntity = ebaEntityDao.getEbaEntityByCode(ebaEntity.getEntityCode(), ebaEntity.ebaProperties.codeType)
        if (dbEntity == null) {
            ebaEntityDao.insertEbaEntity(ebaEntity)
        } else {
            dbEntity._description = ebaEntity._description
            dbEntity._entityName = ebaEntity._entityName
            dbEntity._ebaEntityVersion = ebaEntity._ebaEntityVersion
            dbEntity._ebaPassport = ebaEntity._ebaPassport
            dbEntity._status = ebaEntity._status
            ebaEntityDao.updateEbaEntity(dbEntity)
        }

        return ebaTpp
    }

    override suspend fun getTppByName(country: String, tppName: String): Result<List<Tpp>> {
        if (tppName.isNullOrBlank()) {
            Result.Warn("TPP Not Found", "Cannot find a TPP with empty ID")
        }
        return Result.Loading(Timeout().timeout(100, TimeUnit.MILLISECONDS));
    }

    override suspend fun getTppByNameExact(country: String, tppName: String, tppId: String): Result<Tpp> {

        val tppsResult: Result<List<Tpp>> = getTppByName(country, tppName)
        if (tppsResult is Result.Success) {
            val tpp: Optional<Tpp> = tppsResult.data.stream().filter { it.getEntityId().equals(tppId) }.findFirst()
            if (tpp.isPresent) {
                return Result.Success(tpp.get())
            }
        }
        return Result.Error(Exception("Tpp not found"))
    }

    private fun loadTppsPage(paging: Paging): Result<TppsListResponse> {
        val call = tppsService.listEbaEntitiesByName(theApiKey.apiKey,"", paging.page, paging.size, paging.sortBy)
        var response: Response<EbaEntitiesListResponse>?
        try {
            response = call.execute()

            if (response.isSuccessful()) {
                val ebaEntitiesListResponse = response.body()
                Timber.d("ebaEntitiesList=" + ebaEntitiesListResponse?.entitiesList)

                if (ebaEntitiesListResponse?.paging != null) {
                    val tppsListResponse = getTppsListResponse(ebaEntitiesListResponse)
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

    private fun getTppsListResponse(ebaEntitiesListResponse: EbaEntitiesListResponse): TppsListResponse {
        return TppsListResponse(ebaEntitiesListResponse.entitiesList.stream().map{x -> getTpp(x)}.collect(Collectors.toList()))
    }

    fun getTpp(ebaEntity: EbaEntity) : Tpp {
        return Tpp(ebaEntity, NcaEntity())
    }

    private fun getErrorCodeCategory(code: Int): Any {
        return (code/100)*100;
    }
}
