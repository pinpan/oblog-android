package com.applego.oblog.tppwatch.tpps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.applego.oblog.tppwatch.LiveDataTestUtil
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.assertLiveDataEventTriggered
import com.applego.oblog.tppwatch.assertSnackbarMessage
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.source.local.EbaEntity
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

    val tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title1", _description = "Description1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
    val tppEntity2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "Title2", _description = "Description2", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
    val tppEntity3 = EbaEntity(_entityId = "28173283", _entityCode = "Entity_CZ28173283", _entityName = "Title3", _description = "Description3", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each ebaEntity synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tpps to 3, with one active and two followed
        tppsRepository = FakeRepository()
        tppsRepository.addTpps(Tpp(tppEntity1), Tpp(tppEntity2), Tpp(tppEntity3))

        tppsViewModel = TppsViewModel(tppsRepository)
    }

    @Test
    fun loadAllTppsFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        val aTpp = tppsViewModel.items.value?.get(0)
        if (aTpp != null) {
            aTpp.ebaEntity.psd2 = true
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

        //val sTpps = tppsViewModel.items.value?.filter { it.entityName == "Title1" }?.forEach { it.isFollowed = true }

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
        // When adding a new ebaEntity
        tppsViewModel.addNewTpp()

        // Then the event is triggered
        val value = LiveDataTestUtil.getValue(tppsViewModel.newTppEvent)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickOnOpenTpp_setsEvent() {
        // When opening a new ebaEntity
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
        // With a repository that has an active ebaEntity
        val tppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title", _description = "Description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppsRepository.addTpps(Tpp(tppEntity))

        // Follow ebaEntity
        tppsViewModel.followTpp(Tpp(tppEntity), true)

        // Verify the ebaEntity is followed
        assertThat(tppsRepository.tppsServiceData[tppEntity.getEntityId()]?.isFollowed()).isTrue()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_followed
        )

        tppsRepository.addTpps(Tpp(tppEntity1))
    }

    @Test
    fun activateTpp_dataAndSnackbarUpdated() {
        // With a repository that has a followed ebaEntity
        val tppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title", _description = "Description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppsRepository.addTpps(Tpp(tppEntity))

        // Activate ebaEntity
        tppsViewModel.followTpp(Tpp(tppEntity), true)
        tppsViewModel.followTpp(Tpp(tppEntity), true)

        // Verify the ebaEntity is active
        assertThat(tppsRepository.tppsServiceData[tppEntity.getEntityId()]?.isActive()).isFalse()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_followed
        )

        tppsRepository.addTpps(Tpp(tppEntity1))
    }

    @Test
    fun getTppsAddViewVisible() {
        // When the filter type is ALL_TPPS
        tppsViewModel.setFiltering(TppsFilterType.PSD2_TPPS)

        // Then the "Add ebaEntity" action is visible
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.tppsAddViewVisible)).isTrue()
    }
}
