package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object for the tpp details table.
 */
@Dao
interface TppDetailsDao {

    /**
     * Select all tpp details from the tppdetails table.
     *
     * @return all tppdetails.
     */
    @Query("SELECT * FROM TppDetails")
    suspend fun getTppDetails(): List<TppDetail>

    /**
     * Select a tppDetail by id.
     *
     * @param tppDetailId the tppDetail id.
     * @return the tppDetail with tppDetailId.
     */
    @Query("SELECT * FROM TppDetails WHERE id = :tppDetailId")
    suspend fun getTppDetailById(tppDetailId: String): TppDetail?
}
