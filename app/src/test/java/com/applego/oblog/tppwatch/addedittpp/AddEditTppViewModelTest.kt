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
package com.applego.oblog.tppwatch.addedittpp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.applego.oblog.tppwatch.LiveDataTestUtil.getValue
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.R.string
import com.applego.oblog.tppwatch.assertSnackbarMessage
import com.applego.oblog.tppwatch.data.Tpp
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [AddEditTppViewModel].
 */
@ExperimentalCoroutinesApi
class AddEditTppViewModelTest {

    // Subject under test
    private lateinit var addEditTppViewModel: AddEditTppViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tppsRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each tpp synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val tpp = Tpp("Title1", "Description1")

    @Before
    fun setupViewModel() {
        // We initialise the repository with no tpps
        tppsRepository = FakeRepository()

        // Create class under test
        addEditTppViewModel = AddEditTppViewModel(tppsRepository)
    }

    @Test
    fun saveNewTppToRepository_showsSuccessMessageUi() {
        val newTitle = "New Tpp Title"
        val newDescription = "Some Tpp Description"
        (addEditTppViewModel).apply {
            title.value = newTitle
            description.value = newDescription
        }
        addEditTppViewModel.saveTpp()

        val newTpp = tppsRepository.tppsServiceData.values.first()

        // Then a tpp is saved in the repository and the view updated
        assertThat(newTpp.title).isEqualTo(newTitle)
        assertThat(newTpp.description).isEqualTo(newDescription)
    }

    @Test
    fun loadTpps_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Load the tpp in the viewmodel
        addEditTppViewModel.start(tpp.id)

        // Then progress indicator is shown
        assertThat(getValue(addEditTppViewModel.dataLoading)).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(getValue(addEditTppViewModel.dataLoading)).isFalse()
    }

    @Test
    fun loadTpps_tppShown() {
        // Add tpp to repository
        tppsRepository.addTpps(tpp)

        // Load the tpp with the viewmodel
        addEditTppViewModel.start(tpp.id)

        // Verify a tpp is loaded
        assertThat(getValue(addEditTppViewModel.title)).isEqualTo(tpp.title)
        assertThat(getValue(addEditTppViewModel.description)).isEqualTo(tpp.description)
        assertThat(getValue(addEditTppViewModel.dataLoading)).isFalse()
    }

    @Test
    fun saveNewTppToRepository_emptyTitle_error() {
        saveTppAndAssertSnackbarError("", "Some Tpp Description")
    }

    @Test
    fun saveNewTppToRepository_nullTitle_error() {
        saveTppAndAssertSnackbarError(null, "Some Tpp Description")
    }

    @Test
    fun saveNewTppToRepository_emptyDescription_error() {
        saveTppAndAssertSnackbarError("Title", "")
    }

    @Test
    fun saveNewTppToRepository_nullDescription_error() {
        saveTppAndAssertSnackbarError("Title", null)
    }

    @Test
    fun saveNewTppToRepository_nullDescriptionNullTitle_error() {
        saveTppAndAssertSnackbarError(null, null)
    }

    @Test
    fun saveNewTppToRepository_emptyDescriptionEmptyTitle_error() {
        saveTppAndAssertSnackbarError("", "")
    }

    private fun saveTppAndAssertSnackbarError(title: String?, description: String?) {
        (addEditTppViewModel).apply {
            this.title.value = title
            this.description.value = description
        }

        // When saving an incomplete tpp
        addEditTppViewModel.saveTpp()

        // Then the snackbar shows an error
        assertSnackbarMessage(addEditTppViewModel.snackbarText, string.empty_tpp_message)
    }
}
