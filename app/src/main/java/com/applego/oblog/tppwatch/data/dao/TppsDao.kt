package com.applego.oblog.tppwatch.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
    @Query("SELECT * FROM Tpps order by followed DESC")
    suspend fun getAllTppEntities(): List<EbaEntity>

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
    /*suspend */fun getTppEntityByCode(entityCode: String, entityCodeType: String): EbaEntity?

    /**
     * Select a tpp by NCA entityId (provided by OBLOG backend).
     *
     * @param entityId the NCA tpp entityId.
     * @return the tpp with entityId.
     */
    @Query("SELECT * FROM Tpps WHERE entityId = :entityId")
    suspend fun getTppEntityByEntityId(entityId: String): EbaEntity?

    /**
     * Insert a ebaEntity in the database. If the ebaEntity already exists, replace it.
     *
     * @param ebaEntity the ebaEntity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTppEntity(ebaEntity: EbaEntity)

    /**
     * Update a ebaEntity.
     *
     * @param ebaEntity ebaEntity to be updated
     * @return the number of tpps updated. This should always be 1.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTppEntity(ebaEntity: EbaEntity): Int

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

    @Query("SELECT * FROM Tpps WHERE country = :country")
    fun getTppEntitiesByCountry(country: String): List<EbaEntity>



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
    suspend fun updateApp(аpp: App)
}
