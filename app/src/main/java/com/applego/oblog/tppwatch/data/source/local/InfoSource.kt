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
import java.util.UUID

/**
 * Immutable model class for a InfoSource.
 * In order to compile with Room, we can't use @JvmOverloads to generate multiple constructors.
 *
 * @param title       title of the InfoSource
 * @param id          id of the InfoSource
 */
@Entity(tableName = "infosources")
data class InfoSource @JvmOverloads constructor(
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "sourceType") var sourceType: String = "",  // TODO: Define SourceType enum - GOV, MEDIA, SOCIAL, EBA, NCA...
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

}
