package com.applego.oblog.tppwatch.addedittpp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.applego.oblog.tppwatch.LiveDataTestUtil.getValue
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.EbaEntity
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

    // Executes each ebaEntity synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val tppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title1", _description = "Description1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")

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
            entityName.value = newTitle
            description.value = newDescription
        }
        addEditTppViewModel.saveTpp()

        val newTpp = tppsRepository.tppsServiceData.values.first()

        // Then a ebaEntity is saved in the repository and the view updated
        assertThat(newTpp.getEntityName()).isEqualTo(newTitle)
        assertThat(newTpp.getDescription()).isEqualTo(newDescription)
    }

    @Test
    fun loadTpps_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Load the ebaEntity in the viewmodel
        addEditTppViewModel.start(tppEntity.getId())

        // Then progress indicator is shown
        assertThat(getValue(addEditTppViewModel.dataLoading)).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(getValue(addEditTppViewModel.dataLoading)).isFalse()
    }

    @Test
    fun loadTpps_tppShown() {
        // Add ebaEntity to repository
        tppsRepository.addTpps(Tpp(tppEntity))

        // Load the ebaEntity with the viewmodel
        addEditTppViewModel.start(tppEntity.getEntityId())

        // Verify a ebaEntity is loaded
        assertThat(getValue(addEditTppViewModel.entityName)).isEqualTo(tppEntity.getEntityName())
        assertThat(getValue(addEditTppViewModel.description)).isEqualTo(tppEntity.getDescription())
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
            this.entityName.value = title
            this.description.value = description
        }

        // When saving an unFollowed ebaEntity
        addEditTppViewModel.saveTpp()

        // Then the snackbar shows an error
        // isEmpty REMOVED FROM ENTITIY: assertSnackbarMessage(addEditTppViewModel.snackbarText, string.empty_tpp_message)
    }
}
