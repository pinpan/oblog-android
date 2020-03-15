package com.applego.oblog.tppwatch.data.source.local

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class TppsDaoDataSource internal constructor(
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalTppDataSource {

    override suspend fun getTpps(filter: TppsFilter): Result<List<Tpp>> = withContext(ioDispatcher) {
        var tpps = ArrayList<Tpp>()
        try {
            var tppEntities : List<TppEntity>
            if (isOnlyCountry(filter)) {
                tppEntities = tppsDao.getTppsByCountry(filter.country)
            } else {
                tppEntities = tppsDao.getTpps()
            }
            tppEntities.forEach {tppEntity ->
                tpps.add(Tpp(tppEntity))}
        } catch (e: Exception) {
            Error(e)
        }
        return@withContext Success(tpps)
    }

    private fun isOnlyCountry(filter: TppsFilter): Boolean {
        return (!filter.country.isNullOrBlank() && filter.pasportedTo.isNullOrEmpty() && filter.services.isNullOrEmpty() && filter.tppName.isNullOrBlank());
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> = withContext(ioDispatcher) {
        try {
            val tppEntity = tppsDao.getTppById(tppId)
            if (tppEntity != null) {
                return@withContext Success(Tpp(tppEntity))
            } else {
                return@withContext Error(Exception("TppEntity not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun saveTpp(tpp: Tpp) = withContext(ioDispatcher) {
        tppsDao.insertTpp(tpp.tppEntity)
    }

    override suspend fun udateFollowing(tpp: Tpp, follow: Boolean) = withContext(ioDispatcher) {
        tppsDao.updateFollowed(tpp.tppEntity.getId(), follow)
    }

    override suspend fun setTppActivateFlag(tppId: String, active: Boolean)  = withContext(ioDispatcher) {
        tppsDao.updateActive(tppId, active)
    }

    suspend fun clearFollowedTpps() = withContext<Unit>(ioDispatcher) {
        tppsDao.deleteFollowedTpps()
    }

    override suspend fun deleteAllTpps() = withContext(ioDispatcher) {
        tppsDao.deleteTpps()
    }

    override suspend fun deleteTpp(tppId: String) = withContext<Unit>(ioDispatcher) {
        tppsDao.deleteTppById(tppId)
    }
}
