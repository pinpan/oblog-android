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
package com.applego.oblog.tppwatch.tppdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.ServiceLocator
import com.applego.oblog.tppwatch.data.Tpp
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.util.saveTppBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the Tpp Details screen.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class TppDetailFragmentTest {

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
    fun activeTppDetails_DisplayedInUi() {
        // GIVEN - Add active (incomplete) tpp to the DB
        val activeTpp = Tpp("Active Tpp", "AndroidX Rocks", false)
        repository.saveTppBlocking(activeTpp)

        // WHEN - Details fragment launched to display tpp
        val bundle = TppDetailFragmentArgs(activeTpp.id).toBundle()
        launchFragmentInContainer<TppDetailFragment>(bundle, R.style.AppTheme)

        // THEN - Tpp details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.tpp_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.tpp_detail_title_text)).check(matches(withText("Active Tpp")))
        onView(withId(R.id.tpp_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.tpp_detail_description_text)).check(matches(withText("AndroidX Rocks")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.tpp_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.tpp_detail_complete_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun completedTppDetails_DisplayedInUi() {
        // GIVEN - Add completed tpp to the DB
        val completedTpp = Tpp("Completed Tpp", "AndroidX Rocks", true)
        repository.saveTppBlocking(completedTpp)

        // WHEN - Details fragment launched to display tpp
        val bundle = TppDetailFragmentArgs(completedTpp.id).toBundle()
        launchFragmentInContainer<TppDetailFragment>(bundle, R.style.AppTheme)

        // THEN - Tpp details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.tpp_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.tpp_detail_title_text)).check(matches(withText("Completed Tpp")))
        onView(withId(R.id.tpp_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.tpp_detail_description_text)).check(matches(withText("AndroidX Rocks")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.tpp_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.tpp_detail_complete_checkbox)).check(matches(isChecked()))
    }
}
