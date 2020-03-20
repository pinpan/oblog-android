package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

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
    suspend fun getTpps(): List<TppEntity>

    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM Tpps WHERE id = :tppId")
    suspend fun getTppById(tppId: String): TppEntity?

    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM Tpps WHERE globalUrn = :globalUrn")
    suspend fun getTppByGlobalUrn(globalUrn: String): TppEntity?

    /**
     * Select a tpp by entityCode (provided by Backend).
     *
     * @param tppId the tpp entityCode.
     * @return the tpp with entityCode.
     */
    @Query("SELECT * FROM Tpps WHERE entityCode = :entityCode")
    suspend fun getTppByEntityCode(entityCode: String): TppEntity?

    /**
     * Insert a tpp in the database. If the tpp already exists, replace it.
     *
     * @param tpp the tpp to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTpp(tpp: TppEntity)

    /**
     * Update a tpp.
     *
     * @param tpp tpp to be updated
     * @return the number of tpps updated. This should always be 1.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTpp(tpp: TppEntity): Int

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
    fun getTppsByCountry(country: String): List<TppEntity>
}
