package com.applego.oblog.tppwatch.data.source.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
// TODO: Elevate SDK version to 29 for tests
@Config(sdk = [Build.VERSION_CODES.P])
class EbaEntityDaoTest {

    private lateinit var database: TppDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each tpp synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            TppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTppAndGetById() = runBlockingTest {
        // GIVEN - insert a tpp
        val tpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        database.ebaDao().insertEbaEntity(tpp)

        // WHEN - Get the tpp by id from the database
        val loaded = database.ebaDao().getEbaEntityByDbId(tpp.getId())

        // THEN - The loaded data contains the expected values
        assertThat<EbaEntity>(loaded as EbaEntity, notNullValue())
        assertThat(loaded.getEntityId(), `is`(tpp.getEntityId()))
        assertThat(loaded.getEntityName(), `is`(tpp.getEntityName()))
        assertThat(loaded.getDescription(), `is`(tpp.getDescription()))
        assertThat(loaded.isFollowed(), `is`(tpp.isFollowed()))
    }

    @Test
    fun insertTppReplacesOnConflict() = runBlockingTest {
        // Given that a tpp is inserted
        val tpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        database.ebaDao().insertEbaEntity(tpp)

        // When a tpp with the same id is inserted
        val newTpp = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "title2", _description = "description2", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        database.ebaDao().insertEbaEntity(newTpp)

        // THEN - The loaded data contains the expected values
        val loaded = database.ebaDao().getEbaEntityByDbId(tpp.getId())
        assertThat(loaded?.getEntityId(), `is`(tpp.getEntityId()))
        assertThat(loaded?.getEntityName(), `is`("entityName"))
        assertThat(loaded?.getDescription(), `is`("description"))
        assertThat(loaded?.isFollowed(), `is`(false))
    }

    @Test
    fun insertTppAndGetTpps() = runBlockingTest {
        // GIVEN - insert a tpp
        val tpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        database.ebaDao().insertEbaEntity(tpp)

        // WHEN - Get tpps from the database
        val tpps = database.ebaDao().getAllTppEntities()

        // THEN - There is only 1 tpp in the database, and contains the expected values
        assertThat(tpps.size, `is`(1))
        assertThat(tpps[0].getEntityId(), `is`(tpp.getEntityId()))
        assertThat(tpps[0].getEntityName(), `is`(tpp.getEntityName()))
        assertThat(tpps[0].getDescription(), `is`(tpp.getDescription()))
        assertThat(tpps[0].isFollowed(), `is`(tpp.isFollowed()))
    }

    @Test
    fun updateTppAndGetById() = runBlockingTest {
        // When inserting a tpp
        val originalTpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        database.ebaDao().insertEbaEntity(originalTpp)

        // When the tpp is updated
        val updatedTpp = EbaEntity("28173282", "Entity_CZ28173282", "new entityName", "new description", originalTpp.getEntityId(), originalTpp.getEbaEntityVersion(), "CZ", EbaEntityType.NONE, originalTpp.getId())
        updatedTpp.followed = true
        database.ebaDao().updateEbaEntity(updatedTpp)

        // THEN - The loaded data contains the expected values
        val loaded = database.ebaDao().getEbaEntityByDbId(originalTpp.getId())
        assertThat(loaded?.getEntityId(), `is`(updatedTpp.getEntityId()))
        assertThat(loaded?.getEntityName(), `is`("new entityName"))
        assertThat(loaded?.getDescription(), `is`("new description"))
        assertThat(loaded?.isFollowed(), `is`(true))
    }

    @Test
    fun updateFollowedAndGetById() = runBlockingTest {
        // When inserting a tpp
        val tpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        database.ebaDao().insertEbaEntity(tpp)

        // When the tpp is updated
        database.ebaDao().updateFollowed(tpp.getId(), false)

        // THEN - The loaded data contains the expected values
        val loaded = database.ebaDao().getEbaEntityByDbId(tpp.getId())
        assertThat(loaded?.getEntityId(), `is`(tpp.getEntityId()))
        assertThat(loaded?.getEntityName(), `is`(tpp.getEntityName()))
        assertThat(loaded?._description, `is`(tpp.getDescription()))
        assertThat(loaded?.isFollowed(), `is`(false))
    }

    @Test
    fun deleteTppByIdAndGettingTpps() = runBlockingTest {
        // Given a tpp inserted
        val tpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        database.ebaDao().insertEbaEntity(tpp)

        // When deleting a tpp by id
        database.ebaDao().deleteTppEntityByDbId(tpp.getId())

        // THEN - The list is empty
        val tpps = database.ebaDao().getAllTppEntities()
        assertThat(tpps.isEmpty(), `is`(true))
    }

    @Test
    fun deleteTppsAndGettingTpps() = runBlockingTest {
        // Given a tpp inserted
        database.ebaDao().insertEbaEntity(EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE))

        // When deleting all tpps
        database.ebaDao().deleteTpps()

        // THEN - The list is empty
        val tpps = database.ebaDao().getAllTppEntities()
        assertThat(tpps.isEmpty(), `is`(true))
    }

    @Test
    fun deleteUnfollowedTppsAndGettingTpps() = runBlockingTest {
        // Given a followed tpp inserted
        var aTpp = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "followed", _description = "tpp", _globalUrn = "", _ebaEntityVersion = "", _country = "CZ", _entityType = EbaEntityType.NONE)
        aTpp.followed = true // Followed is not set in constructor
        database.ebaDao().insertEbaEntity(aTpp)

        // When deleting followed tpps
        database.ebaDao().deleteFollowedTppsEntities()

        // THEN - The list is empty
        val tpps = database.ebaDao().getAllTppEntities()
        assertThat(tpps.isEmpty(), `is`(true))
    }
}
