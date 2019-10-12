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
    @Query("SELECT * FROM TppDetails WHERE detailid = :tppDetailId")
    suspend fun getTppDetailById(tppDetailId: String): TppDetail?
}
