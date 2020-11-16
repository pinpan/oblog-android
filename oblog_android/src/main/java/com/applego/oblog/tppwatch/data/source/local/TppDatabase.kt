package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import com.applego.oblog.tppwatch.data.dao.*
import com.applego.oblog.tppwatch.data.model.*

/**
 * The Room Database that contains the Tpp table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [EbaEntity::class, NcaEntity::class, Psd2Service::class, Role::class, App::class], version = 40, exportSchema = true)
@TypeConverters(OblogTypeConverters::class)
abstract class TppDatabase : RoomDatabase() {

    //abstract fun tppDao(): TppEntityDao
    abstract fun apppDao(): AppEntityDao

    abstract fun ebaDao(): EbaEntityDao

    abstract fun ncaDao(): NcaEntityDao

    abstract fun serviceDao(): ServicesDao
}
