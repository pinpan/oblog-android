package com.applego.oblog.tppwatch.data.source.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.succeeded
import org.junit.Assert.assertEquals
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
import org.robolectric.annotation.Config

/**
 * Integration test for the [LocalTppDataSource].
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
// TODO: Elevate SDK version to 29 for tests
@Config(sdk = [Build.VERSION_CODES.P])
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

        localDataSource = TppsDaoDataSource(database.ebaDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveTpp_retrievesTpp() = runBlockingTest {
        // GIVEN - a new tpp saved in the database
        val newTpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        newTpp.followed = true
        localDataSource.saveTpp(Tpp(newTpp, NcaEntity()))

        // WHEN  - Tpp retrieved by ID
        val result = localDataSource.getTpp(newTpp.getId())

        // THEN - Same tpp is returned
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.getEntityName(), `is`("entityName"))
        assertThat(result.data.getDescription(), `is`("description"))
        assertThat(result.data.isFollowed(), `is`(true))
    }

    @Test
    fun followedTpp_retrievedTppIsFollow() = runBlockingTest {
        // Given a new tpp in the persistent repository
        val newTpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        val tpp = Tpp(newTpp, NcaEntity())
        localDataSource.saveTpp(tpp)


        // When followed in the persistent repository
        localDataSource.updateFollowing(tpp, true)
        val result = localDataSource.getTpp(newTpp.getId())

        // Then the tpp can be retrieved from the persistent repository and is Followed
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.getEntityName(), `is`(newTpp.getEntityName()))
        assertThat(result.data.isFollowed(), `is`(true))
    }

    /*@Test
    fun activateTpp_retrievedTppIsUsed() = runBlockingTest {
        // Given a new followed tpp in the persistent repository
        val newTpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "Some entityName", _description = "Some description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        localDataSource.saveTpp(Tpp(newTpp, NcaEntity()))

        localDataSource.setTppActivateFlag(newTpp.getId(), true)

        // Then the tppk can be retrieved from the persistent repository and is used
        val result = localDataSource.getTpp(newTpp.getId())

        assertThat(result.succeeded, `is`(true))
        result as Success

        assertThat(result.data.getEntityName(), `is`("Some entityName"))
        //assertThat(result.data.isUsed(), `is`(true))
    }*/

    @Test
    fun clearUnfollowedTpp_tppNotRetrievable() = runBlockingTest {
        // Given 2 new followed tpps and 1 used tpp in the persistent repository
        val newTppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        val newTppEntity2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "title2", _description = "", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        val newTppEntity3 = EbaEntity(_entityId = "28173283", _entityCode = "Entity_CZ28173283", _entityName = "title3", _description = "", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        val newTpp1 = Tpp(newTppEntity1, NcaEntity())
        val newTpp2 = Tpp(newTppEntity2, NcaEntity())
        val newTpp3 = Tpp(newTppEntity3, NcaEntity())
        localDataSource.saveTpp(newTpp1)
        localDataSource.updateFollowing(newTpp1, true)

        localDataSource.saveTpp(newTpp2)
        localDataSource.updateFollowing(newTpp2, true)

        localDataSource.saveTpp(newTpp3)
        // When followed tpps are cleared in the repository
        localDataSource.clearFollowedTpps()

        // Then the followed tpps cannot be retrieved and the used one can
        assertThat(localDataSource.getTpp(newTpp1.getId()).succeeded, `is`(false))
        assertThat(localDataSource.getTpp(newTpp2.getId()).succeeded, `is`(false))

        val result3 = localDataSource.getTpp(newTpp3.getId())

        assertThat(result3.succeeded, `is`(true))
        result3 as Success

        assertEquals("Stored Tpp is not the same as fetched", result3.data.ebaEntity, newTpp3.ebaEntity)
    }

    @Test
    fun deleteAllTpps_emptyListOfRetrievedTpp() = runBlockingTest {
        // Given a new tpp in the persistent repository and a mocked callback
        val newTppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)

        localDataSource.saveTpp(Tpp(newTppEntity, NcaEntity()))

        // When all tpps are deleted
        localDataSource.deleteAllTpps()

        // Then the retrieved tpps is an empty list
        val result = localDataSource.getTpps() as Success
        assertThat(result.data.isEmpty(), `is`(true))

    }

    @Test
    fun getTpps_retrieveSavedTpps() = runBlockingTest {
        // Given 2 new tpps in the persistent repository
        val newTppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ",  _entityType = EbaEntityType.NONE)
        val newTppEntity2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "title2", _description = "", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)

        localDataSource.saveTpp(Tpp(newTppEntity1, NcaEntity()))
        localDataSource.saveTpp(Tpp(newTppEntity2, NcaEntity()))
        // Then the tpps can be retrieved from the persistent repository
        val results = localDataSource.getTpps() as Success<List<Tpp>>
        val tpps = results.data
        assertThat(tpps.size, `is`(2))
    }
}
