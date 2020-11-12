package com.applego.oblog.tppwatch.data.source.remote.nca

import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.dao.NcaEntityDao
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import kotlinx.coroutines.*
import okio.Timeout
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.*

/**
 * Concrete implementation of a data source as a db.
 */
class TppsNcaDataSource internal constructor (
        private val oblogNcaService: OblogNcaService,
        private val ncaEntityDao: NcaEntityDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteTppDataSource {

    var theApiKey : ApiKey = ApiKey("T11NOL41x0L7Cn4OAc1FNQogHAcpWvQA ")

    override suspend fun getAllTpps(): Result<TppsListResponse> = withContext(ioDispatcher) {
        var paging = Paging(10, 1, 10, true)

        launch {
            while (!paging.last) {
                paging.page +=1
                var result = loadTppsPage("", "", paging)
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

    override suspend fun getTpps(paging: Paging): Result<TppsListResponse> {
        TODO("Not yet implemented")
    }

    private fun loadTppsPage(country: String, tppName: String, paging: Paging): Result<Paging> {
        val call = oblogNcaService.listTpps(theApiKey.apiKey, country, tppName, paging.page, paging.size, paging.sortBy)
        var response: Response<TppsListResponse>?
        try {
            response = call.execute()

            if (response.isSuccessful()) {
                val tppsListResponse = response.body()!!
                Timber.d("tppsList=" + tppsListResponse.tppsList)
                tppsListResponse.tppsList?.forEach { tpp ->
                    System.out.println("Insert/Update tpp: " + tpp.ebaEntity.getEntityName() + " into database")

                    runBlocking<Unit> {
                        if (ncaEntityDao.getNcaEntityById(tpp.ebaEntity.getEntityId()) == null) {
                            ncaEntityDao.insertNcaEntity(tpp.ncaEntity)
                        } else {
                            ncaEntityDao.updateNcaEntity(tpp.ncaEntity)
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

    override suspend fun getTppById(country: String, tppId: String): Result<Tpp> {
        val paging = Paging()

        val call = oblogNcaService.findById(theApiKey.apiKey,country, tppId.toString(), paging.page, paging.size, paging.sortBy)
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
                        // Multiple entities matched by NCA entityID - mess to be solved
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

    suspend fun updateTppEntity(ncaTpp: Tpp) : Tpp {
        val ncaEntity = ncaTpp.ncaEntity
        val dbEntity = ncaEntityDao.getNcaEntityById(ncaEntity.getEntityId())
        if (dbEntity == null) {
            ncaEntityDao.insertNcaEntity(ncaEntity)
        } else {
            dbEntity._description = ncaEntity._description
            dbEntity._entityName = ncaEntity._entityName
            dbEntity._ebaEntityVersion = ncaEntity._ebaEntityVersion
            //dbEntity._ebaPassport = ncaEntity._ebaPassport
            //dbEntity._status = ncaEntity._status
            ncaEntityDao.updateNcaEntity(dbEntity)
        }

        return ncaTpp
    }

    override suspend fun getTppByName(country: String, tppName: String): Result<List<Tpp>> {

        val paging = Paging()

        val call = oblogNcaService.findByName(theApiKey.apiKey,country, tppName, paging.page, paging.size, paging.sortBy)
        var response: Response<List<Tpp>>?
        try {
            response = call.execute()
            var theTpp: Tpp?

            if (response.isSuccessful()) {
                val tppList = response.body()!!

                theTpp = tppList.firstOrNull()
                if (theTpp != null) {
                    val ncaEntity = theTpp.ncaEntity
                    Timber.d("tppsList=" + ncaEntity)
                    if (ncaEntityDao.getNcaEntityById(ncaEntity.getEntityId()) == null) {
                        ncaEntityDao.insertNcaEntity(ncaEntity)
                    } else {

                        ncaEntityDao.updateNcaEntity(ncaEntity)
                    }
                    return Result.Success(tppList)
                }
                return Result.Warn("Tpp was not found in NCA registry", "")
            } else {
                return Result.Warn(response.code().toString(),  response.errorBody().toString())
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
    }

    override suspend fun getTppByNameExact(country: String, tppName: String, tppId: String): Result<Tpp> {

        val result: Result<List<Tpp>> = getTppByName(country, tppName)
        if (result is Result.Success) {
            val tpp: Optional<Tpp> = result.data.stream().filter { it.getEntityId().equals(tppId) }.findFirst()
            if (tpp.isPresent) {
                return Result.Success(tpp.get())
            }
        }
        return Result.Error(Exception("Tpp not found"))
    }
}
