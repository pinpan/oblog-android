package com.applego.oblog.tppwatch.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.succeeded
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the [LocalTppDataSource].
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TppsLocalDataSourceTest {

    private lateinit var localDataSource: TppsDaoDataSource
    private lateinit var database: TppDatabase


    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each tpp synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource = TppsDaoDataSource(database.tppDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveTpp_retrievesTpp() = runBlockingTest {
        // GIVEN - a new tpp saved in the database
        val newTpp = TppEntity("Entity_CZ28173281", "title", "description", "", "", "cz")
        newTpp.followed = true
        localDataSource.saveTpp(Tpp(newTpp))

        // WHEN  - Tpp retrieved by ID
        val result = localDataSource.getTpp(newTpp.getId())

        // THEN - Same tpp is returned
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.getTitle(), `is`("title"))
        assertThat(result.data.getDescription(), `is`("description"))
        assertThat(result.data.isFollowed(), `is`(true))
    }

    @Test
    fun followedTpp_retrievedTppIsFollow() = runBlockingTest {
        // Given a new tpp in the persistent repository
        val newTpp = TppEntity("Entity_CZ28173281", "title", "", "", "cz")
        val tpp = Tpp(newTpp)
        localDataSource.saveTpp(tpp)


        // When followed in the persistent repository
        localDataSource.udateFollowing(tpp, true)
        val result = localDataSource.getTpp(newTpp.getId())

        // Then the tpp can be retrieved from the persistent repository and is Followed
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.getTitle(), `is`(newTpp.getTitle()))
        assertThat(result.data.isFollowed(), `is`(true))
    }

    @Test
    fun activateTpp_retrievedTppIsActive() = runBlockingTest {
        // Given a new followed tpp in the persistent repository
        val newTpp = TppEntity("Entity_CZ28173281", "Some title", "Some description", "", "", "cz")
        localDataSource.saveTpp(Tpp(newTpp))

        localDataSource.setTppActivateFlag(newTpp.getId(), true)

        // Then the tppk can be retrieved from the persistent repository and is active
        val result = localDataSource.getTpp(newTpp.getId())

        assertThat(result.succeeded, `is`(true))
        result as Success

        assertThat(result.data.getTitle(), `is`("Some title"))
        assertThat(result.data.isActive(), `is`(true))
    }

    @Test
    fun clearUnfollowedTpp_tppNotRetrievable() = runBlockingTest {
        // Given 2 new followed tpps and 1 active tpp in the persistent repository
        val newTppEntity1 = TppEntity("Entity_CZ28173281", "title", "", "", "cz")
        val newTppEntity2 = TppEntity("Entity_CZ28173282", "title2", "", "", "cz")
        val newTppEntity3 = TppEntity("Entity_CZ28173283", "title3", "", "", "cz")
        val newTpp1 = Tpp(newTppEntity1)
        val newTpp2 = Tpp(newTppEntity2)
        val newTpp3 = Tpp(newTppEntity3)
        localDataSource.saveTpp(newTpp1)
        localDataSource.udateFollowing(newTpp1, true)

        localDataSource.saveTpp(newTpp2)
        localDataSource.udateFollowing(newTpp2, true)

        localDataSource.saveTpp(newTpp3)
        // When followed tpps are cleared in the repository
        localDataSource.clearFollowedTpps()

        // Then the followed tpps cannot be retrieved and the active one can
        assertThat(localDataSource.getTpp(newTpp1.getId()).succeeded, `is`(false))
        assertThat(localDataSource.getTpp(newTpp2.getId()).succeeded, `is`(false))

        val result3 = localDataSource.getTpp(newTpp3.getId())

        assertThat(result3.succeeded, `is`(true))
        result3 as Success

        assertEquals("Stored Tpp is not the same as fetched", result3.data.tppEntity, newTpp3.tppEntity)
    }

    @Test
    fun deleteAllTpps_emptyListOfRetrievedTpp() = runBlockingTest {
        // Given a new tpp in the persistent repository and a mocked callback
        val newTppEntity = TppEntity("Entity_CZ28173281", "title", "", "", "cz")

        localDataSource.saveTpp(Tpp(newTppEntity))

        // When all tpps are deleted
        localDataSource.deleteAllTpps()

        // Then the retrieved tpps is an empty list
        val result = localDataSource.getTpps(TppsFilter()) as Success
        assertThat(result.data.isEmpty(), `is`(true))

    }

    @Test
    fun getTpps_retrieveSavedTpps() = runBlockingTest {
        // Given 2 new tpps in the persistent repository
        val newTppEntity1 = TppEntity("Entity_CZ28173281", "title", "", "", "cz")
        val newTppEntity2 = TppEntity("Entity_CZ28173282", "title2", "", "", "cz")

        localDataSource.saveTpp(Tpp(newTppEntity1))
        localDataSource.saveTpp(Tpp(newTppEntity2))
        // Then the tpps can be retrieved from the persistent repository
        val results = localDataSource.getTpps(TppsFilter()) as Success<List<Tpp>>
        val tpps = results.data
        assertThat(tpps.size, `is`(2))
    }
}
