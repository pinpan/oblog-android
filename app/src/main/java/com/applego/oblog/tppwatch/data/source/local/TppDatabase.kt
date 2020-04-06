package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import com.applego.oblog.tppwatch.data.dao.ServicesDao
import com.applego.oblog.tppwatch.data.dao.TppsDao
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.Psd2Service
import com.applego.oblog.tppwatch.data.model.Role

/**
 * The Room Database that contains the Tpp table.
 *
 * Note that exportSchema should be true in production databases.
 */
// TODO: Set schema export to true and provide `room.schemaLocation` annotation processor argument
@Database(entities = [EbaEntity::class, Psd2Service::class, Role::class, App::class], version = 28, exportSchema = true)
@TypeConverters(OblogTypeConverters::class)
abstract class TppDatabase : RoomDatabase() {

    abstract fun tppDao(): TppsDao

    abstract fun serviceDao(): ServicesDao

}
