package com.applego.oblog.tppwatch.data.source.remote.nca

import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.dao.NcaEntityDao
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.source.remote.ListResponse
import com.applego.oblog.tppwatch.data.source.remote.NcaEntitiesListResponse
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
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
) : RemoteTppDataSource<NcaEntity> {

    var theApiKey : ApiKey = ApiKey("T11NOL41x0L7Cn4OAc1FNQogHAcpWvQA ")

    override suspend fun getAllEntities(): Result<ListResponse<NcaEntity>> = withContext(ioDispatcher) {
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

    override suspend fun getEntitiesPage(paging: Paging): Result<ListResponse<NcaEntity>> {
        TODO("Not yet implemented")
    }

    private fun loadTppsPage(country: String, tppName: String, paging: Paging): Result<Paging> {
        val call = oblogNcaService.listTpps(theApiKey.apiKey, country, tppName, paging.page, paging.size, paging.sortBy)
        var response: Response<NcaEntitiesListResponse>?
        try {
            response = call.execute()

            if (response.isSuccessful()) {
                val ncaEntitiesListResponse = response.body()!!
                Timber.d("tppsList=" + ncaEntitiesListResponse.aList)
                ncaEntitiesListResponse.aList?.forEach { entity ->
                    Timber.d("Insert/Update NCA Entity: " + entity.getEntityName() + " into database")

                    runBlocking<Unit> {
                        if (ncaEntityDao.getNcaEntityById(entity.getEntityId()) == null) {
                            ncaEntityDao.insertNcaEntity(entity)
                        } else {
                            ncaEntityDao.updateNcaEntity(entity)
                        }
                    }
                }
                return Result.Success(ncaEntitiesListResponse.paging)
            } else {
                return Result.Warn(response.code().toString(),  response.errorBody().toString())
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
    }

    override suspend fun getEntityById(country: String, tppId: String): Result<NcaEntity> {
        val paging = Paging()

        val call = oblogNcaService.findById(theApiKey.apiKey,country, tppId, paging.page, paging.size, paging.sortBy)
        try {
            var response: Response<NcaEntitiesListResponse> = call.execute()
            if (response.isSuccessful()) {
                if (response.body()?.aList.isNullOrEmpty()) {
                    return Result.Warn("HTTP response body is empty", "HTTP response code: $response.code(), response body: $response.body()")
                } else {
                    val ncaEntityList = response.body()?.aList
                    Timber.d("ncaEntitiesList=" + ncaEntityList)

                    var ncaEntity: NcaEntity?
                    if (!ncaEntityList.isNullOrEmpty()) {
                        if (ncaEntityList?.size > 1) {
                            // Multiple entities matched by NCA entityID - mess to be solved
                            return Result.Warn("HTTP response returned multiple entities", "HTTP response code: $response.code(), response body: $response.body()")
                        }
                        ncaEntity = updateNcaEntity(ncaEntityList[0])
                        return Result.Success(ncaEntity)
                    }
                    return Result.Warn("EMPTY", "Couldn't parse a NcaEntitiy from remote rest response")
                }
            } else {
                return Result.Error(Exception("HTTP response with code: $response.code().toString() and error body: $response.errorBody().toString()"))
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
    }

    suspend fun updateNcaEntity(ncaEntity: NcaEntity) : NcaEntity {
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

        return dbEntity!!
    }

    override suspend fun getEntityByName(country: String, tppName: String): Result<List<NcaEntity>> {
        val paging = Paging()

        val call = oblogNcaService.findByName(theApiKey.apiKey,country, tppName, paging.page, paging.size, paging.sortBy)
        try {
            var response: Response<List<NcaEntity>> = call.execute()

            if (response.isSuccessful()) {
                val ncaEntityList = response.body()!!
                var ncaEntity: NcaEntity? = ncaEntityList.firstOrNull()
                if (ncaEntity != null) {
                    //val ncaEntity = theTpp.ncaEntity
                    Timber.d("ncaEntity = " + ncaEntity)

                    if (ncaEntityDao.getNcaEntityById(ncaEntity.getEntityId()) == null) {
                        ncaEntityDao.insertNcaEntity(ncaEntity)
                    } else {

                        ncaEntityDao.updateNcaEntity(ncaEntity)
                    }
                    return Result.Success(ncaEntityList)
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

    override suspend fun getEntityByNameExact(country: String, entityName: String, entityId: String): Result<NcaEntity> {

        val result: Result<List<NcaEntity>> = getEntityByName(country, entityName)
        if (result is Result.Success) {
            val tpp: Optional<NcaEntity> = result.data.stream().filter { it.getEntityId().equals(entityId) }.findFirst()
            if (tpp.isPresent) {
                return Result.Success(tpp.get())
            }
        }
        return Result.Error(Exception("Tpp not found"))
    }
}
