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
package com.applego.oblog.tppwatch.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.applego.oblog.tppwatch.FakeFailingTppsLocalDataSource
import com.applego.oblog.tppwatch.FakeFailingTppsRemoteDataSource
import com.applego.oblog.tppwatch.LiveDataTestUtil
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.DefaultTppsRepository
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [StatisticsViewModel]
 */
@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each tpp synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the viewmodel
    private val tppsRepository = FakeRepository()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupStatisticsViewModel() {
        statisticsViewModel = StatisticsViewModel(tppsRepository)
    }

    @Test
    fun loadEmptyTppsFromRepository_EmptyResults() = mainCoroutineRule.runBlockingTest {
        // Given an initialized StatisticsViewModel with no tpps

        // When loading of Tpps is requested
        statisticsViewModel.start()

        // Then the results are empty
        assertThat(LiveDataTestUtil.getValue(statisticsViewModel.empty)).isTrue()
    }

    @Test
    fun loadNonEmptyTppsFromRepository_NonEmptyResults() {
        // We initialise the tpps to 3, with one active and two followed
        val tpp1 = Tpp("Entity_CZ28173281", "Title1", "Description1")
        tpp1.isFollowed = true
        val tpp2 = Tpp("Entity_CZ28173282", "Title2", "Description2")
        tpp2.isFollowed = true
        val tpp3 = Tpp("Entity_CZ28173283", "Title3", "Description3")
        tpp3.isFollowed = true
        val tpp4 = Tpp("Entity_CZ28173284", "Title4", "Description4")
        tppsRepository.addTpps(tpp1, tpp2, tpp3, tpp4)

        // When loading of Tpps is requested
        statisticsViewModel.start()

        // Then the results are not empty
        assertThat(LiveDataTestUtil.getValue(statisticsViewModel.empty))
            .isFalse()
        assertThat(LiveDataTestUtil.getValue(statisticsViewModel.activeTppsPercent))
            .isEqualTo(25f)
        assertThat(LiveDataTestUtil.getValue(statisticsViewModel.followedTppsPercent))
            .isEqualTo(75f)
    }

    @Test
    fun loadStatisticsWhenTppsAreUnavailable_CallErrorToDisplay() =
        mainCoroutineRule.runBlockingTest {
            val errorViewModel = StatisticsViewModel(
                DefaultTppsRepository(
                    FakeFailingTppsRemoteDataSource,
                    FakeFailingTppsLocalDataSource,
                    Dispatchers.Main  // Main is set in MainCoroutineRule
                )
            )

            // When statistics are loaded
            errorViewModel.start()

            // Then an error message is shown
            assertThat(LiveDataTestUtil.getValue(errorViewModel.empty)).isTrue()
            assertThat(LiveDataTestUtil.getValue(errorViewModel.error)).isFalse()
        }

    @Test
    fun loadTpps_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Load the tpp in the viewmodel
        statisticsViewModel.start()

        // Then progress indicator is shown
        assertThat(LiveDataTestUtil.getValue(statisticsViewModel.dataLoading)).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(statisticsViewModel.dataLoading)).isFalse()
    }
}
