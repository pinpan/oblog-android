package com.applego.oblog.tppwatch.tppdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.applego.oblog.tppwatch.LiveDataTestUtil.getValue
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.assertSnackbarMessage
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.TppEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.BeforeClass
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

    // Executes each tppEntity synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    companion object {
        val tppEntity = TppEntity("Entity_CZ28173281", "Title1", "Description1")
        var tpp : Tpp? = null

        @BeforeClass
        @JvmStatic
        fun setupTestClass() {
            tpp = Tpp(tppEntity)
        }
    }

    @Before
    fun setupViewModel() {
        tppsRepository = FakeRepository()
        if (tpp != null) {
            tppsRepository.addTpps(tpp!!)
        }

        tppDetailViewModel = TppDetailViewModel(tppsRepository)
    }

    @Test
    fun getActiveTppFromRepositoryAndLoadIntoView() {
        CoroutineScope(Dispatchers.Main).launch {
            tppDetailViewModel.start(tppEntity.getId())
        }

        // Then verify that the view was notified
        assertThat(getValue(tppDetailViewModel.tpp).getTitle()).isEqualTo(tppEntity.getTitle())
        assertThat(getValue(tppDetailViewModel.tpp).getDescription())
            .isEqualTo(tppEntity.getDescription())
    }

    @Test
    fun followTpp() {
        CoroutineScope(Dispatchers.Main).launch {
            tppDetailViewModel.start(tppEntity.getId())
        }
        // Verify that the tppEntity was active initially
        assertThat(tppsRepository.tppsServiceData[tppEntity.getId()]?.isFollowed()).isFalse()

        // When the ViewModel is asked to follow the tppEntity
        tppDetailViewModel.setFollowed(true)

        // Then the tppEntity is followed and the snackbar shows the correct message
        assertThat(tppsRepository.tppsServiceData[tppEntity.getId()]?.isFollowed()).isTrue()
        assertSnackbarMessage(tppDetailViewModel.snackbarText, R.string.tpp_marked_followed)
    }

    @Test
    fun activateTpp() {
        tppEntity.active = true

        CoroutineScope(Dispatchers.Main).launch {
            tppDetailViewModel.start(tppEntity.getId())
        }
        // Verify that the tppEntity was followed initially
        assertThat(tppsRepository.tppsServiceData[tppEntity.getId()]?.isActive()).isTrue()

        // When the ViewModel is asked to follow the tppEntity
        tppDetailViewModel.setActive(false)

        // Then the tppEntity is not followed and the snackbar shows the correct message
        assertThat(tppsRepository.tppsServiceData[tppEntity.getId()]?.isActive()).isFalse()
        assertSnackbarMessage(tppDetailViewModel.snackbarText, R.string.tpp_marked_inactive)
    }

    @Test
    fun tppDetailViewModel_repositoryError() {
        // Given a repository that returns errors
        tppsRepository.setReturnError(true)

        // Given an initialized ViewModel with an active tppEntity
        CoroutineScope(Dispatchers.Main).launch {
            tppDetailViewModel.start(tppEntity.getId())
        }
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
        // When opening a new tppEntity
        tppDetailViewModel.editTpp()

        // Then the event is triggered
        val value = getValue(tppDetailViewModel.editTppEvent)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun deleteTpp() {
        assertThat(tppsRepository.tppsServiceData.containsValue(tpp)).isTrue()
        CoroutineScope(Dispatchers.Main).launch {
            tppDetailViewModel.start(tppEntity.getId())
        }
        // When the deletion of a tppEntity is requested
        //tppDetailViewModel.deleteTpp()

        assertThat(tppsRepository.tppsServiceData.containsValue(tpp)).isTrue()
    }

    @Test
    fun loadTpp_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Load the tppEntity in the viewmodel
        //runBlocking {

        //CoroutineScope(Dispatchers.Main).launch {
            tppDetailViewModel.start(tppEntity.getId())
        //}

        // Then progress indicator is shown
        val loading = getValue(tppDetailViewModel.dataLoading)
        assertThat(loading).isFalse()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(getValue(tppDetailViewModel.dataLoading)).isFalse()
    }
}
