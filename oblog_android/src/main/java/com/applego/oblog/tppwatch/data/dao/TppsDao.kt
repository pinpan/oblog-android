package com.applego.oblog.tppwatch.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.EbaEntity

/**
 * Data Access Object for the tpps table.
 */
@Dao
interface TppsDao {

    /**
     * Select all tpps from the tpps table.
     *
     * @return all tpps.
     */
    @Query("SELECT * FROM Tpps order by entityName DESC")
    suspend fun getAllTppEntities(): List<EbaEntity>

    @RawQuery
    suspend fun getAllTppEntitiesRaw(sortQuery: SupportSQLiteQuery): List<EbaEntity>

    /**
     * Select all tpps from the tpps table.
     *
     * @return all tpps.
     */
    @Query("SELECT * FROM Tpps order by CASE WHEN :isAsc = 1 THEN :orderBy END ASC, CASE WHEN :isAsc = 0 THEN :orderBy END DESC")
    //CASE WHEN :isAsc = 1 THEN :orderBy END ASC, CASE WHEN :isAsc = 0 THEN :orderBy END DESC
    suspend fun getAllTppEntitiesOrdered(orderBy: String, isAsc: Boolean): List<EbaEntity>

    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM Tpps WHERE db_id = :tppId order by followed DESC")
    /*suspend */fun getTppEntityByDbId(tppId: String): EbaEntity?

    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM Tpps WHERE globalUrn = :globalUrn")
    suspend fun getTppEntityByGlobalUrn(globalUrn: String): EbaEntity?

    /**
     * Select a tpp by EBA entityCode (provided by OBLOG backend).
     *
     * @param entityCode the EBA  tpp entityCode.
     * @return the tpp with entityCode.
     */
    @Query("SELECT * FROM Tpps WHERE entityCode = :entityCode and codeType = :entityCodeType")
    suspend fun getTppEntityByCode(entityCode: String, entityCodeType: String): EbaEntity?

    /**
     * Select a tpp by EBA entityCode (provided by OBLOG backend).
     *
     * @param entityCode the EBA  tpp entityCode.
     * @return the tpp with entityCode.
     */
    @Query("SELECT * FROM Tpps WHERE entityCode = :entityCode and codeType = :entityCodeType and revoked = :isRevoked")
    suspend fun getActiveOrRevokedTppEntityByCode(entityCode: String, entityCodeType: String, isRevoked: Boolean = false): EbaEntity?

    /**
     * Select a tpp by NCA entityId (provided by OBLOG backend).
     *
     * @param entityId the NCA tpp entityId.
     * @return the tpp with entityId.
     */
    @Query("SELECT * FROM Tpps WHERE entityId = :entityId")
    suspend fun getTppEntityByEntityId(entityId: String): EbaEntity?

    /**
     * Insert or Update an EbaEntity in the database. If the EbaEntity already exists, replace it.
     *
     * @param ebaEntity the EbaEntity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEbaEntity(ebaEntity: EbaEntity)

    /**
     * Update an EbaEntity.
     *
     * @param ebaEntity an EbaEntity to be updated
     * @return the number of EbaEntity-s updated. This should always be 1.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateEbaEntity(ebaEntity: EbaEntity): Int

    /**
     * Update the followed status of a tpp
     *
     * @param tppId    id of the tpp
     * @param followed status to be updated
     */
    @Query("UPDATE tpps SET followed = :followed WHERE db_id = :tppId")
    suspend fun updateFollowed(tppId: String, followed: Boolean)

    /**
     * Update the used status of a tpp
     *
     * @param tppId  id of the tpp
     * @param used status to be updated
     */
    @Query("UPDATE tpps SET used = :used WHERE db_id = :tppId")
    suspend fun updateUsed(tppId: String, used: Boolean)

    /**
     * Delete a tpp by id.
     *
     * @return the number of tpps deleted. This should always be 1.
     */
    @Query("DELETE FROM Tpps WHERE db_id = :tppId")
    suspend fun deleteTppEntityByDbId(tppId: String): Int

    /**
     * Delete all tpps.
     */
    @Query("DELETE FROM Tpps")
    suspend fun deleteTpps()

    /**
     * Delete all followed tpps from the table.
     *
     * @return the number of tpps deleted.
     */
    @Query("DELETE FROM Tpps WHERE followed = 1")
    suspend fun deleteFollowedTppsEntities(): Int

    /*@Query("SELECT * FROM Tpps WHERE country = :country")
    fun getTppEntitiesByCountry(country: String): List<EbaEntity>*/



    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM apps WHERE tppId = :tppId")
    /*suspend */fun getTppEntityAppsByDbId(tppId: String): List<App>


    /**
     * Select all apps from the apps table.
     *
     * @return all apps.
     */
    @Query("SELECT * FROM Apps")
    suspend fun getApps(): List<App>

    /**
     * Select a app by id.
     *
     * @param appId the app id.
     * @return the App with appId.
     */
    @Query("SELECT * FROM Apps WHERE id = :appId")
    suspend fun getAppById(appId: String): App?

    /**
     * Select a app by id.
     *
     * @param appId the app id.
     * @return the App with appId.
     */
    @Query("SELECT * FROM Apps WHERE name like :appName and tppID=:tppId")
    suspend fun getAppByName(appName: String, tppId: String): App?

    /**
     * Insert a App in the database. If the App already exists, replace it.
     *
     * @param App - the App to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: App)

    /**
     * Update an app
     *
     * @param app App to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateApp(аpp: App) : Int

    /**
     * Update an app
     *
     * @param app App to be updated
     */
    @Delete
    suspend fun deleteApp(аpp: App)
}
