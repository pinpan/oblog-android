package com.applego.oblog.tppwatch.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.applego.oblog.tppwatch.BuildConfig
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.repository.DefaultTppsRepository
import com.applego.oblog.tppwatch.data.source.local.TppsDaoDataSource
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.local.TppDatabase
import com.applego.oblog.tppwatch.data.source.remote.eba.OblogEbaService
import com.applego.oblog.tppwatch.data.source.remote.eba.TppsEbaDataSource
import com.applego.oblog.tppwatch.data.source.remote.nca.OblogNcaService
import com.applego.oblog.tppwatch.data.source.remote.nca.TppsNcaDataSource

/**
 * A Psd2Service Locator for the [TppsRepository]. This is the prod version, with a
 * the "real" [TppsRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var actualDatabase: TppDatabase? = null
    private var databases = HashMap<String, TppDatabase>()

    @Volatile
    var tppsRepository: TppsRepository? = null
        @VisibleForTesting set

    fun provideTppsRepository(context: Context): TppsRepository {
        synchronized(this) {
            return tppsRepository ?: tppsRepository ?: resetTppsRepository(context)
        }
    }

    fun resetTppsRepository(context: Context) : TppsRepository {
        synchronized(this) {
            return createTppsRepository(context)
        }
    }

    private fun createTppsRepository(context: Context): TppsRepository {
        tppsRepository = DefaultTppsRepository(createTppsEbaDataSource(context), createTppsNcaDataSource(context), createTppLocalDataSource(context))
        return tppsRepository as DefaultTppsRepository
    }

    private fun getEnvName(context: Context) : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) //Environment.getDataDirectory()
        var selectedEnvironmentName = sharedPreferences.getString("RUNTIME_ENV", BuildConfig.FLAVOR)

        return selectedEnvironmentName
    }

    private fun createTppLocalDataSource(context: Context): LocalTppDataSource {
        val envName = getEnvName(context) ?: BuildConfig.FLAVOR
        val database = getDataBase(context, envName) //database?: createDataBase(context)
        return TppsDaoDataSource(database.ebaDao())
    }

    private fun createTppsEbaDataSource(context: Context): RemoteTppDataSource<EbaEntity> {
        val envName = getEnvName(context) ?: BuildConfig.FLAVOR
        val database = getDataBase(context, envName) //database?: createDataBase(context)
        return TppsEbaDataSource(OblogEbaService.create(context), database.ebaDao())
    }

    private fun createTppsNcaDataSource(context: Context): TppsNcaDataSource {
        val envName = getEnvName(context) ?: BuildConfig.FLAVOR
        val database = getDataBase(context, envName) //database?: createDataBase(context)
        return TppsNcaDataSource(OblogNcaService.create(context), database.ncaDao())
    }

    private fun createDataBase(context: Context, dbName: String): TppDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            TppDatabase::class.java, dbName
        )
                .fallbackToDestructiveMigration()
                .build()
        //database = result
        return result
    }

    private fun getDataBase(context: Context, env: String): TppDatabase {
        var envName = if (env.isNullOrBlank()) "Dev" else env
        var aDatabase : TppDatabase? = databases.get(envName)
        if (aDatabase == null) {
            aDatabase = createDataBase(context, "Tpps_" + envName + ".db")
            databases.put(envName, aDatabase)
        }

        //database = aDatabase
        return aDatabase!!
    }

    @VisibleForTesting
    fun resetRestDataSource() {
        synchronized(lock) {
            // Clear all data to avoid test pollution.
            actualDatabase?.apply {
                clearAllTables()
                close()
            }
            actualDatabase = null
        }
    }
}
