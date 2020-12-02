package com.applego.oblog.tppwatch.preferences

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.applego.oblog.tppwatch.R

class OblogEnvironmentPreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, DefaultPreferencesFragment(R.xml.env_preferences))
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}