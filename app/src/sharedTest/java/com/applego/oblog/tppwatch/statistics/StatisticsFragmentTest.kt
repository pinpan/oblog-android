package com.applego.oblog.tppwatch.statistics

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
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
import com.applego.oblog.tppwatch.util.DataBindingIdlingResource
import com.applego.oblog.tppwatch.util.monitorFragment
import com.applego.oblog.tppwatch.util.saveTppBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the statistics screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class StatisticsFragmentTest {
    private lateinit var repository: TppsRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun initRepository() {
        repository = FakeRepository()
        ServiceLocator.tppsRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRestDataSource()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun tpps_showsNonEmptyMessage() {
        // Given some tpps
        repository.apply {
            var tpp1 = TppEntity("Entity_CZ28173281", "Title1", "Description1")
            tpp1.active = true
            tpp1.followed = false
            saveTppBlocking(Tpp(tpp1))
            var tpp2 = TppEntity("Entity_CZ28173282", "Title2", "Description2")
            tpp2.followed = false
            saveTppBlocking(Tpp(tpp2))
        }

        val scenario = launchFragmentInContainer<StatisticsFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        val expectedActiveTppText = getApplicationContext<Context>()
            .getString(R.string.statistics_active_tpps, 50.0f)
        val expectedFollowedTppText = getApplicationContext<Context>()
            .getString(R.string.statistics_followed_tpps, 0.0f)
        // check that both info boxes are displayed and contain the correct info
        onView(withId(R.id.stats_active_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_active_text)).check(matches(withText(expectedActiveTppText)))
        onView(withId(R.id.stats_followed_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_followed_text)).check(matches(withText(expectedFollowedTppText)))
    }
}
