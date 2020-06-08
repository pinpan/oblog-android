package com.applego.oblog.tppwatch.tpps

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.applego.oblog.tppwatch.BuildConfig
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.onboarding.OnboardingActivity
import com.applego.oblog.tppwatch.preferences.OblogPreferencesActivity
import com.applego.oblog.tppwatch.tppdetail.TppDetailTabsFragment
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.google.android.material.navigation.NavigationView
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger


/**
 * Main activity for the com.applego.oblog.tppwatch. Holds the Navigation Host Fragment and the Drawer, Toolbar, etc.
 */
class TppsActivity : SharedPreferences.OnSharedPreferenceChangeListener, AppCompatActivity() {

    private var selectedEnv: String ?= ""
    private var actualEnvironment : String ?= ""
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var selectedTppId: String ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

/*
        val skipSplash = sharedPerfs.getBoolean("skipSplash", false)
        if (!skipSplash) {
            Handler().postDelayed(object : Runnable {

                override fun run(): Unit {
                    setContentView(R.layout.spalsh_screen)

                    Toast.makeText(this@TppsActivity, "Run only once", Toast.LENGTH_LONG)
                            .show()
                }
            }, 5000)
        }
*/

        val sharedPerfs = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstRun = sharedPerfs.getBoolean("isFirstRun", false)
        if (!isFirstRun) {
            //show sign up activity
            Handler().post/*Delayed*/(object : Runnable {

                override fun run(): Unit {
                    val editor = sharedPerfs.edit()
                    editor.putBoolean("isFirstRun", false)
                    editor.commit()

                    /*val signOnIntent = Intent(this@TppsActivity, SignOnActivity::class)
                    startActivity(signOnIntent)*/
                    //finish()

                    startActivity(Intent(this@TppsActivity, OnboardingActivity::class.java))
                    Toast.makeText(this@TppsActivity, "Run only once", Toast.LENGTH_LONG).show()
                }
            }/*, 5000*/)
        }

        setContentView(com.applego.oblog.tppwatch.R.layout.tpps_act)
        setupNavigationDrawer()
        setSupportActionBar(findViewById(com.applego.oblog.tppwatch.R.id.toolbar))

        val navController: NavController = findNavController(com.applego.oblog.tppwatch.R.id.nav_host_fragment)
        appBarConfiguration =
            AppBarConfiguration.Builder(com.applego.oblog.tppwatch.R.id.tpps_fragment_dest, com.applego.oblog.tppwatch.R.id.statistics_fragment_dest)
                .setDrawerLayout(drawerLayout)
                .build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(com.applego.oblog.tppwatch.R.id.nav_view).setupWithNavController(navController)

        // This is for passing arguments to a destination fragment when changing whole screen
        //    - call it master fragment change.
        // When using tabview we have multiple fragments which does not occur in the navigation flow.
        // We need to pass data in a different way - inject or pass on creation.
        /*navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id) {
                R.id.addedit_tppapp_fragment_dest -> {
                    val argument = NavArgument.Builder().setDefaultValue(6).build()
                    destination.addArgument("Argument", argument)
                }
            }
        }*/
        navController.addOnDestinationChangedListener { navController, destination, arguments ->

            Timber.i("onDestinationChanged: "+ destination.label);
            if ((destination as FragmentNavigator.Destination).className.equals(TppDetailTabsFragment::class.java.canonicalName)) {
                selectedTppId = arguments!!.getString("tppId")
            } else if ((destination as FragmentNavigator.Destination).className.equals(TppsFragment::class.java.canonicalName)) {
                arguments!!.putString("tppId", selectedTppId.toString())
                selectedTppId = null
            }
        }

        getUserId()

        setupSharedPreferences();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (BuildConfig.DEBUG){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private fun setupSharedPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals("environment")) {
            actualEnvironment = sharedPreferences?.getString("environment","")
            val envsArray = getResources().getStringArray(R.array.environments);
            for (i in envsArray.indices.reversed()) {
                val env = envsArray[i]

                if (env == "Dev") {
                    selectedEnv = env
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tpps_activity_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId();
        if (id == R.id.settings) {
            val intent = Intent(this@TppsActivity, OblogPreferencesActivity::class.java)
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (Intent.ACTION_SEARCH == intent.action) {
            val aFragment = supportFragmentManager.primaryNavigationFragment
            if (aFragment != null) {
                val frags = aFragment.childFragmentManager.fragments
                if (!frags.isNullOrEmpty()) {
                    frags.forEach() {
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout))
            .apply {
                setStatusBarBackground(R.color.colorPrimaryDark)
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState);
    }

    override  fun onBackPressed() {
        val lastStack = supportFragmentManager.backStackEntryCount

        if (supportFragmentManager.getBackStackEntryCount() > 1) {
            supportFragmentManager.popBackStack();
        }

        //If the last fragment was named/tagged "three"
        val tag = supportFragmentManager.fragments[lastStack].tag
        if (tag != null) {
            if (tag.equals("THIRD", ignoreCase = true)) {

                supportFragmentManager.popBackStackImmediate()
                supportFragmentManager.popBackStackImmediate()

                //Get your first fragment that you loaded in the beginning.
                val first = supportFragmentManager.findFragmentByTag("FirstFragment")
                if (first != null) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(com.applego.oblog.tppwatch.R.id.drawer_layout, first)
                    transaction.commit()
                }
                return
            }
        }

        super.onBackPressed()
    }
/*

    fun getUsername(): String? {
        getPermissionToAccessAccounts()

        val manager: AccountManager = AccountManager.get(this)
        val accounts: Array<Account> = manager.accounts
        val possibleEmails = ArrayList<String>()
        for (account in accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name)
        }
        if (!possibleEmails.isEmpty() && possibleEmails[0] != null) {
            val email = possibleEmails[0]
            val parts = email!!.split("@").toTypedArray()
            if (parts.size > 1) return parts[0]
        }
        return null
    }
*/

    private var lastPermissionRequestId = AtomicInteger(0)

    fun pickUserAccount() {
        val googlePicker = AccountPicker.newChooseAccountIntent(null, null, arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE), true, null, null, null, null)
        startActivityForResult(googlePicker, 11)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11) {
            // Receiving a result from the AccountPicker
            if (resultCode == Activity.RESULT_OK) {
                println(data?.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))
                println(data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))

                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val prefsEditor = prefs.edit()
                prefsEditor.putString("userAccountType", data?.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))
                prefsEditor.putString("userId", data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))
                prefsEditor.commit()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Toast.makeText(this, R.string.pick_account, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getPermission(permission : String) :Int {
        val permissionRequest = lastPermissionRequestId.getAndIncrement()

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) { /*android.Manifest.permission.GET_ACCOUNTS*/
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) { /*android.Manifest.permission.GET_ACCOUNTS*/
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                return -1
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequest);
            }
        }

        return permissionRequest
    }

    fun getUserId() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val userAccount = prefs.getString("userId", null)
        if (userAccount == null) {
            pickUserAccount()
        }
    }
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
