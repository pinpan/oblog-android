package com.applego.oblog.tppwatch.tpps

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.R.string
import com.applego.oblog.tppwatch.ServiceLocator
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.util.DataBindingIdlingResource
import com.applego.oblog.tppwatch.util.EspressoIdlingResource
import com.applego.oblog.tppwatch.util.deleteAllTppsBlocking
import com.applego.oblog.tppwatch.util.monitorActivity
import com.applego.oblog.tppwatch.util.saveTppBlocking
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Large End-to-End test for the tpps module.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TppsActivityTest {

    private lateinit var repository: TppsRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        repository = ServiceLocator.provideTppsRepository(getApplicationContext())
        repository.deleteAllTppsBlocking()
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
    fun editTpp() {
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the tpp on the list and verify that all the data is correct
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.title)).check(matches(withText("TITLE1")))
        // TODO: Fix test
        //onView(withId(R.id.tpp_detail_description_text)).check(matches(withText("DESCRIPTION")))
        //onView(withId(R.id.tpp_detail_follow_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save
        onView(withId(R.id.edit_tpp_fab)).perform(click())
        onView(withId(R.id.add_tpp_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_tpp_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_tpp_fab)).perform(click())

        // Verify tpp is displayed on screen in the tpp list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous tpp is not displayed
        onView(withText("TITLE1")).check(doesNotExist())
    }

    @Test
    fun createOneTpp_deleteTpp() {

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active tpp
        onView(withId(R.id.add_tpp_fab)).perform(click())
        onView(withId(R.id.add_tpp_title_edit_text))
            .perform(typeText("TITLE1"), closeSoftKeyboard())
        onView(withId(R.id.add_tpp_description_edit_text)).perform(typeText("DESCRIPTION"))
        onView(withId(R.id.save_tpp_fab)).perform(click())

        // Open it in details view
        onView(withText("TITLE1")).perform(click())
        // Click delete tpp in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        //onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
    }

    //@Test
    fun createTwoTpps_deleteOneTpp() {
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")))
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "TITLE2", _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Open the second tpp in details view
        onView(withText("TITLE2")).perform(click())
        // Click delete tpp in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify only one tpp was deleted
        //onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(doesNotExist())
    }

    @Test
    fun markTppAsFollowedOnDetailScreen_tppIsFollowInList() {
        // Add 1 active tpp
        val tppTitle = "FOLLOWED"
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = tppTitle, _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the tpp on the list
        onView(withText(tppTitle)).perform(click())

        // Click on the checkbox in tpp details screen
        onView(withId(R.id.checkbox_follow)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the tpp is marked as followed
        onView(allOf(withId(R.id.follow_checkbox), hasSibling(withText(tppTitle))))
            .check(matches(isChecked()))
    }

    @Test
    fun markTppAsActiveOnDetailScreen_tppIsActiveInList() {
        // Add 1 followed tpp
        val tppTitle = "ACTIVE"
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = tppTitle, _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the tpp on the list
        onView(withText(tppTitle)).perform(click())
        // Click on the checkbox in tpp details screen
        onView(withId(R.id.checkbox_follow)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the tpp is marked as active
        onView(allOf(withId(R.id.follow_checkbox), hasSibling(withText(tppTitle))))
            .check(matches(not(isChecked())))
    }

    @Test
    fun markTppAsFollowAndActiveOnDetailScreen_tppIsActiveInList() {
        // Add 1 active tpp
        val tppTitle = "ACT-COMP"
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = tppTitle, _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the tpp on the list
        onView(withText(tppTitle)).perform(click())
        // Click on the checkbox in tpp details screen
        onView(withId(R.id.checkbox_follow)).perform(click())
        // Click again to restore it to original state
        onView(withId(R.id.checkbox_follow)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the tpp is marked as active
        onView(allOf(withId(R.id.follow_checkbox), hasSibling(withText(tppTitle))))
            .check(matches(not(isChecked())))
    }

    @Test
    fun markTppAsActiveAndFollowOnDetailScreen_tppIsFollowInList() {
        // Add 1 followed tpp
        val tppTitle = "COMP-ACT"
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = tppTitle, _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the tpp on the list
        onView(withText(tppTitle)).perform(click())
        // Click on the checkbox in tpp details screen
        onView(withId(R.id.checkbox_follow)).perform(click())
        // Click again to restore it to original state
        onView(withId(R.id.checkbox_follow)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the tpp is marked as active
        onView(allOf(withId(R.id.follow_checkbox), hasSibling(withText(tppTitle))))
            .check(matches(isChecked()))
    }

    @Test
    fun createTpp() {
        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the "+" button, add details, and save
        onView(withId(R.id.add_tpp_fab)).perform(click())
        onView(withId(R.id.add_tpp_title_edit_text))
            .perform(typeText("entityName"), closeSoftKeyboard())
        onView(withId(R.id.add_tpp_description_edit_text)).perform(typeText("description"))
        onView(withId(R.id.save_tpp_fab)).perform(click())

        // Then verify tpp is displayed on screen
        onView(withText("entityName")).check(matches(isDisplayed()))
    }
}
