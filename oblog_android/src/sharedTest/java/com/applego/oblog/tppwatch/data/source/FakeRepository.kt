package com.applego.oblog.tppwatch.data.source

import androidx.annotation.VisibleForTesting
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeRepository : TppsRepository {
    override suspend fun saveApp(tpp: Tpp, app: App) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun loadTppsFromLocalDatasource(orderBy: String, isAsc: Boolean): Result<List<Tpp>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        return Success(tppsServiceData.values.toList())
    }

    override suspend fun deleteApp(app: App) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateApp(tpp: Tpp, app: App) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var tppsServiceData: LinkedHashMap<String, Tpp> = LinkedHashMap()
    var tppsResponseData = TppsListResponse()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getTpp(tppId: String, forceUpdate: Boolean): Result<Tpp> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        tppsServiceData[tppId]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find tpp"))
    }

    override suspend fun getTppBlocking(tppId: String, forceUpdate: Boolean): Result<Tpp> {
        // Void
        return Error(Exception("Could not find tpp"))
    }

    override suspend fun refreshTpp(tpp: Tpp) {
        // Void
    }

    override suspend fun fetchTppsPageFromRemoteDatasource(paging: Paging): Result<TppsListResponse> {
        if (shouldReturnError) {
            paging.last = true
            return Error(Exception("Test exception"))
        }
        paging.last = true
        return Success(tppsResponseData)
    }

    override suspend fun loadTppsFromLocalDatasource(): Result<List<Tpp>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        return Success(tppsServiceData.values.toList())
    }

    override suspend fun getAllTpps(forceUpdate: Boolean): Result<List<Tpp>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        return Success(tppsServiceData.values.toList())
    }

    override suspend fun saveTpp(tpp: Tpp) {
        tppsServiceData[tpp.ebaEntity.getEntityId()] = tpp
    }

    override suspend fun setTppFollowedFlag(tpp: Tpp, followed: Boolean) {
        tppsServiceData[tpp.ebaEntity.getEntityId()]?.ebaEntity?.followed = true
    }

    override suspend fun setTppActivateFlag(tpp: Tpp, used: Boolean) {
        tpp.ebaEntity.used = used
    }

/*
    suspend fun clearFollowedTpps() {
        tppsServiceData = tppsServiceData.filterValues {
            !it.ebaEntity?.isFollowed()
        } as LinkedHashMap<String, Tpp>
    }
*/

    override suspend fun deleteTpp(tppId: String) {
        tppsServiceData.remove(tppId)
    }

    override suspend fun deleteAllTpps() {
        tppsServiceData.clear()
    }

    @VisibleForTesting
    fun addTpps(vararg tpps: Tpp) {
        for (tpp in tpps) {
            tppsServiceData[tpp.getEntityId()] = tpp
        }
    }
}
