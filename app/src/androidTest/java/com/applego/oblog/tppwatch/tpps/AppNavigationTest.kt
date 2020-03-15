package com.applego.oblog.tppwatch.tpps

import android.view.Gravity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.ServiceLocator
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.data.source.local.TppEntity
import com.applego.oblog.tppwatch.util.DataBindingIdlingResource
import com.applego.oblog.tppwatch.util.EspressoIdlingResource
import com.applego.oblog.tppwatch.util.monitorActivity
import com.applego.oblog.tppwatch.util.saveTppBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the [DrawerLayout] layout component in [TppsActivity] which manages
 * navigation within the app.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    private lateinit var tppsRepository: TppsRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        tppsRepository = ServiceLocator.provideTppsRepository(getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetRestDataSource()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun drawerNavigationFromTppsToStatistics() {
        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
            .perform(open()) // Open Drawer

        // Start statistics screen.
        onView(withId(R.id.nav_view))
            .perform(navigateTo(R.id.statistics_fragment_dest))

        // Check that statistics screen was opened.
        onView(withId(R.id.statistics_layout)).check(matches(isDisplayed()))

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
            .perform(open()) // Open Drawer

        // Start tpps screen.
        onView(withId(R.id.nav_view))
            .perform(navigateTo(R.id.tpps_fragment_dest))

        // Check that tpps screen was opened.
        onView(withId(R.id.tpps_container_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun tppsScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.

        // Open Drawer
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check if drawer is open
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open.
    }

    @Test
    fun statsScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // When the user navigates to the stats screen
        activityScenario.onActivity {
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.statistics_fragment_dest)
        }

        // Then check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.

        // When the drawer is opened
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Then check that the drawer is open
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open.
    }

    @Test
    fun tppDetailScreen_doubleUIBackButton() {
        val tppEntity = TppEntity("Entity_CZ28173281", "UI <- button", "Description")
        tppsRepository.saveTppBlocking(Tpp(tppEntity))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the tpp on the list
        onView(withText("UI <- button")).perform(click())
        // Click on the edit tpp button
        onView(withId(R.id.edit_tpp_fab)).perform(click())

        // Confirm that if we click "<-" once, we end up back at the tpp details page
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.title)).check(matches(isDisplayed()))

        // Confirm that if we click "<-" a second time, we end up back at the home screen
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.tpps_container_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun tppDetailScreen_doubleBackButton() {
        val tppEntity = TppEntity("Entity_CZ28173281", "Back button", "Description")
        tppsRepository.saveTppBlocking(Tpp(tppEntity))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the tpp on the list
        onView(withText("Back button")).perform(click())
        // Click on the edit tpp button
        onView(withId(R.id.edit_tpp_fab)).perform(click())

        // Confirm that if we click back once, we end up back at the tpp details page
        pressBack()
        onView(withId(R.id.title)).check(matches(isDisplayed()))

        // Confirm that if we click back a second time, we end up back at the home screen
        pressBack()
        onView(withId(R.id.tpps_container_layout)).check(matches(isDisplayed()))
    }
}
