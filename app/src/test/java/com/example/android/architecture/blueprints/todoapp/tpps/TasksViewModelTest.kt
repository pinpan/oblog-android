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

package com.example.android.architecture.blueprints.todoapp.tpps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.LiveDataTestUtil
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.assertLiveDataEventTriggered
import com.example.android.architecture.blueprints.todoapp.assertSnackbarMessage
import com.example.android.architecture.blueprints.todoapp.data.Tpp
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [TppsViewModel]
 */
@ExperimentalCoroutinesApi
class TppsViewModelTest {

    // Subject under test
    private lateinit var tppsViewModel: TppsViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tppsRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each tpp synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tpps to 3, with one active and two completed
        tppsRepository = FakeRepository()
        val tpp1 = Tpp("Title1", "Description1")
        val tpp2 = Tpp("Title2", "Description2", true)
        val tpp3 = Tpp("Title3", "Description3", true)
        tppsRepository.addTpps(tpp1, tpp2, tpp3)

        tppsViewModel = TppsViewModel(tppsRepository)
    }

    @Test
    fun loadAllTppsFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Given an initialized TppsViewModel with initialized tpps
        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.ALL_TPPS)

        // Trigger loading of tpps
        tppsViewModel.loadTpps(true)

        // Then progress indicator is shown
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isFalse()

        // And data correctly loaded
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.items)).hasSize(3)
    }

    @Test
    fun loadActiveTppsFromRepositoryAndLoadIntoView() {
        // Given an initialized TppsViewModel with initialized tpps
        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.ACTIVE_TPPS)

        // Load tpps
        tppsViewModel.loadTpps(true)

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isFalse()

        // And data correctly loaded
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.items)).hasSize(1)
    }

    @Test
    fun loadCompletedTppsFromRepositoryAndLoadIntoView() {
        // Given an initialized TppsViewModel with initialized tpps
        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.COMPLETED_TPPS)

        // Load tpps
        tppsViewModel.loadTpps(true)

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isFalse()

        // And data correctly loaded
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.items)).hasSize(2)
    }

    @Test
    fun loadTpps_error() {
        // Make the repository return errors
        tppsRepository.setReturnError(true)

        // Load tpps
        tppsViewModel.loadTpps(true)

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isFalse()

        // And the list of items is empty
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.items)).isEmpty()

        // And the snackbar updated
        assertSnackbarMessage(tppsViewModel.snackbarText, R.string.loading_tpps_error)
    }

    @Test
    fun clickOnFab_showsAddTppUi() {
        // When adding a new tpp
        tppsViewModel.addNewTpp()

        // Then the event is triggered
        val value = LiveDataTestUtil.getValue(tppsViewModel.newTppEvent)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickOnOpenTpp_setsEvent() {
        // When opening a new tpp
        val tppId = "42"
        tppsViewModel.openTpp(tppId)

        // Then the event is triggered
        assertLiveDataEventTriggered(tppsViewModel.openTppEvent, tppId)
    }

    @Test
    fun clearCompletedTpps_clearsTpps() = mainCoroutineRule.runBlockingTest {
        // When completed tpps are cleared
        tppsViewModel.clearCompletedTpps()

        // Fetch tpps
        tppsViewModel.loadTpps(true)

        // Fetch tpps
        val allTpps = LiveDataTestUtil.getValue(tppsViewModel.items)
        val completedTpps = allTpps.filter { it.isCompleted }

        // Verify there are no completed tpps left
        assertThat(completedTpps).isEmpty()

        // Verify active tpp is not cleared
        assertThat(allTpps).hasSize(1)

        // Verify snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.completed_tpps_cleared
        )
    }

    @Test
    fun showEditResultMessages_editOk_snackbarUpdated() {
        // When the viewmodel receives a result from another destination
        tppsViewModel.showEditResultMessage(EDIT_RESULT_OK)

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.successfully_saved_tpp_message
        )
    }

    @Test
    fun showEditResultMessages_addOk_snackbarUpdated() {
        // When the viewmodel receives a result from another destination
        tppsViewModel.showEditResultMessage(ADD_EDIT_RESULT_OK)

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.successfully_added_tpp_message
        )
    }

    @Test
    fun showEditResultMessages_deleteOk_snackbarUpdated() {
        // When the viewmodel receives a result from another destination
        tppsViewModel.showEditResultMessage(DELETE_RESULT_OK)

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.successfully_deleted_tpp_message
        )
    }

    @Test
    fun completeTpp_dataAndSnackbarUpdated() {
        // With a repository that has an active tpp
        val tpp = Tpp("Title", "Description")
        tppsRepository.addTpps(tpp)

        // Complete tpp
        tppsViewModel.completeTpp(tpp, true)

        // Verify the tpp is completed
        assertThat(tppsRepository.tppsServiceData[tpp.id]?.isCompleted).isTrue()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_complete
        )
    }

    @Test
    fun activateTpp_dataAndSnackbarUpdated() {
        // With a repository that has a completed tpp
        val tpp = Tpp("Title", "Description", true)
        tppsRepository.addTpps(tpp)

        // Activate tpp
        tppsViewModel.completeTpp(tpp, true)

        // Verify the tpp is active
        assertThat(tppsRepository.tppsServiceData[tpp.id]?.isActive).isFalse()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_complete
        )
    }

    @Test
    fun getTppsAddViewVisible() {
        // When the filter type is ALL_TPPS
        tppsViewModel.setFiltering(TppsFilterType.ALL_TPPS)

        // Then the "Add tpp" action is visible
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.tppsAddViewVisible)).isTrue()
    }
}
