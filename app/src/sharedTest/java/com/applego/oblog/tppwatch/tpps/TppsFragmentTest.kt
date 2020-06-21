package com.applego.oblog.tppwatch.tpps

import android.content.Context
import android.content.Intent
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
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.util.ServiceLocator
import com.applego.oblog.tppwatch.util.saveTppBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Ignore
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
        val job = CoroutineScope(Dispatchers.Main).launch {
            ServiceLocator.resetRestDataSource()
        }
    }

    @Test
    fun displayTpp_whenRepositoryHasData() {
        // GIVEN - One tpp already in the repository
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz"), NcaEntity()))

        // WHEN - On startup
        launchTppsActivity(false)

        // THEN - Verify tpp is displayed on screen
        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Test
    fun displayUsedTpp() {
        val tppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity.used = true
        tppEntity.followed = false
        repository.saveTppBlocking(Tpp(tppEntity, NcaEntity()))

        launchTppsActivity(false)

        onView(withText("TITLE1")).check(matches(isDisplayed()))


        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_used)).perform(click()) // Goes to FALSE
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))

        tppEntity.used = false
        tppEntity.followed = true
        repository.saveTppBlocking(Tpp(tppEntity, NcaEntity()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }

    @Test
    fun displayFollowedTpp() {
        var tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity1.followed = true
        repository.saveTppBlocking(Tpp(tppEntity1, NcaEntity()))

        launchTppsActivity(false)

        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_used)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }

    @Test
    fun deleteOneTpp() {
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz"), NcaEntity()))

        launchTppsActivity(false)

        // Open it in details view
        onView(withText("TITLE1")).perform(click())

        // Click delete tpp in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        //onView(withId(R.id.menu_filter)).perform(click())
        //onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
    }

    //@Ignore
    @Test
    fun deleteOneOfTwoTpps() {
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz"), NcaEntity()))
        repository.saveTppBlocking(Tpp(EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "TITLE2", _description = "DESCRIPTION2", _globalUrn = "", _ebaEntityVersion = "", _country = "cz"), NcaEntity()))

        launchTppsActivity(false)

        // Open it in details view
        onView(withText("TITLE1")).perform(click())

        // Click delete tpp in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        onView(withText("TITLE1")).check(doesNotExist())

        launchTppsActivity(false)
        // but not the other one
        onView(withText("TITLE2")).check(matches(isDisplayed()))
    }

    @Test
    fun markTppAsFollowed() {
        var tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity1.followed = true
        repository.saveTppBlocking(Tpp(tppEntity1, NcaEntity()))

        launchTppsActivity(false)

        // Mark the tpp as followed
        //onView(checkboxFollowed()).perform(click())

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Ignore
    @Test
    fun markTppAsUsed() {
        var aTpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        repository.saveTppBlocking(Tpp(aTpp, NcaEntity()))

        launchTppsActivity(false)

        // Mark the tpp as used
        onView(checkboxUsed()).perform(click())

        // Verify tpp is shown as used
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_used)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_used)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }

    @Test
    fun showAllTpps() {
        // Add one used tpp and one followed tpp
        var tpp1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        repository.saveTppBlocking(Tpp(tpp1, NcaEntity()))
        var tpp2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "TITLE2", _description = "DESCRIPTION2", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        repository.saveTppBlocking(Tpp(tpp2, NcaEntity()))

        launchTppsActivity(false)

        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))

        // Verify that both of our tpps are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
        onView(withText("TITLE2")).check(matches(not(isDisplayed())))

        // Verify that both of our tpps are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
    }

    @Test
    fun showUsedTpps() {
        // Add 2 used tpps and one followed tpp
        var tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity1.used = true
        var tppEntity2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "TITLE2", _description = "DESCRIPTION2", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity2.used = true
        var tppEntity3 = EbaEntity(_entityId = "28173283", _entityCode = "Entity_CZ28173283", _entityName = "TITLE3", _description = "DESCRIPTION3", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity3.followed = true

        repository.saveTppBlocking(Tpp(tppEntity1, NcaEntity()))
        repository.saveTppBlocking(Tpp(tppEntity2, NcaEntity()))
        repository.saveTppBlocking(Tpp(tppEntity3, NcaEntity()))

        launchTppsActivity(false)
        // By default ALL is selected
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))

        // Verify that the used tpps are not shown, but others (e.g. followed) are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_used)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        onView(withText("TITLE2")).check(doesNotExist())
        onView(withText("TITLE3")).check(matches(isDisplayed()))

        // Verify that the used tpps are not shown, but others (e.g. followed) are shown
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_used)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))
    }

    @Test
    fun showFollowedTpps() {
        // Add one used tpp and 2 followed tpps
        var tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "TITLE1", _description = "DESCRIPTION1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity1.used = true
        var tppEntity2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "TITLE2", _description = "DESCRIPTION2", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity2.followed = true
        var tppEntity3 = EbaEntity(_entityId = "28173283", _entityCode = "Entity_CZ28173283", _entityName = "TITLE3", _description = "DESCRIPTION3", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity3.followed = true
        repository.saveTppBlocking(Tpp(tppEntity1, NcaEntity()))
        repository.saveTppBlocking(Tpp(tppEntity2, NcaEntity()))
        repository.saveTppBlocking(Tpp(tppEntity3, NcaEntity()))

        launchTppsActivity(false)

        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))

        // Verify that the followed tpps (but not the used tpp) are shown
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
    fun noTpps_FollowedTppsFilter_AddTppViewVisible() {
        launchTppsActivity(false)

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())

        // Verify the "You have no tpps!" text is shown
        onView(withText("You have no followed TPPs!")).check(matches(isDisplayed()))
    }

    @Test
    fun noTpps_FollowedTppsFilter_AddTppViewNotVisible() {
        launchTppsActivity(false)

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_followed)).perform(click())

        // Verify the "You have no followed tpps!" text is shown
        onView(withText("You have no followed TPPs!")).check(matches((isDisplayed())))
    }

    @Test
    fun noTpps_UsedTppsFilter_AddTppViewNotVisible() {
        launchTppsActivity(false)

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_used)).perform(click())

        // Verify the "You have no used tpps!" text is shown
        onView(withText("No used TPPs selected!")).check(matches((isDisplayed())))
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

    private fun launchTppsActivity(isFirstRun: Boolean): ActivityScenario<TppsActivity>? {
        val intent = Intent(getApplicationContext(), TppsActivity::class.java)
        intent.putExtra("com.applego.oblog.tppwatch.isFirstRun", isFirstRun);
        val activityScenario: ActivityScenario<TppsActivity> = launch(intent)

        activityScenario.onActivity { activity ->
            // Disable animations in RecyclerView
            (activity.findViewById(R.id.tpps_list) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }

    private fun launchTppsFragment(): FragmentScenario<TppsFragment>? {
        val bundle = TppsFragmentArgs().toBundle()

        val fragmentScenario = launchFragmentInContainer<TppsFragment>(bundle, R.style.AppTheme)
        fragmentScenario.onFragment {
            //Navigation.setViewNavController(it.view!!, navController)
        }
        return fragmentScenario
    }


    private fun checkboxFollowed(): Matcher<View> {
        return allOf(withId(R.id.follow_checkbox))
    }

    private fun checkboxUsed(): Matcher<View> {
        return allOf(withId(R.id.used_checkbox))
    }
}
