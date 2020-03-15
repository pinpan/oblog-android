package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters



/**
 * The Room Database that contains the Tpp table.
 *
 * Note that exportSchema should be true in production databases.
 */
// TODO: Set schema export to true and provide `room.schemaLocation` annotation processor argument
@Database(entities = [TppEntity::class, Service::class, Role::class, App::class], version = 20, exportSchema = false)
@TypeConverters(OblogTypeConverters::class)
abstract class TppDatabase : RoomDatabase() {

    abstract fun tppDao(): TppsDao

    abstract fun serviceDao(): ServicesDao

}
