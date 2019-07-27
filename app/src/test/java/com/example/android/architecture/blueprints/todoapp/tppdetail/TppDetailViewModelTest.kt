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
package com.example.android.architecture.blueprints.todoapp.tppdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.LiveDataTestUtil.getValue
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.assertSnackbarMessage
import com.example.android.architecture.blueprints.todoapp.data.Tpp
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [TppDetailViewModel]
 */
@ExperimentalCoroutinesApi
class TppDetailViewModelTest {

    // Subject under test
    private lateinit var tppDetailViewModel: TppDetailViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tppsRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each tpp synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val tpp = Tpp("Title1", "Description1")

    @Before
    fun setupViewModel() {
        tppsRepository = FakeRepository()
        tppsRepository.addTpps(tpp)

        tppDetailViewModel = TppDetailViewModel(tppsRepository)
    }

    @Test
    fun getActiveTppFromRepositoryAndLoadIntoView() {
        tppDetailViewModel.start(tpp.id)

        // Then verify that the view was notified
        assertThat(getValue(tppDetailViewModel.tpp).title).isEqualTo(tpp.title)
        assertThat(getValue(tppDetailViewModel.tpp).description)
            .isEqualTo(tpp.description)
    }

    @Test
    fun completeTpp() {
        tppDetailViewModel.start(tpp.id)

        // Verify that the tpp was active initially
        assertThat(tppsRepository.tppsServiceData[tpp.id]?.isCompleted).isFalse()

        // When the ViewModel is asked to complete the tpp
        tppDetailViewModel.setCompleted(true)

        // Then the tpp is completed and the snackbar shows the correct message
        assertThat(tppsRepository.tppsServiceData[tpp.id]?.isCompleted).isTrue()
        assertSnackbarMessage(tppDetailViewModel.snackbarText, R.string.tpp_marked_complete)
    }

    @Test
    fun activateTpp() {
        tpp.isCompleted = true

        tppDetailViewModel.start(tpp.id)

        // Verify that the tpp was completed initially
        assertThat(tppsRepository.tppsServiceData[tpp.id]?.isCompleted).isTrue()

        // When the ViewModel is asked to complete the tpp
        tppDetailViewModel.setCompleted(false)

        // Then the tpp is not completed and the snackbar shows the correct message
        assertThat(tppsRepository.tppsServiceData[tpp.id]?.isCompleted).isFalse()
        assertSnackbarMessage(tppDetailViewModel.snackbarText, R.string.tpp_marked_active)

    }

    @Test
    fun tppDetailViewModel_repositoryError() {
        // Given a repository that returns errors
        tppsRepository.setReturnError(true)

        // Given an initialized ViewModel with an active tpp
        tppDetailViewModel.start(tpp.id)

        // Then verify that data is not available
        assertThat(getValue(tppDetailViewModel.isDataAvailable)).isFalse()
    }

    @Test
    fun updateSnackbar_nullValue() {
        // Before setting the Snackbar text, get its current value
        val snackbarText = tppDetailViewModel.snackbarText.value

        // Check that the value is null
        assertThat(snackbarText).isNull()
    }

    @Test
    fun clickOnEditTpp_SetsEvent() {
        // When opening a new tpp
        tppDetailViewModel.editTpp()

        // Then the event is triggered
        val value = getValue(tppDetailViewModel.editTppEvent)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun deleteTpp() {
        assertThat(tppsRepository.tppsServiceData.containsValue(tpp)).isTrue()
        tppDetailViewModel.start(tpp.id)

        // When the deletion of a tpp is requested
        tppDetailViewModel.deleteTpp()

        assertThat(tppsRepository.tppsServiceData.containsValue(tpp)).isFalse()
    }

    @Test
    fun loadTpp_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Load the tpp in the viewmodel
        tppDetailViewModel.start(tpp.id)

        // Then progress indicator is shown
        assertThat(getValue(tppDetailViewModel.dataLoading)).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(getValue(tppDetailViewModel.dataLoading)).isFalse()
    }
}
