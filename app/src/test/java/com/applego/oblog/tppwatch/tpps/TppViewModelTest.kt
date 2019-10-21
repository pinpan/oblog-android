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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.applego.oblog.tppwatch.LiveDataTestUtil
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.assertLiveDataEventTriggered
import com.applego.oblog.tppwatch.assertSnackbarMessage
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.FakeRepository
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
        // We initialise the tpps to 3, with one active and two followed
        tppsRepository = FakeRepository()
        val tpp1 = Tpp("Entity_CZ28173281", "Title1", "Description1")
        val tpp2 = Tpp("Entity_CZ28173282", "Title2", "Description2", true)
        val tpp3 = Tpp("Entity_CZ28173283", "Title3", "Description3", true)
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
    fun loadFollowedTppsFromRepositoryAndLoadIntoView() {
        // Given an initialized TppsViewModel with initialized tpps
        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.FOLLOWED_TPPS)

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

        // And the list of tppsList is empty
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
    fun clearFollowedTpps_clearsTpps() = mainCoroutineRule.runBlockingTest {
        // When followed tpps are cleared
        tppsViewModel.clearFollowedTpps()

        // Fetch tpps
        tppsViewModel.loadTpps(true)

        // Fetch tpps
        val allTpps = LiveDataTestUtil.getValue(tppsViewModel.items)
        val followedTpps = allTpps.filter { it.isFollowed }

        // Verify there are no followed tpps left
        assertThat(followedTpps).isEmpty()

        // Verify active tpp is not cleared
        assertThat(allTpps).hasSize(1)

        // Verify snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.followed_tpps_cleared
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
    fun followTpp_dataAndSnackbarUpdated() {
        // With a repository that has an active tpp
        val tpp = Tpp("Entity_CZ28173281", "Title", "Description")
        tppsRepository.addTpps(tpp)

        // Follow tpp
        tppsViewModel.FollowTpp(tpp, true)

        // Verify the tpp is followed
        assertThat(tppsRepository.tppsServiceData[tpp.id]?.isFollowed).isTrue()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_followed
        )
    }

    @Test
    fun activateTpp_dataAndSnackbarUpdated() {
        // With a repository that has a followed tpp
        val tpp = Tpp("Entity_CZ28173281", "Title", "Description", true)
        tppsRepository.addTpps(tpp)

        // Activate tpp
        tppsViewModel.FollowTpp(tpp, true)

        // Verify the tpp is active
        assertThat(tppsRepository.tppsServiceData[tpp.id]?.isActive).isFalse()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_followed
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
