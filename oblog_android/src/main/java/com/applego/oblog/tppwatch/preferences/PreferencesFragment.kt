package com.applego.oblog.tppwatch.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

open class PreferencesFragment (prefsXmlResource: Int) : PreferenceFragmentCompat() {
    val xmlResource = prefsXmlResource

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(xmlResource, rootKey)
    }
}