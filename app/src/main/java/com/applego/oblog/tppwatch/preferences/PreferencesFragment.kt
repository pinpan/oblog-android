package com.applego.oblog.tppwatch.preferences

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.applego.oblog.tppwatch.R

class PreferencesFragment : PreferenceFragmentCompat() {
    //val  REQUEST_CODE_PICK_ACCOUNT = 11

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val listPreference = findPreference("userAccount") as ListPreference?;

        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
        setListPreferenceData(listPreference);

        /*
            listPreference?.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener {
                override fun onPreferenceClick(preference: Preference?): Boolean {
                    setListPreferenceData(listPreference)
                    return true
                }
            })
        */
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

    /*
    private fun findAccount(userAccountName: String?, accounts: Array<Account>): Account? {
        accounts?.forEach { if (it.name.equals(userAccountName)) return it }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == Activity.RESULT_OK) {
                val userAccountType = data?.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE) ?: "N/A"
                Timber.d("Selected user account type: %s", userAccountType)

                val userAccountName = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) ?: "N/A"
                Timber.d("Selected user account name: %s", userAccountName)

                val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this.activity)
                val prefsEditor = prefs.edit()
                prefsEditor.putString("userAccountType", userAccountType)
                prefsEditor.putString("userAccountName", userAccountName)
                prefsEditor.putString("userAccount", userAccountName)
                prefsEditor.apply()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Timber.w("Activity for requestCode: REQUEST_CODE_PICK_ACCOUNT was cancelled")
            }
        }
    }*/
}