package com.applego.oblog.tppwatch.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.applego.oblog.tppwatch.data.repository.DefaultTppsRepository
import com.applego.oblog.tppwatch.data.source.local.TppsDaoDataSource
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.local.TppDatabase
import com.applego.oblog.tppwatch.data.source.remote.eba.OblogEbaService
import com.applego.oblog.tppwatch.data.source.remote.eba.TppEbaDataSource
import com.applego.oblog.tppwatch.data.source.remote.nca.OblogNcaService
import com.applego.oblog.tppwatch.data.source.remote.nca.TppsNcaDataSource

/**
 * A Psd2Service Locator for the [TppsRepository]. This is the prod version, with a
 * the "real" [TppsRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: TppDatabase? = null
    @Volatile
    var tppsRepository: TppsRepository? = null
        @VisibleForTesting set

    fun provideTppsRepository(context: Context): TppsRepository {
        synchronized(this) {
            return tppsRepository
                    ?: tppsRepository
                    ?: createTppsRepository(context)
        }
    }

    fun resetTppsRepository(context: Context) {
        synchronized(this) {
            createTppsRepository(context)
        }
    }

    private fun createTppsRepository(context: Context): TppsRepository {
        tppsRepository = DefaultTppsRepository(createTppsEbaDataSource(context), createTppsNcaDataSource(context), createTppLocalDataSource(context))
        return tppsRepository as DefaultTppsRepository
    }

    private fun createTppLocalDataSource(context: Context): LocalTppDataSource {
        val database = database
                ?: createDataBase(context)
        return TppsDaoDataSource(database.tppDao())
    }

    private fun createTppsEbaDataSource(context: Context): RemoteTppDataSource {
        val database = database
                ?: createDataBase(context)
        return TppEbaDataSource(OblogEbaService.create(context), database.tppDao())
    }

    private fun createTppsNcaDataSource(context: Context): TppsNcaDataSource {
        val database = database
                ?: createDataBase(context)
        return TppsNcaDataSource(OblogNcaService.create(context), database.tppDao())
    }

    private fun createDataBase(context: Context): TppDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            TppDatabase::class.java, "Tpps.db"
        )
                .fallbackToDestructiveMigration()
                .build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRestDataSource() {
        synchronized(lock) {
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
        }
    }
}
