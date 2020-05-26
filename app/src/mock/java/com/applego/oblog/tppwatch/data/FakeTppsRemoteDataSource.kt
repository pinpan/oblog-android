package com.applego.oblog.tppwatch.data

import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.local.Tpp
import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeTppsRemoteDataSource : LocalTppDataSource {

    private var TPPS_SERVICE_DATA: LinkedHashMap<String, Tpp> = LinkedHashMap()

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        TPPS_SERVICE_DATA[tppId]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find tpp"))
    }

    override suspend fun getTpps(): Result<List<Tpp>> {
        return Success(TPPS_SERVICE_DATA.values.toList())
    }

    override suspend fun saveTpp(tpp: Tpp) {
        TPPS_SERVICE_DATA[tpp.id] = tpp
    }

    override suspend fun unfollowTpp(tpp: Tpp) {
        val followedTpp = Tpp(tpp.entityCode, tpp.title, tpp.description, true, tpp.id)
        TPPS_SERVICE_DATA[tpp.id] = followedTpp
    }

    override suspend fun unfollowTpp(tppId: String) {
        // Not required for the remote data source.
    }

    override suspend fun activateTpp(tpp: Tpp) {
        val usedTpp = Tpp(tpp.entityCode, tpp.title, tpp.description, false, tpp.id)
        TPPS_SERVICE_DATA[tpp.id] = usedTpp
    }

    override suspend fun activateTpp(tppId: String) {
        // Not required for the remote data source.
    }

    override suspend fun clearFollowedTpps() {
        TPPS_SERVICE_DATA = TPPS_SERVICE_DATA.filterValues {
            !it.isFollowed
        } as LinkedHashMap<String, Tpp>
    }

    override suspend fun deleteTpp(tppId: String) {
        TPPS_SERVICE_DATA.remove(tppId)
    }

    override suspend fun deleteAllTpps() {
        TPPS_SERVICE_DATA.clear()
    }
}
