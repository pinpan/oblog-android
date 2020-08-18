package com.applego.oblog.tppwatch.preferences

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.applego.oblog.tppwatch.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val listPreference = findPreference("userId") as ListPreference?;

        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
        setListPreferenceData(listPreference);

        val userAccountPreference = findPreference("account") as SwitchPreference?;
        userAccountPreference?.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                //setListPreferenceData(listPreference)
                return false
            }
        })
    }

    protected fun setLanguagePreferenceData(lp: ListPreference?) {
        val entries = arrayOf<CharSequence>("English", "Czech")
        val entryValues = arrayOf<CharSequence>("1", "2")
        lp?.setEntries(entries)
        lp?.setDefaultValue("1")
        lp?.setEntryValues(entryValues)
    }

    protected fun setListPreferenceData(lp: ListPreference?) {
        val accountNames = getAccountNames()
        lp?.setEntryValues(accountNames)
        lp?.setEntries(accountNames)
    }

    private fun getAccountNames(): Array<CharSequence> {
        val namesList = ArrayList<CharSequence>()
        val accounts: Array<Account> = AccountManager.get(this.activity).accounts
        accounts.forEach { a -> namesList.add(a.name) }
        return namesList.toTypedArray()
    }
}