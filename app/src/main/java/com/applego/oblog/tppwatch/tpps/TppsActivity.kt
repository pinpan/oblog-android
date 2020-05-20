package com.applego.oblog.tppwatch.tpps

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.applego.oblog.tppwatch.PreferencesActivity
import com.google.android.material.navigation.NavigationView
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.WindowManager
import androidx.navigation.NavArgument
import androidx.navigation.Navigation
import com.applego.oblog.tppwatch.BuildConfig
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.about.AboutFragment
import timber.log.Timber


/**
 * Main activity for the com.applego.oblog.tppwatch. Holds the Navigation Host Fragment and the Drawer, Toolbar, etc.
 */
class TppsActivity : SharedPreferences.OnSharedPreferenceChangeListener, AppCompatActivity() {

    private var selectedEnv: String ?= ""
    private var actualEnvironment : String ?= ""
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        }


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

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId();
        if (id == R.id.settings) {
            val intent = Intent(this@TppsActivity, PreferencesActivity::class.java)
            startActivity(intent);
            return true;
        }
        if (id == R.id.about_frag) {
            /*val intent = Intent(this@TppsActivity, PreferencesActivity::class.java)
            startActivity(intent);*/

            val aFragment = supportFragmentManager.primaryNavigationFragment
            if (aFragment != null) {
                val frags = aFragment.childFragmentManager.fragments
                if (!frags.isNullOrEmpty()) {
                    frags.forEach() {
                        if (it is TppsFragment)  {
                            /*
                                it.searchBy(query)
                            */
                        }
                    }
                }
            }

            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            if (aFragment != null) {
                transaction.replace(aFragment.id /*com.applego.oblog.tppwatch.R.id.nav_host_fragment*/, AboutFragment()).commit()
            }

            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
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
            || super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout))
            .apply {
                setStatusBarBackground(R.color.colorPrimaryDark)
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        /* do nothing */
        super.onSaveInstanceState(outState);
    }

    //var navController :NavController= Navigation.findNavController(this, R.id.nav_host_fragment)

    override  fun onBackPressed() {
        val lastStack = supportFragmentManager.backStackEntryCount

        if (supportFragmentManager.getBackStackEntryCount() > 1) {
            supportFragmentManager.popBackStack();
            // super.onBackPressed();
            // return;
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
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
