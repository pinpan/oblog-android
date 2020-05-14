package com.applego.oblog.tppwatch.data.source.local

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.dao.TppsDao
import com.applego.oblog.tppwatch.data.model.*
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
            var ebaEntities : List<EbaEntity>
            if (isOnlyCountry(filter)) {
                ebaEntities = tppsDao.getTppEntitiesByCountry(filter.country)
            } else {
                ebaEntities = tppsDao.getAllTppEntities()
            }
            ebaEntities.forEach { tppEntity ->
                tpps.add(Tpp(tppEntity, NcaEntity()))}
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
            val tppEntity = tppsDao.getTppEntityByDbId(tppId)
            if (tppEntity != null) {
                val tpp = Tpp(tppEntity, NcaEntity())

                val apps = tppsDao.getTppEntityAppsByDbId(tppId)
                if (apps != null) {
                    tpp.appsPortfolio.appsList = apps as ArrayList
                }
                return@withContext Success(tpp)
            } else {
                return@withContext Error(Exception("EbaEntity not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun saveTpp(tpp: Tpp) = withContext(ioDispatcher) {
        tppsDao.insertTppEntity(tpp.ebaEntity)
    }

    override suspend fun saveАpp(аpp: App) = withContext(ioDispatcher) {
        tppsDao.insertApp(аpp)
    }

    override suspend fun udateFollowing(tpp: Tpp, follow: Boolean) = withContext(ioDispatcher) {
        tppsDao.updateFollowed(tpp.ebaEntity.getId(), follow)
    }

    override suspend fun setTppActivateFlag(tppId: String, used: Boolean)  = withContext(ioDispatcher) {
        tppsDao.updateUsed(tppId, used)
    }

    suspend fun clearFollowedTpps() = withContext<Unit>(ioDispatcher) {
        tppsDao.deleteFollowedTppsEntities()
    }

    override suspend fun deleteAllTpps() = withContext(ioDispatcher) {
        tppsDao.deleteTpps()
    }

    override suspend fun deleteTpp(tppId: String) = withContext<Unit>(ioDispatcher) {
        tppsDao.deleteTppEntityByDbId(tppId)
    }
}
