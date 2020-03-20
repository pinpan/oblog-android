package com.applego.oblog.tppwatch.data.source

import androidx.annotation.VisibleForTesting
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
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
        tppsServiceData[tpp.tppEntity.getEntityId()] = tpp
    }

    override suspend fun setTppFollowedFlag(tpp: Tpp, followed: Boolean) {
        tppsServiceData[tpp.tppEntity.getEntityId()]?.tppEntity?.followed = true
    }

    override suspend fun setTppActivateFlag(tpp: Tpp, active: Boolean) {
        tpp.tppEntity.active = active
    }

/*
    suspend fun clearFollowedTpps() {
        tppsServiceData = tppsServiceData.filterValues {
            !it.tppEntity?.isFollowed()
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
