/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    suspend fun getTpps(): List<Tpp>

    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM Tpps WHERE id = :tppId")
    suspend fun getTppById(tppId: String): Tpp?

    /**
     * Select a tpp by id.
     *
     * @param tppId the tpp id.
     * @return the tpp with tppId.
     */
    @Query("SELECT * FROM Tpps WHERE globalUrn = :globalUrn")
    suspend fun getTppByGlobalUrn(globalUrn: String): Tpp?

    /**
     * Select a tpp by entityCode (provided by Backend).
     *
     * @param tppId the tpp entityCode.
     * @return the tpp with entityCode.
     */
    @Query("SELECT * FROM Tpps WHERE entityCode = :entityCode")
    suspend fun getTppByEntityCode(entityCode: String): Tpp?

    /**
     * Insert a tpp in the database. If the tpp already exists, replace it.
     *
     * @param tpp the tpp to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTpp(tpp: Tpp)

    /**
     * Update a tpp.
     *
     * @param tpp tpp to be updated
     * @return the number of tpps updated. This should always be 1.
     */
    @Update
    suspend fun updateTpp(tpp: Tpp): Int

    /**
     * Update the followed status of a tpp
     *
     * @param tppId    id of the tpp
     * @param followed status to be updated
     */
    @Query("UPDATE tpps SET followed = :followed WHERE id = :tppId")
    suspend fun updateFollowed(tppId: String, followed: Boolean)

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
    suspend fun deleteUnfollowedTpps(): Int
}
