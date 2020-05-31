package com.applego.oblog.tppwatch.addedittpp

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.ServiceLocator
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.tpps.ADD_EDIT_RESULT_OK
import com.applego.oblog.tppwatch.util.getTppsBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

/**
 * Integration test for the Add Tpp screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
@ExperimentalCoroutinesApi
class AddEditTppFragmentTest {
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
    fun emptyTpp_isNotSaved() {
        // GIVEN - On the "Add Tpp" screen.
        val bundle = AddEditTppFragmentArgs(
            null,
                "Title"
        ).toBundle()
        launchFragmentInContainer<AddEditTppFragment>(bundle, R.style.AppTheme)

        // WHEN - Enter invalid entityName and description combination and click save
        onView(withId(R.id.add_tpp_title_edit_text)).perform(clearText())
        onView(withId(R.id.add_tpp_description_edit_text)).perform(clearText())
        onView(withId(R.id.save_tpp_fab)).perform(click())

        // THEN - Entered Tpp is still displayed (a correct tpp would close it).
        onView(withId(R.id.add_tpp_title_edit_text)).check(matches(isDisplayed()))
    }

    @Test
    fun validTpp_navigatesBack() {
        // GIVEN - On the "Add Tpp" screen.
        val navController = mock(NavController::class.java)
        launchFragment(navController)

        // WHEN - Valid entityName and description combination and click save
        onView(withId(R.id.add_tpp_title_edit_text)).perform(replaceText("entityName"))
        onView(withId(R.id.add_tpp_description_edit_text)).perform(replaceText("description"))
        onView(withId(R.id.save_tpp_fab)).perform(click())

        // THEN - Verify that we navigated back to the tpps screen.
        verify(navController).navigate(
            AddEditTppFragmentDirections
                .actionAddEditTppFragmentToTppsFragment(null, ADD_EDIT_RESULT_OK)
        )
    }

    private fun launchFragment(navController: NavController?) {
        val bundle = AddEditTppFragmentArgs(
            null,
            getApplicationContext<Context>().getString(R.string.add_tpp)
        ).toBundle()
        val scenario = launchFragmentInContainer<AddEditTppFragment>(bundle, R.style.AppTheme)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }

    @Test
    fun validTpp_isSaved() {
        // GIVEN - On the "Add Tpp" screen.
        val navController = mock(NavController::class.java)
        launchFragment(navController)

        // WHEN - Valid entityName and description combination and click save
        onView(withId(R.id.add_tpp_title_edit_text)).perform(replaceText("entityName"))
        onView(withId(R.id.add_tpp_description_edit_text)).perform(replaceText("description"))
        onView(withId(R.id.save_tpp_fab)).perform(click())

        // THEN - Verify that the repository saved the tpp
        val tpps = (repository.getTppsBlocking(true) as Result.Success).data
        assertEquals(tpps.size, 1)
        assertEquals(tpps[0].getEntityName(), "entityName")
        assertEquals(tpps[0].getDescription(), "description")
    }
}
