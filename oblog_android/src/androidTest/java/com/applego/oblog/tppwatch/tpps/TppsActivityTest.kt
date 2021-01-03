package com.applego.oblog.tppwatch.tpps

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
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
import com.applego.oblog.tppwatch.util.ServiceLocator
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.NcaEntity
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
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE), NcaEntity()))

        // start up Tpps screen
        val activityScenario = ActivityScenario.launch(TppsActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the tpp on the list and verify that all the data is correct
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.title)).check(matches(withText("TITLE1")))
        // TODO: Fix test
        //onView(withId(R.id.tpp_detail_description_text)).check(matches(withText("DESCRIPTION")))
        //onView(withId(R.id.tpp_detail_follow_checkbox)).check(matches(not(isChecked())))

        // Verify tpp is displayed on screen in the tpp list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous tpp is not displayed
        onView(withText("TITLE1")).check(doesNotExist())
    }

    @Test
    fun markTppAsFollowedOnDetailScreen_tppIsFollowInList() {
        // Add 1 used tpp
        val tppTitle = "FOLLOWED"
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = tppTitle, _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE), NcaEntity()))

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

    /*@Test
    fun markTppAsUsedOnDetailScreen_tppIsUsedInList() {
        // Add 1 followed tpp
        val tppTitle = "USED"
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = tppTitle, _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE), NcaEntity()))

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

        // Check that the tpp is marked as used
        onView(allOf(withId(R.id.follow_checkbox), hasSibling(withText(tppTitle))))
            .check(matches(not(isChecked())))
    }
*/
    @Test
    fun markTppAsFollowAndUsedOnDetailScreen_tppIsUsedInList() {
        // Add 1 used tpp
        val tppTitle = "ACT-COMP"
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = tppTitle, _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE), NcaEntity()))

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

        // Check that the tpp is marked as used
        onView(allOf(withId(R.id.follow_checkbox), hasSibling(withText(tppTitle))))
            .check(matches(not(isChecked())))
    }

    @Test
    fun markTppAsFollowOnDetailScreen_tppIsFollowInList() {//UsedAnd
        // Add 1 followed tpp
        val tppTitle = "COMP-ACT"
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = tppTitle, _description = "DESCRIPTION", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE), NcaEntity()))

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

        // Check that the tpp is marked as used
        onView(allOf(withId(R.id.follow_checkbox), hasSibling(withText(tppTitle))))
            .check(matches(isChecked()))
    }
}
