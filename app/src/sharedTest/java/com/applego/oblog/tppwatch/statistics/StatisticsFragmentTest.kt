package com.applego.oblog.tppwatch.statistics

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.util.ServiceLocator
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.util.DataBindingIdlingResource
import com.applego.oblog.tppwatch.util.monitorFragment
import com.applego.oblog.tppwatch.util.saveTppBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Integration test for the statistics screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
// TODO: Elevate SDK version to 29 for tests
@Config(sdk = [Build.VERSION_CODES.P])
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
        AsyncTask.execute( {ServiceLocator.resetRestDataSource()})
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
            var tpp1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title1", _description = "Description1", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", entityType = EbaEntityType.NONE)
            tpp1.used = true
            tpp1.followed = false
            saveTppBlocking(Tpp(tpp1, NcaEntity()))
            var tpp2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "Title2", _description = "Description2", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", entityType = EbaEntityType.NONE)
            tpp2.followed = false
            saveTppBlocking(Tpp(tpp2, NcaEntity()))
        }

        val scenario = launchFragmentInContainer<StatisticsFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        val expectedUsedTppText = getApplicationContext<Context>()
            .getString(R.string.statistics_used_tpps, 50.0f)
        val expectedFollowedTppText = getApplicationContext<Context>()
            .getString(R.string.statistics_followed_tpps, 0.0f)
        // check that both info boxes are displayed and contain the correct info
        /*onView(withId(R.id.stats_used_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_used_text)).check(matches(withText(expectedUsedTppText)))
        onView(withId(R.id.stats_followed_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_followed_text)).check(matches(withText(expectedFollowedTppText)))*/
    }
}
