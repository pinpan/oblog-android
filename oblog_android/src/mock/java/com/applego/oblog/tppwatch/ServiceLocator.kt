package com.applego.oblog.tppwatch

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.applego.oblog.tppwatch.data.FakeTppsRemoteDataSource
import com.applego.oblog.tppwatch.data.source.DefaultTppsRepository
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.data.source.local.TppsDaoDataSource
import com.applego.oblog.tppwatch.data.source.local.TppDatabase
import com.applego.oblog.tppwatch.data.source.remote.eba.OblogEbaService
import com.applego.oblog.tppwatch.data.source.remote.eba.TppsEbaDataSource
import kotlinx.coroutines.runBlocking

/**
 * A Psd2Service Locator for the [TppsRepository]. This is the mock version, with a
 * [FakeTppsRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: TppDatabase? = null
    @Volatile
    var tppsRepository: TppsRepository? = null
        @VisibleForTesting set

    fun provideTppsRepository(context: Context): TppsRepository {
        synchronized(this) {
            return tppsRepository ?: tppsRepository ?: createTppsRepository(context)
        }
    }

    private fun createTppsRepository(context: Context): TppsRepository {
        return DefaultTppsRepository(createTppsRestDataSource(context), createTppLocalDataSource(context))
    }

    private fun createTppLocalDataSource(context: Context): LocalTppDataSource {
        val database = database ?: createDataBase(context)
        return TppsDaoDataSource(database.tppDao())
    }

    private fun createTppsRestDataSource(context: Context): TppsEbaDataSource {
        val database = database ?: createDataBase(context)
        return TppsEbaDataSource(OblogEbaService.create(), database.tppDao())
    }

    private fun createDataBase(context: Context): TppDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext, TppDatabase::class.java, "Tpps.db")
                .fallbackToDestructiveMigration()
                .build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRestDataSource() {
        synchronized(lock) {
            runBlocking {
                FakeTppsRemoteDataSource.deleteAllTpps()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            //tppsRepository = null
        }
    }
}
