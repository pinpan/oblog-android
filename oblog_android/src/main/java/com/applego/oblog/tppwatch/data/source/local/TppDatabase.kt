package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import com.applego.oblog.tppwatch.data.dao.ServicesDao
import com.applego.oblog.tppwatch.data.dao.EbaEntityDao
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.Psd2Service
import com.applego.oblog.tppwatch.data.model.Role

/**
 * The Room Database that contains the Tpp table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [EbaEntity::class, Psd2Service::class, Role::class, App::class], version = 36, exportSchema = true)
@TypeConverters(OblogTypeConverters::class)
abstract class TppDatabase : RoomDatabase() {

    abstract fun tppDao(): EbaEntityDao

    abstract fun serviceDao(): ServicesDao

}
