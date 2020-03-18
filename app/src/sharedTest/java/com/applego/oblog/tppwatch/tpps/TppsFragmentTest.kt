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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import com.applego.oblog.tppwatch.data.source.local.TppEntity
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
        ServiceLocator.resetRestDataSource()
    }

    @Test
    fun displayTpp_whenRepositoryHasData() {
        // GIVEN - One tpp already in the repository
        repository.saveTppBlocking(Tpp(TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")))

        // WHEN - On startup
        launchActivity()

        // THEN - Verify tpp is displayed on screen
        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Test
    fun displayActiveTpp() {
        val tppEntity = TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")
        tppEntity.active = true
        tppEntity.followed = false
        repository.saveTppBlocking(Tpp(tppEntity))

        launchActivity()

        onView(withText("TITLE1")).check(matches(isDisplayed()))


        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click()) // Goes to FALSE
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))

        tppEntity.active = false
        tppEntity.followed = true
        repository.saveTppBlocking(Tpp(tppEntity))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }

    @Test
    fun displayFollowedTpp() {
        var tppEntity1 = TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")
        tppEntity1.followed = true
        repository.saveTppBlocking(Tpp(tppEntity1))

        launchActivity()

        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }

    //@Test
    fun deleteOneTpp() {
        repository.saveTppBlocking(Tpp(TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")))

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

    //@Test
    fun deleteOneOfTwoTpps() {
        repository.saveTppBlocking(Tpp(TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")))
        repository.saveTppBlocking(Tpp(TppEntity("Entity_CZ28173282", "TITLE2", "DESCRIPTION2", "", "", "cz")))

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
        var tppEntity1 = TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")
        repository.saveTppBlocking(Tpp(tppEntity1))

        launchActivity()

        // Mark the tpp as followed
        onView(checkboxFollowed()).perform(click())

        // Verify tpp is shown as followed
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }

    @Test
    fun markTppAsActive() {
        var aTpp = TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")
        repository.saveTppBlocking(Tpp(aTpp))

        launchActivity()

        // Mark the tpp as active
        onView(checkboxActive()).perform(click())

        // Verify tpp is shown as active
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }

    @Test
    fun showAllTpps() {
        // Add one active tpp and one followed tpp
        var tpp1 = TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")
        repository.saveTppBlocking(Tpp(tpp1))
        var tpp2 = TppEntity("Entity_CZ28173282", "TITLE2", "DESCRIPTION2", "", "", "cz")
        repository.saveTppBlocking(Tpp(tpp2))

        launchActivity()

        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))

        // Verify that both of our tpps are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
        onView(withText("TITLE2")).check(matches(not(isDisplayed())))

        // Verify that both of our tpps are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
    }

    @Test
    fun showActiveTpps() {
        // Add 2 active tpps and one followed tpp
        var tppEntity1 = TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")
        tppEntity1.active = true
        var tppEntity2 = TppEntity("Entity_CZ28173282", "TITLE2", "DESCRIPTION2", "", "", "cz")
        tppEntity2.active = true
        var tppEntity3 = TppEntity("Entity_CZ28173283", "TITLE3", "DESCRIPTION3", "", "", "cz")
        tppEntity3.followed = true

        repository.saveTppBlocking(Tpp(tppEntity1))
        repository.saveTppBlocking(Tpp(tppEntity2))
        repository.saveTppBlocking(Tpp(tppEntity3))

        launchActivity()
        // By default ALL is selected
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))

        // Verify that the active tpps are not shown, but others (e.g. followed) are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        onView(withText("TITLE2")).check(doesNotExist())
        onView(withText("TITLE3")).check(matches(isDisplayed()))

        // Verify that the active tpps are not shown, but others (e.g. followed) are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))
    }

    @Test
    fun showFollowedTpps() {
        // Add one active tpp and 2 followed tpps
        var tppEntity1 = TppEntity("Entity_CZ28173281", "TITLE1", "DESCRIPTION1", "", "", "cz")
        tppEntity1.active = true
        var tppEntity2 = TppEntity("Entity_CZ28173282", "TITLE2", "DESCRIPTION2", "", "", "cz")
        tppEntity2.followed = true
        var tppEntity3 = TppEntity("Entity_CZ28173283", "TITLE3", "DESCRIPTION3", "", "", "cz")
        tppEntity3.followed = true
        repository.saveTppBlocking(Tpp(tppEntity1))
        repository.saveTppBlocking(Tpp(tppEntity2))
        repository.saveTppBlocking(Tpp(tppEntity3))

        launchActivity()

        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))

        // Verify that the followed tpps (but not the active tpp) are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(doesNotExist())
        onView(withText("TITLE3")).check(doesNotExist())

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))
    }

    @Test
    fun noTpps_AllTppsFilter_AddTppViewVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())

        // Verify the "You have no tpps!" text is shown
        onView(withText("No TPPs available!")).check(matches(isDisplayed()))
    }

    @Test
    fun noTpps_FollowedTppsFilter_AddTppViewNotVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())

        // Verify the "You have no followed tpps!" text is shown
        onView(withText("You have no followed TPPs!")).check(matches((isDisplayed())))
    }

    @Test
    fun noTpps_ActiveTppsFilter_AddTppViewNotVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())

        // Verify the "You have no active tpps!" text is shown
        onView(withText("No active TPPs selected!")).check(matches((isDisplayed())))
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

/*

    private fun checkboxWithText(text: String): Matcher<View> {
        return allOf(withId(R.id.follow_checkbox), hasSibling(withText(text)))
    }
*/

    private fun checkboxFollowed(): Matcher<View> {
        return allOf(withId(R.id.follow_checkbox))
    }

    private fun checkboxActive(): Matcher<View> {
        return allOf(withId(R.id.active_checkbox))
    }
}
