package com.applego.oblog.tppwatch.tpps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.applego.oblog.tppwatch.LiveDataTestUtil
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.assertLiveDataEventTriggered
import com.applego.oblog.tppwatch.assertSnackbarMessage
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.source.local.TppEntity
import com.applego.oblog.tppwatch.util.saveTppBlocking
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Ignore
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

    // Executes each tppEntity synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tpps to 3, with one active and two followed
        tppsRepository = FakeRepository()
        val tppEntity1 = TppEntity("Entity_CZ28173281", "Title1", "Description1", "", "", "cz")
        val tppEntity2 = TppEntity("Entity_CZ28173282", "Title2", "Description2", "", "", "cz")
        val tppEntity3 = TppEntity("Entity_CZ28173283", "Title3", "Description3", "", "", "cz")
        tppsRepository.addTpps(Tpp(tppEntity1), Tpp(tppEntity2), Tpp(tppEntity3))

        tppsViewModel = TppsViewModel(tppsRepository)
    }

    @Test
    fun loadAllTppsFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        val aTpp = tppsViewModel.items.value?.get(0)
        if (aTpp != null) {
            aTpp.tppEntity.psd2 = true
            tppsRepository.saveTppBlocking(aTpp)
        }

        // Trigger loading of tpps
        tppsViewModel.loadTpps(false)

        // Then progress indicator is shown
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isFalse()

        // And data correctly loaded
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.items)).hasSize(3)

        // Given an initialized TppsViewModel with initialized tpps
        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.USED_TPPS)
        tppsViewModel.loadTpps(false)
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.items)).hasSize(1)


        tppsViewModel.setFiltering(TppsFilterType.PSD2_TPPS)
        tppsViewModel.loadTpps(false)
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.items)).hasSize(0)
    }

    @Ignore
    @Test
    fun loadActiveTppsFromRepositoryAndLoadIntoView() {
        // Given an initialized TppsViewModel with initialized tpps
        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.USED_TPPS)

        // Load tpps
        tppsViewModel.loadTpps(true)

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isFalse()

        // And data correctly loaded
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.items)).hasSize(3)
    }

    @Test
    fun loadFollowedTppsFromRepositoryAndLoadIntoView() {
        // Given an initialized TppsViewModel with initialized tpps

        // Load tpps
        tppsViewModel.loadTpps(true)

        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.FOLLOWED_TPPS)

        //val sTpps = tppsViewModel.items.value?.filter { it.title == "Title1" }?.forEach { it.isFollowed = true }

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading)).isFalse()

        val allTpps = tppsViewModel.items.value
        val followedTpps = allTpps?.filter { it.isFollowed()}
        assertThat(followedTpps?.size == 1)

        // And data correctly loaded
        assertThat(allTpps?.size == 3)
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
        // When adding a new tppEntity
        tppsViewModel.addNewTpp()

        // Then the event is triggered
        val value = LiveDataTestUtil.getValue(tppsViewModel.newTppEvent)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickOnOpenTpp_setsEvent() {
        // When opening a new tppEntity
        val tppId = "42"
        tppsViewModel.openTpp(tppId)

        // Then the event is triggered
        assertLiveDataEventTriggered(tppsViewModel.openTppEvent, tppId)
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
        // With a repository that has an active tppEntity
        val tppEntity = TppEntity("Entity_CZ28173281", "Title", "Description", "", "", "cz")
        tppsRepository.addTpps(Tpp(tppEntity))

        // Follow tppEntity
        tppsViewModel.followTpp(Tpp(tppEntity), true)

        // Verify the tppEntity is followed
        assertThat(tppsRepository.tppsServiceData[tppEntity.getId()]?.isFollowed()).isTrue()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_followed
        )
    }

    @Test
    fun activateTpp_dataAndSnackbarUpdated() {
        // With a repository that has a followed tppEntity
        val tppEntity = TppEntity("Entity_CZ28173281", "Title", "Description", "", "", "cz")
        tppsRepository.addTpps(Tpp(tppEntity))

        // Activate tppEntity
        tppsViewModel.followTpp(Tpp(tppEntity), true)
        tppsViewModel.followTpp(Tpp(tppEntity), true)

        // Verify the tppEntity is active
        assertThat(tppsRepository.tppsServiceData[tppEntity.getId()]?.isActive()).isFalse()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_followed
        )
    }

    @Test
    fun getTppsAddViewVisible() {
        // When the filter type is ALL_TPPS
        tppsViewModel.setFiltering(TppsFilterType.PSD2_TPPS)

        // Then the "Add tppEntity" action is visible
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.tppsAddViewVisible)).isTrue()
    }
}
