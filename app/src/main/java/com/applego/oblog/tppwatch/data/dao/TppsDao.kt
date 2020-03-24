package com.applego.oblog.tppwatch.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
    @Query("SELECT * FROM Tpps")
    suspend fun getTpps(): List<EbaEntity>

    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM Tpps WHERE id = :tppId")
    suspend fun getTppById(tppId: String): EbaEntity?

    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM Tpps WHERE globalUrn = :globalUrn")
    suspend fun getTppByGlobalUrn(globalUrn: String): EbaEntity?

    /**
     * Select a tpp by EBA entityCode (provided by OBLOG backend).
     *
     * @param entityCode the EBA  tpp entityCode.
     * @return the tpp with entityCode.
     */
    @Query("SELECT * FROM Tpps WHERE entityCode = :entityCode")
    suspend fun getTppByEntityCode(entityCode: String): EbaEntity?

    /**
     * Select a tpp by NCA entityId (provided by OBLOG backend).
     *
     * @param entityId the NCA tpp entityId.
     * @return the tpp with entityId.
     */
    @Query("SELECT * FROM Tpps WHERE entityId = :entityId")
    suspend fun getTppByEntityId(entityId: String): EbaEntity?

    /**
     * Insert a ebaEntity in the database. If the ebaEntity already exists, replace it.
     *
     * @param ebaEntity the ebaEntity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTpp(ebaEntity: EbaEntity)

    /**
     * Update a ebaEntity.
     *
     * @param ebaEntity ebaEntity to be updated
     * @return the number of tpps updated. This should always be 1.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTpp(ebaEntity: EbaEntity): Int

    /**
     * Update the followed status of a tpp
     *
     * @param tppId    id of the tpp
     * @param followed status to be updated
     */
    @Query("UPDATE tpps SET followed = :followed WHERE id = :tppId")
    suspend fun updateFollowed(tppId: String, followed: Boolean)

    /**
     * Update the active status of a tpp
     *
     * @param tppId  id of the tpp
     * @param active status to be updated
     */
    @Query("UPDATE tpps SET active = :active WHERE id = :tppId")
    suspend fun updateActive(tppId: String, active: Boolean)

    /**
     * Delete a tpp by id.
     *
     * @return the number of tpps deleted. This should always be 1.
     */
    @Query("DELETE FROM Tpps WHERE id = :tppId")
    suspend fun deleteTppById(tppId: String): Int

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
    suspend fun deleteFollowedTpps(): Int

    @Query("SELECT * FROM Tpps WHERE country = :country")
    fun getTppsByCountry(country: String): List<EbaEntity>
}
