package com.applego.oblog.tppwatch.tpps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.IdlingRegistry
import com.applego.oblog.tppwatch.LiveDataTestUtil
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.assertLiveDataEventTriggered
import com.applego.oblog.tppwatch.assertSnackbarMessage
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.util.EspressoIdlingResource
import com.applego.oblog.tppwatch.util.loadTppsBlocking
import com.applego.oblog.tppwatch.util.saveTppBlocking
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*

/**
 * Unit tests for the implementation of [TppsViewModel]
 */
@ExperimentalCoroutinesApi
class TppsViewModelTest {

    // Subject under test
    private lateinit var tppsViewModel: TppsViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tppsRepository: FakeRepository

    val tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title1", _description = "Description1", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.PSD_AISP)
    val tppEntity2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "Title2", _description = "Description2", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
    val tppEntity3 = EbaEntity(_entityId = "28173283", _entityCode = "Entity_CZ28173283", _entityName = "Title3", _description = "Description3", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each ebaEntity synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tpps to 3, with one used and two followed
        tppsRepository = FakeRepository()
        tppsRepository.addTpps(Tpp(tppEntity1, NcaEntity()), Tpp(tppEntity2, NcaEntity()), Tpp(tppEntity3, NcaEntity()))

        tppsViewModel = TppsViewModel(tppsRepository)
        tppsViewModel.searchFilter.init()
        tppsViewModel.loadEbaDirectory()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loadAllTppsFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Trigger loading of tpps
        tppsViewModel.loadTpps()

        // Then progress indicator is shown
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoadingLocalDB)).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoadingLocalDB)).isFalse()

        // And data correctly loaded
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.displayedItems)).hasSize(1)

        // Given an initialized TppsViewModel with initialized tpps
        // When loading of Tpps is requested
        val aTpp = tppsViewModel.displayedItems.value?.get(0)
        if (aTpp != null) {
            aTpp.ebaEntity.used = true
            tppsRepository.saveTppBlocking(aTpp)
        }

        tppsViewModel.searchFilter.init()
        tppsViewModel.searchFilter.updateUserSelection(TppsFilterType.USED)

        //tppsViewModel.setFiltering(TppsFilterType.USED_TPPS)
        tppsViewModel.loadTppsBlocking(false)
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.displayedItems)).hasSize(1)

        tppsViewModel.searchFilter.updateUserSelection(TppsFilterType.USED)
        tppsViewModel.searchFilter.updateUserSelection(TppsFilterType.AI_INST)
        tppsViewModel.loadTppsBlocking(false)
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.displayedItems)).hasSize(1)
    }

    @Ignore
    @Test
    fun loadUsedTppsFromRepositoryAndLoadIntoView() {
        // Given an initialized TppsViewModel with initialized tpps
        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.USED)

        // Load tpps
        tppsViewModel.loadTppsBlocking(true)

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoadingLocalDB)).isFalse()

        // And data correctly loaded
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.displayedItems)).hasSize(3)
    }

    @Test
    fun loadFollowedTppsFromRepositoryAndLoadIntoView() {
        // Given an initialized TppsViewModel with initialized tpps

        // Load tpps
        tppsViewModel.loadEbaDirectory()

        // When loading of Tpps is requested
        tppsViewModel.setFiltering(TppsFilterType.FOLLOWED)

        //val sTpps = tppsViewModel.items.value?.filter { it.entityName == "Title1" }?.forEach { it.isFollowed = true }

        // Then progress indicator is hidden
        //assertThat(LiveDataTestUtil.getValue(tppsViewModel.dataLoading).get()).isFalse()

        val allTpps = tppsViewModel.displayedItems.value
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
        tppsViewModel.loadTppsBlocking(true)

        // And the list of tppsList is the old one
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.displayedItems)).isEmpty()
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.displayedItems).size).isEqualTo(0)
        assertThat(LiveDataTestUtil.getValue(tppsViewModel.snackbarText).hasBeenHandled)

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
        // With a repository that has an used ebaEntity
        val tppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title", _description = "Description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        tppsRepository.addTpps(Tpp(tppEntity, NcaEntity()))

        // Follow ebaEntity
        tppsViewModel.followTpp(Tpp(tppEntity, NcaEntity()), true)

        // Verify the ebaEntity is followed
        assertThat(tppsRepository.tppsServiceData[tppEntity.getEntityId()]?.isFollowed()).isTrue()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_followed
        )

        tppsRepository.addTpps(Tpp(tppEntity1, NcaEntity()))
    }

    @Test
    fun activateTpp_dataAndSnackbarUpdated() {
        // With a repository that has a followed ebaEntity
        val tppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title", _description = "Description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        tppsRepository.addTpps(Tpp(tppEntity, NcaEntity()))

        // Activate ebaEntity
        tppsViewModel.followTpp(Tpp(tppEntity, NcaEntity()), true)
        tppsViewModel.followTpp(Tpp(tppEntity, NcaEntity()), true)

        // Verify the ebaEntity is used
        assertThat(tppsRepository.tppsServiceData[tppEntity.getEntityId()]?.isUsed()).isFalse()

        // The snackbar is updated
        assertSnackbarMessage(
            tppsViewModel.snackbarText, R.string.tpp_marked_followed
        )

        tppsRepository.addTpps(Tpp(tppEntity1, NcaEntity()))
    }
}
