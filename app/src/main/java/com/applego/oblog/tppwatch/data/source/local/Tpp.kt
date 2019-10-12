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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.UUID

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param title       title of the tpp
 * @param description description of the tpp
 * @param isFollowed whether or not this tpp is followed
 * @param id          id of the tpp
 */
@Entity(tableName = "tpps")
@TypeConverters(StatusConverter::class)
data class Tpp @JvmOverloads constructor(
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "description") var description: String = "",             // Description  provided by original source. For additional details see detail
        @ColumnInfo(name = "followed") var isFollowed: Boolean = false,             // TPP is followed by user
        @ColumnInfo(name = "globalUrn") var globalUrn: String = "",                 // A Global Unified identifier. Used by Preta as own ID.
        @ColumnInfo(name = "status") var status: RecordStatus = RecordStatus.NEW,   // New, Updated, Removed (from original source), Deleted (From our DB) ...
        @PrimaryKey @ColumnInfo(name = "entryid") var id: String = UUID.randomUUID().toString()
    // TODO: Define tppRoles, tpp Services, tppApps, tppPassporting, tppDetails
) {

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description


    val isActive
        get() = !isFollowed

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}
