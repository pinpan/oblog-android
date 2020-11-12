package com.applego.oblog.tppwatch.data.source.local

import androidx.sqlite.db.SimpleSQLiteQuery
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.dao.EbaEntityDao
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.model.Tpp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


/**
 * Concrete implementation of a data source as a db.
 */
class TppsDaoDataSource internal constructor(
        private val ebaEntityDao: EbaEntityDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalTppDataSource {

    override suspend fun getTpps(): Result<List<Tpp>> = withContext(ioDispatcher) {
        getTpps("followed", true)
    }

    override suspend fun getTpps(orderBy: String, isAsc: Boolean): Result<List<Tpp>> = withContext(ioDispatcher) {
        var tpps = ArrayList<Tpp>()
        try {
            val query = "SELECT * FROM Tpps ORDER BY " + orderBy + (if (isAsc) " ASC" else " DESC")
            var ebaEntities = ebaEntityDao.getAllTppEntitiesRaw(SimpleSQLiteQuery(query))
            ebaEntities.forEach { ebaEntity ->
                tpps.add(Tpp(ebaEntity, NcaEntity()))}
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
            val tppEntity = ebaEntityDao.getEbaEntityByDbId(tppId)
            if (tppEntity != null) {
                val tpp = Tpp(tppEntity, NcaEntity())

                val apps = ebaEntityDao.getTppEntityAppsByDbId(tppId)
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
                if (app.id != null) {
                    ebaEntityDao.updateApp(app)
                } else {
                    ebaEntityDao.insertApp(app)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        val isRevoked = tpp.isRevoked()
        val foundEntity = ebaEntityDao.getActiveOrRevokedEbaEntityByCode(tpp.ebaEntity.getEntityCode(), tpp.ebaEntity.ebaProperties.codeType, isRevoked)
        if (foundEntity == null) {
            ebaEntityDao.insertEbaEntity(tpp.ebaEntity)
            Timber.d("TPP with Eba Code %s was inserted in local DB.", tpp.getEntityCode())
        } else {
            tpp.ebaEntity._db_id = foundEntity._db_id
            val updatedNumber = ebaEntityDao.updateEbaEntity(tpp.ebaEntity)
            if (updatedNumber != 1) {
                Timber.w("Update of TPP with ID %s was not successfull.", tpp.getEntityId())
            } else {
                Timber.d("TPP with Eba Code %s was updated.", tpp.getEntityCode())
            }
        }
    }

    override suspend fun deleteАpp(аpp: App) {
        ebaEntityDao.deleteApp(аpp)
    }

    override suspend fun saveАpp(аpp: App) {
        val foundApp = ebaEntityDao.getAppByName(аpp.name, аpp.tppId)
        if (foundApp != null) {
            аpp.id = foundApp.id
            if (1 != ebaEntityDao.updateApp(аpp) ) {
                Timber.w("Couldn't update aplication: %s from TPP: %s", аpp.id, аpp.tppId)
            }
        } else {
            ebaEntityDao.insertApp(аpp)
        }
    }

    override suspend fun updateFollowing(tpp: Tpp, follow: Boolean) = withContext(ioDispatcher) {
        ebaEntityDao.updateFollowed(tpp.ebaEntity.getId(), follow)
    }

    override suspend fun setTppActivateFlag(tppId: String, used: Boolean)  = withContext(ioDispatcher) {
        ebaEntityDao.updateUsed(tppId, used)
    }

    suspend fun clearFollowedTpps() = withContext<Unit>(ioDispatcher) {
        ebaEntityDao.deleteFollowedTppsEntities()
    }

    override suspend fun deleteAllTpps() = withContext(ioDispatcher) {
        ebaEntityDao.deleteTpps()
    }

    override suspend fun deleteTpp(tppId: String) = withContext<Unit>(ioDispatcher) {
        ebaEntityDao.deleteTppEntityByDbId(tppId)
    }
}
