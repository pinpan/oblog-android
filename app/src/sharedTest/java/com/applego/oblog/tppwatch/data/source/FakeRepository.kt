package com.applego.oblog.tppwatch.data.source

import androidx.annotation.VisibleForTesting
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeRepository : TppsRepository {

    var tppsServiceData: LinkedHashMap<String, Tpp> = LinkedHashMap()

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

    override suspend fun getAllTpps(forceUpdate: Boolean): Result<List<Tpp>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        return Success(tppsServiceData.values.toList())
    }

    override suspend fun filterTpps(filter: TppsFilter, forceUpdate: Boolean): Result<List<Tpp>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
