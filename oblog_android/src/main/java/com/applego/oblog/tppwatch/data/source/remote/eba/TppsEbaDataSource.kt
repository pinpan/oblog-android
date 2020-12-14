package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.BuildConfig
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.dao.EbaEntityDao
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaService
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.*
import kotlinx.coroutines.*
import okio.Timeout
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Concrete implementation of a data source as a db.
 */
class TppsEbaDataSource internal constructor (
        private val tppsService: OblogEbaService,
        private val ebaEntityDao: EbaEntityDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteTppDataSource<EbaEntity> {

    override suspend fun getAllEntities(): Result<ListResponse<EbaEntity>> {
        var allFetchedTpps = ArrayList<EbaEntity>()
        runBlocking {
            var paging = Paging(100, 1, 0, true)

            while (!paging.last) {
                var result = loadTppsPage(paging)
                when (result) {
                    is Result.Success -> {
                        allFetchedTpps.addAll(result.data.aList) //.stream().map{t -> t}.collect(Collectors.toList())
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

        return Result.Success(ListResponse(allFetchedTpps))
    }

    override suspend fun getEntitiesPage(paging : Paging): Result<ListResponse<EbaEntity>> = withContext(ioDispatcher) {
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
    override suspend fun getEntityById(country: String, tppId: String): Result<EbaEntity> {
        val paging = Paging()

        val call = tppsService.findById(OblogEbaService.theApiKey.apiKey, tppId.toString(), paging.page, paging.size, paging.sortBy)
        var response: Response<List<Tpp>>?
        try {
            response = call.execute()
            var theTpp: EbaEntity
            if (response.isSuccessful()) {
                if (response.body().isNullOrEmpty()) {
                    return Result.Warn("HTTP response body is empty", "HTTP response code: $response.code(), response body: $response.body()")
                } else {
                    val ebaEntityList = response.body()
                    Timber.d("tppsList=" + ebaEntityList)
                    if (ebaEntityList?.size == 1) {
                        theTpp = updateTppEntity(ebaEntityList[0].ebaEntity)
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

    suspend fun updateTppEntity(ebaEntity: EbaEntity) : EbaEntity {
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

        return dbEntity!!
    }

    override suspend fun getEntityByName(country: String, tppName: String): Result<List<EbaEntity>> {
        if (tppName.isNullOrBlank()) {
            Result.Warn("TPP Not Found", "Cannot find a TPP with empty ID")
        }
        return Result.Loading(Timeout().timeout(100, TimeUnit.MILLISECONDS));
    }

    override suspend fun getEntityByNameExact(country: String, tppName: String, tppId: String): Result<EbaEntity> {

        val ebaEntityResult: Result<List<EbaEntity>> = getEntityByName(country, tppName)
        if (ebaEntityResult is Result.Success) {
            val tpp: Optional<EbaEntity> = ebaEntityResult.data.stream().filter { it.getEntityId().equals(tppId) }.findFirst()
            if (tpp.isPresent) {
                return Result.Success(tpp.get())
            }
        }
        return Result.Error(Exception("Tpp not found"))
    }

    private fun loadTppsPage(paging: Paging): Result<ListResponse<EbaEntity>> {
        val call = tppsService.listTppsByName(OblogEbaService.theApiKey.apiKey,"", paging.page, paging.size, paging.sortBy)
        var response: Response<TppsListResponse>?
        try {
            response = call.execute()

            if (response.isSuccessful()) {
                val tppsList = response.body()?.aList ?: emptyList()
                val entitiesList = ArrayList<EbaEntity>() //tppsList.stream().map (Tpp::ebaEntity).collect(Collectors.toList())
                for (tpp in tppsList) {
                    entitiesList.add(tpp.ebaEntity)
                }
                val entitiesListResponse = ListResponse(entitiesList, response.body()?.paging ?: Paging.Builder().last(true).toPaging())
                // TODO: Investigate why this code was blocking the whole program: response.body().aList.stream().map { t -> t.ebaEntity }.collect(Collectors.toList())
                Timber.d("ebaEntitiesList=" + entitiesListResponse?.aList)

                if (entitiesListResponse?.paging != null) {
                    return Result.Success(entitiesListResponse)
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
