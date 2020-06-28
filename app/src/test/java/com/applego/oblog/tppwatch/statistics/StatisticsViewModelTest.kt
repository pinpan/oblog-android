package com.applego.oblog.tppwatch.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.applego.oblog.tppwatch.FakeFailingTppsLocalDataSource
import com.applego.oblog.tppwatch.FakeFailingTppsRemoteDataSource
import com.applego.oblog.tppwatch.LiveDataTestUtil
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.data.repository.DefaultTppsRepository
import com.applego.oblog.tppwatch.data.source.FakeRepository
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.NcaEntity
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

    // Executes each ebaEntity synchronously using Architecture Components.
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
        // We initialise the tpps to 3, with one used and two followed
        val tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Title1", _description = "Description1", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
        tppEntity1.followed = true
        val tppEntity2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "Title2", _description = "Description2", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
        tppEntity2.followed = true
        val tppEntity3 = EbaEntity(_entityId = "28173283", _entityCode = "Entity_CZ28173283", _entityName = "Title3", _description = "Description3", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
        tppEntity3.followed = true
        val tppEntity4 = EbaEntity(_entityId = "28173284", _entityCode = "Entity_CZ28173284", _entityName = "Title4", _description = "Description4", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
        tppEntity3.used = true
        tppsRepository.addTpps(Tpp(tppEntity1, NcaEntity()), Tpp(tppEntity2, NcaEntity()), Tpp(tppEntity3, NcaEntity()), Tpp(tppEntity4, NcaEntity()))

        // When loading of Tpps is requested
        statisticsViewModel.start()

        // Then the results are not empty
        assertThat(LiveDataTestUtil.getValue(statisticsViewModel.empty))
            .isFalse()
        assertThat(LiveDataTestUtil.getValue(statisticsViewModel.usedTppsPercent))
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
                            FakeFailingTppsRemoteDataSource,
                            FakeFailingTppsLocalDataSource,
                            Dispatchers.Main  // Main is set in MainCoroutineRule
                    )
            )

            // When statistics are loaded
            errorViewModel.start()

            // Then an error message is shown
            assertThat(LiveDataTestUtil.getValue(errorViewModel.empty)).isTrue()
            assertThat(LiveDataTestUtil.getValue(errorViewModel.error)).isTrue()
        }

}
