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
import timber.log.Timber

/**
 * Concrete implementation of a data source as a db.
 */
class TppsDaoDataSource internal constructor(
        private val tppsDao: TppsDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalTppDataSource {

        override suspend fun getTpps(): Result<List<Tpp>> = withContext(ioDispatcher) {
        var tpps = ArrayList<Tpp>()
        try {
            val allTppEntities = tppsDao.getAllTppEntities()
            var ebaEntities = allTppEntities
            ebaEntities.forEach { ebaEntity ->
                tpps.add(Tpp(ebaEntity))}
        } catch (e: Exception) {
            Error(e)
        }
        return@withContext Success(tpps)
    }

    private fun isOnlyCountry(filter: TppsFilter): Boolean {
        return (!filter.country.isNullOrBlank() && filter.pasportedTo.isNullOrEmpty() && filter.services.isNullOrEmpty() && filter.tppName.isNullOrBlank());
    }

    override fun getTpp(tppId: String): Result<Tpp> {
        try {
            val tppEntity = tppsDao.getTppEntityByDbId(tppId)
            if (tppEntity != null) {
                val tpp = Tpp(tppEntity, NcaEntity())

                val apps = tppsDao.getTppEntityAppsByDbId(tppId)
                if ((apps != null) && !apps.isEmpty()) {
                    tpp.appsPortfolio.tppId = tppId
                    tpp.appsPortfolio.appsList = apps as ArrayList
                }
                return Success(tpp)
            } else {
                return Error(Exception("EbaEntity not found!"))
            }
        } catch (e: Exception) {
            return Error(e)
        }
    }

    override suspend fun saveTpp(tpp: Tpp) {
        val apps = tpp.appsPortfolio.appsList
        for (app in apps) {
            try {
                tppsDao.insertApp(app)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        tppsDao.insertEbaEntity(tpp.ebaEntity)
    }

    override suspend fun saveАpp(аpp: App) {
        if (аpp.id != null) {
            tppsDao.updateApp(аpp)
        } else {
            tppsDao.insertApp(аpp)
        }
    }

    override suspend fun updateFollowing(tpp: Tpp, follow: Boolean) = withContext(ioDispatcher) {
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
