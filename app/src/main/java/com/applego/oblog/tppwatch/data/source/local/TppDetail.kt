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
import java.util.*

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param key key for the detail TODO: Use DetailType enum with key name
 * @param detail actual detail info
 * @param tags tags assocoated with the detail
 * @param typeId tpp detail type identification
 * @param sourceId detail InfoSource identification
 * @param originalSourceId detail original InfoSource identification
 * @param datePublished - date when first published by original InfoSource
 * @param dateAcquired - date when accuired by Oblog system
 * @param isconfirmed whether or not this tpp is followed
 * @param confirmationSource InformationSource from which it was confirmed
 * @param id          id of the tpp
 */
@Entity(tableName = "tppdetails")
data class TppDetail @JvmOverloads constructor(
        @ColumnInfo(name = "key") var key: String = "",
        @ColumnInfo(name = "detail") var detail: String = "",
        @ColumnInfo(name = "tags") var tags: String = "",      // #Hash_Tag list as csv
        @ColumnInfo(name = "typeId") var typeId: String = "",  // TODO: Define types enum
        @ColumnInfo(name = "sourceId") var sourceId: String = "", // InfoSource from the detail was acquired by Oblog
        @ColumnInfo(name = "originalSourceId") var originalSourceId: String = "", // Original InfoSource which firstly published the detail - GOV, BANK, NCA, SOC, ...
        @ColumnInfo(name = "datePublished") var datePublished: Date,      // Date when detail was first published by original source
        @ColumnInfo(name = "dateAcquired") var dateAcquired: Date,      // Date when detail was acquired by Oblog from source with sourceId
        @ColumnInfo(name = "confirmed") var isConfirmed: Boolean = false, // TRUE if the detail is from official source or was confirmed publically or was evaluated to be valid by our algorithm
        @ColumnInfo(name = "confirmationSource") var confirmationSourceId: String = "", // InfoSource
        @PrimaryKey @ColumnInfo(name = "detailid") var id: String = UUID.randomUUID().toString()
) {

}
