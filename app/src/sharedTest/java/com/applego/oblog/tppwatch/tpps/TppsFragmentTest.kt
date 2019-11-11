/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.applego.oblog.tppwatch.tpps

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.ServiceLocator
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.util.saveTppBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

/**
 * Integration test for the Tpp List screen.
 */
// TODO - Use FragmentScenario, see: https://github.com/android/android-test/issues/291
@RunWith(AndroidJUnit4::class)
@MediumTest
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
@ExperimentalCoroutinesApi
class TppsFragmentTest {

    private lateinit var repository: TppsRepository

    @Before
    fun initRepository() {
        repository = FakeRepository()
        ServiceLocator.tppsRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun displayTpp_whenRepositoryHasData() {
        // GIVEN - One tpp already in the repository
        repository.saveTppBlocking(Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1"))

        // WHEN - On startup
        launchActivity()

        // THEN - Verify tpp is displayed on screen
        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Test
    fun displayActiveTpp() {
        repository.saveTppBlocking(Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1"))

        launchActivity()

        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }

    @Test
    fun displayFollowedTpp() {
        var tpp1 = Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1")
        tpp1.isFollowed = true
        repository.saveTppBlocking(tpp1)

        launchActivity()

        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Test
    fun deleteOneTpp() {
        repository.saveTppBlocking(Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1"))

        launchActivity()

        // Open it in details view
        onView(withText("TITLE1")).perform(click())

        // Click delete tpp in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
    }

    @Test
    fun deleteOneOfTwoTpps() {
        repository.saveTppBlocking(Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1"))
        repository.saveTppBlocking(Tpp("Entity_CZ28173282", "TITLE2", "DESCRIPTION2"))

        launchActivity()

        // Open it in details view
        onView(withText("TITLE1")).perform(click())

        // Click delete tpp in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        // but not the other one
        onView(withText("TITLE2")).check(matches(isDisplayed()))
    }

    @Test
    fun markTppAsFollowed() {
        var tpp1 = Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1")
        repository.saveTppBlocking(tpp1)

        launchActivity()

        // Mark the tpp as followed
        onView(checkboxWithText("TITLE1")).perform(click())

        // Verify tpp is shown as followed
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())


        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Test
    fun markTppAsActive() {
        var aTpp = Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1")
        repository.saveTppBlocking(aTpp)

        launchActivity()

        // Mark the tpp as active
        onView(checkboxWithText("TITLE1")).perform(click())

        // Verify tpp is shown as active
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Test
    fun showAllTpps() {
        // Add one active tpp and one followed tpp
        repository.saveTppBlocking(Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1"))
        repository.saveTppBlocking(Tpp("Entity_CZ28173282", "TITLE2", "DESCRIPTION2"))

        launchActivity()

        // Verify that both of our tpps are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
    }

    @Test
    fun showActiveTpps() {
        // Add 2 active tpps and one followed tpp
        var tpp1 = Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1")
        var tpp2 = Tpp("Entity_CZ28173282", "TITLE2", "DESCRIPTION2")
        var tpp3 = Tpp("Entity_CZ28173283", "TITLE3", "DESCRIPTION3")
        tpp3.isFollowed = true

        repository.saveTppBlocking(tpp1)
        repository.saveTppBlocking(tpp2)
        repository.saveTppBlocking(tpp3)

        launchActivity()

        // Verify that the active tpps (but not the followed tpp) are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        //onView(withText("TITLE3")).check(doesNotExist())
    }

    @Test
    fun showFollowedTpps() {
        // Add one active tpp and 2 followed tpps
        var tpp1 = Tpp("Entity_CZ28173281", "TITLE1", "DESCRIPTION1")
        var tpp2 = Tpp("Entity_CZ28173282", "TITLE2", "DESCRIPTION2")
        tpp2.isFollowed = true
        var tpp3 = Tpp("Entity_CZ28173283", "TITLE3", "DESCRIPTION3")
        tpp3.isFollowed = true
        repository.saveTppBlocking(tpp1)
        repository.saveTppBlocking(tpp2)
        repository.saveTppBlocking(tpp3)

        launchActivity()

        // Verify that the followed tpps (but not the active tpp) are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))
    }

    @Test
    fun noTpps_AllTppsFilter_AddTppViewVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())

        // Verify the "You have no tpps!" text is shown
        onView(withText("You have no tpps!")).check(matches(isDisplayed()))
    }

    @Test
    fun noTpps_FollowedTppsFilter_AddTppViewNotVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())

        // Verify the "You have no followed tpps!" text is shown
        onView(withText("You have no followed tpps!")).check(matches((isDisplayed())))
    }

    @Test
    fun noTpps_ActiveTppsFilter_AddTppViewNotVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())

        // Verify the "You have no active tpps!" text is shown
        onView(withText("You have no active tpps!")).check(matches((isDisplayed())))
    }

    @Test
    fun clickAddTppButton_navigateToAddEditFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TppsFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the "+" button
        onView(withId(R.id.add_tpp_fab)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            TppsFragmentDirections.actionTppsFragmentToAddEditTppFragment(
                null, getApplicationContext<Context>().getString(R.string.add_tpp)
            )
        )
    }

    private fun launchActivity(): ActivityScenario<TppsActivity>? {
        val activityScenario = launch(TppsActivity::class.java)
        activityScenario.onActivity { activity ->
            // Disable animations in RecyclerView
            (activity.findViewById(R.id.tpps_list) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }

    private fun launchTppsFragment(): FragmentScenario<TppsFragment>? {
        val bundle = TppsFragmentArgs(1).toBundle()

        val fragmentScenario = launchFragmentInContainer<TppsFragment>(bundle, R.style.AppTheme)
        fragmentScenario.onFragment {
            //Navigation.setViewNavController(it.view!!, navController)
        }
        return fragmentScenario
    }


    private fun checkboxWithText(text: String): Matcher<View> {
        return allOf(withId(R.id.follow_checkbox), hasSibling(withText(text)))
    }
}
