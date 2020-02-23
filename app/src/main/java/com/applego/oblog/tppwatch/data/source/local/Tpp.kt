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
        @ColumnInfo(name = "entityCode") var entityCode: String = "",  // Entity Code retruned by EBA or NCA
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "description") var description: String = "",
        @ColumnInfo(name = "globalUrn") var globalUrn: String = "",    // A Global Unified identifier.
        @ColumnInfo(name = "ebaEntityVersion") var ebaEntityVersion: String = "",

        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

    @ColumnInfo(name = "fis")
    var isFis: Boolean = false

    @ColumnInfo(name = "psd2")
    var isPsd2: Boolean = false

    @ColumnInfo(name = "followed")
    var isFollowed: Boolean = false

    @ColumnInfo(name = "status")
    var status: RecordStatus = RecordStatus.NEW

    @ColumnInfo(name = "country")
    var country: String = ""

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    @ColumnInfo(name = "active")
    var isActive: Boolean = false

    var ebaPassport : EbaPassport = EbaPassport()
    // FOLLOWING DOES NOT COMPILE BECAUSE ROOM DOES OT KOW HOW TO SAVE IT
    //var ebaPassportsMap: Map<String, List<EbaService>> = HashMap<String, List<EbaService>>()


    // TODO#1: Remove this "special" field used only for validation
    val isEmpty
        get() = title.isEmpty() || description.isEmpty()

    // TODO#2: Consider Following fields
    //  details aka properties from EBA
    //  apps,
    //  tppRoles, - CZ has, Eba hasn't
}
