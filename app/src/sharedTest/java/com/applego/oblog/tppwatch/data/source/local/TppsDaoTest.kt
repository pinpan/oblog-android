package com.applego.oblog.tppwatch.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.applego.oblog.tppwatch.MainCoroutineRule
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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TppsDaoTest {

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
        val tpp = TppEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        database.tppDao().insertTpp(tpp)

        // WHEN - Get the tpp by id from the database
        val loaded = database.tppDao().getTppById(tpp.getId())

        // THEN - The loaded data contains the expected values
        assertThat<TppEntity>(loaded as TppEntity, notNullValue())
        assertThat(loaded.getEntityId(), `is`(tpp.getEntityId()))
        assertThat(loaded.getEntityName(), `is`(tpp.getEntityName()))
        assertThat(loaded.getDescription(), `is`(tpp.getDescription()))
        assertThat(loaded.isFollowed(), `is`(tpp.isFollowed()))
    }

    @Test
    fun insertTppReplacesOnConflict() = runBlockingTest {
        // Given that a tpp is inserted
        val tpp = TppEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        database.tppDao().insertTpp(tpp)

        // When a tpp with the same id is inserted
        val newTpp = TppEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "title2", _description = "description2", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        database.tppDao().insertTpp(newTpp)

        // THEN - The loaded data contains the expected values
        val loaded = database.tppDao().getTppById(tpp.getId())
        assertThat(loaded?.getEntityId(), `is`(tpp.getEntityId()))
        assertThat(loaded?.getEntityName(), `is`("entityName"))
        assertThat(loaded?.getDescription(), `is`("description"))
        assertThat(loaded?.isFollowed(), `is`(false))
    }

    @Test
    fun insertTppAndGetTpps() = runBlockingTest {
        // GIVEN - insert a tpp
        val tpp = TppEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        database.tppDao().insertTpp(tpp)

        // WHEN - Get tpps from the database
        val tpps = database.tppDao().getTpps()

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
        val originalTpp = TppEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        database.tppDao().insertTpp(originalTpp)

        // When the tpp is updated
        val updatedTpp = TppEntity("28173282", "Entity_CZ28173282", "new entityName", "new description", originalTpp.getEntityId(), originalTpp.getEbaEntityVersion(), "cz", originalTpp.getId())
        updatedTpp.followed = true
        database.tppDao().updateTpp(updatedTpp)

        // THEN - The loaded data contains the expected values
        val loaded = database.tppDao().getTppById(originalTpp.getId())
        assertThat(loaded?.getEntityId(), `is`(updatedTpp.getEntityId()))
        assertThat(loaded?.getEntityName(), `is`("new entityName"))
        assertThat(loaded?.getDescription(), `is`("new description"))
        assertThat(loaded?.isFollowed(), `is`(true))
    }

    @Test
    fun updateFollowedAndGetById() = runBlockingTest {
        // When inserting a tpp
        val tpp = TppEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        database.tppDao().insertTpp(tpp)

        // When the tpp is updated
        database.tppDao().updateFollowed(tpp.getId(), false)

        // THEN - The loaded data contains the expected values
        val loaded = database.tppDao().getTppById(tpp.getId())
        assertThat(loaded?.getEntityId(), `is`(tpp.getEntityId()))
        assertThat(loaded?.getEntityName(), `is`(tpp.getEntityName()))
        assertThat(loaded?._description, `is`(tpp.getDescription()))
        assertThat(loaded?.isFollowed(), `is`(false))
    }

    @Test
    fun deleteTppByIdAndGettingTpps() = runBlockingTest {
        // Given a tpp inserted
        val tpp = TppEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        database.tppDao().insertTpp(tpp)

        // When deleting a tpp by id
        database.tppDao().deleteTppById(tpp.getId())

        // THEN - The list is empty
        val tpps = database.tppDao().getTpps()
        assertThat(tpps.isEmpty(), `is`(true))
    }

    @Test
    fun deleteTppsAndGettingTpps() = runBlockingTest {
        // Given a tpp inserted
        database.tppDao().insertTpp(TppEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "description", _globalUrn = "", _ebaEntityVersion = "", _country = "cz"))

        // When deleting all tpps
        database.tppDao().deleteTpps()

        // THEN - The list is empty
        val tpps = database.tppDao().getTpps()
        assertThat(tpps.isEmpty(), `is`(true))
    }

    @Test
    fun deleteUnfollowedTppsAndGettingTpps() = runBlockingTest {
        // Given a followed tpp inserted
        var aTpp = TppEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "followed", _description = "tpp", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        aTpp.followed = true // Followed is not set in constructor
        database.tppDao().insertTpp(aTpp)

        // When deleting followed tpps
        database.tppDao().deleteFollowedTpps()

        // THEN - The list is empty
        val tpps = database.tppDao().getTpps()
        assertThat(tpps.isEmpty(), `is`(true))
    }
}
