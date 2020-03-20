package com.applego.oblog.tppwatch.data.source.remote.nca

import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.data.Paging
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.TppsDao
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import kotlinx.coroutines.*
import okio.Timeout
import retrofit2.Response
import timber.log.Timber
import java.io.IOException


/**
 * Concrete implementation of a data source as a db.
 */
class TppsNcaDataSource internal constructor (
        private val tppsService: OblogNcaService,
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteTppDataSource {

    var theApiKey : ApiKey = ApiKey("GaW42ue9mRsgvlL0eIrrD6biU1tlpr8Y")

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

    private fun loadTppsPage(country: String, tppName: String, paging: Paging): Result<Paging> {
        val call = tppsService.listTpps(theApiKey.apiKey, country, tppName, paging.page, paging.size, paging.sortBy)
        var response: Response<TppsListResponse>?
        try {
            response = call.execute()

            if (response.isSuccessful()) {
                val tppsListResponse = response.body()!!
                Timber.d("tppsList=" + tppsListResponse.tppsList)
                tppsListResponse.tppsList?.forEach { tpp ->
                    System.out.println("Insert/Update tpp: " + tpp.tppEntity.getEntityName() + " into database")

                    runBlocking<Unit> {
                        if (tppsDao.getTppByEntityCode(tpp.tppEntity.getEntityCode()) == null) {
                            tppsDao.insertTpp(tpp.tppEntity)
                        } else {
                            tppsDao.updateTpp(tpp.tppEntity)
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
        val paging =  Paging()

        val call = tppsService.findById(theApiKey.apiKey,country, tppId.toString(), paging.page, paging.size, paging.sortBy)
        var response: Response<List<Tpp>>?
        try {
            response = call.execute()
            var theTpp: Tpp? = null
            if (response.isSuccessful()) {
                val tppList = response.body()!!
                theTpp = tppList[0]
                val tppEntity = theTpp.tppEntity
                Timber.d("tppsList=" + tppEntity)
                if (tppsDao.getTppByEntityCode(tppEntity.getEntityCode()) == null) {
                    tppsDao.insertTpp(tppEntity)
                } else {
                    tppsDao.updateTpp(tppEntity)
                }
                return Result.Success(theTpp)
            } else {
                return Result.Warn(response.code().toString(),  response.errorBody().toString())
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
    }

    override suspend fun getTppByName(country: String, tppName: String): Result<Tpp> {

        val paging =  Paging()

        val call = tppsService.findByName(theApiKey.apiKey,country, tppName, paging.page, paging.size, paging.sortBy)
        var response: Response<List<Tpp>>?
        try {
            response = call.execute()
            var theTpp: Tpp? = null

            if (response.isSuccessful()) {
                val tppList = response.body()!!
                theTpp = tppList[0]
                val tppEntity = theTpp.tppEntity
                Timber.d("tppsList=" + tppEntity)
                if (tppsDao.getTppByEntityCode(tppEntity.getEntityCode()) == null) {
                    tppsDao.insertTpp(tppEntity)
                } else {
                    tppsDao.updateTpp(tppEntity)
                }
                return Result.Success(theTpp)
            } else {
                return Result.Warn(response.code().toString(),  response.errorBody().toString())
            }
        } catch (ioe: IOException) {
            Timber.e(ioe, "IOException caught: %s", ioe.message)
            return Result.Error(ioe)
        }
    }

    override suspend fun filterTpps(filter: TppFilter): Result<TppsListResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
