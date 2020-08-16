package com.applego.oblog.tppwatch

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.util.ServiceLocator
import timber.log.Timber
import timber.log.Timber.DebugTree
import kotlin.reflect.KProperty


/**
 * An application that lazily provides a repository. Note that this Psd2Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 */
class TppWatchApplication : Application() , SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if ("RUNTIME_ENV".equals(key)) {
            ServiceLocator.resetTppsRepository(this)
            tppRepository = ServiceLocator.tppsRepository!!
        }
    }

    class ProvideTppsRepository {
        operator fun getValue(thisRef: Context, property: KProperty<*>) : TppsRepository {
            return ServiceLocator.provideTppsRepository(thisRef.applicationContext)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TppsRepository) {
            println("$value has been assigned to '${property.name}' in $thisRef.")
        }
    }

    var tppRepository: TppsRepository by ProvideTppsRepository()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())

        ServiceLocator.resetTppsRepository(this)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val currentEnv = sharedPreferences.getString("RUNTIME_ENV", null)
        if (currentEnv == null) {
            sharedPreferences.edit().putString("RUNTIME_ENV", "PRODUCTION").commit()
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
}
