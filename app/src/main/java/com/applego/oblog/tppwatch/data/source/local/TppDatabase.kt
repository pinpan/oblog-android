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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.databinding.adapters.Converters
import androidx.room.TypeConverters



/**
 * The Room Database that contains the Tpp table.
 *
 * Note that exportSchema should be true in production databases.
 */
// TODO: Set schema export to true and provide `room.schemaLocation` annotation processor argument
@Database(entities = [Tpp::class, Service::class, Role::class, App::class], version = 15, exportSchema = false)
@TypeConverters(OblogTypeConverters::class)
abstract class TppDatabase : RoomDatabase() {

    abstract fun tppDao(): TppsDao

    abstract fun serviceDao(): ServicesDao

}
