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
import java.util.*

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
@TypeConverters(OblogTypeConverters::class)
data class Tpp @JvmOverloads constructor(
        @ColumnInfo(name = "entityCode") var entityCode: String = "",   // Entity Code retruned by EBA or NCA
        @ColumnInfo(name = "title") var title: String = "",             // Description  provided by original source. For additional details see detail
        @ColumnInfo(name = "description") var description: String = "",             // TPP is followed by user
        @ColumnInfo(name = "globalUrn") var globalUrn: String = "",                 // A Global Unified identifier. Used by Preta as own ID.
        @ColumnInfo(name = "ebaEntityVersion") var ebaEntityVersion: String = "",   // New, Updated, Removed (from original source), Deleted (From our DB) ...
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

    @ColumnInfo(name = "followed")
    var isFollowed: Boolean = false

    @ColumnInfo(name = "status")
    var status: RecordStatus = RecordStatus.NEW

    @ColumnInfo(name = "country")
    var country: String = ""

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isFollowed

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()

    @JvmField
    var ebaPassports: List<EbaPassport> = ArrayList<EbaPassport>()

    // DOES NOT COMPILE BECAUSE ROOM DOES OT KOW HOW TO SAVE IT
    //@JvmField
    //var ebaPassportsMap: Map<String, List<EbaService>> = HashMap<String, List<EbaService>>()

    //  details aka properties from EBA
    //  apps,
    //  tppRoles, - CZ has, Eba hasn't
    //  var d : Date = Date()
    //  get() = if (dateAcquired != null) dateAcquired else Date()
}
