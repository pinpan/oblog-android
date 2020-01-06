package com.applego.oblog.tppwatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, PreferencesFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}