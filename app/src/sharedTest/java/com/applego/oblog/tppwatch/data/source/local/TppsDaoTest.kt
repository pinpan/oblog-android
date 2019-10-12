/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        val tpp = Tpp("Entity_CZ28173281", "title", "description")
        database.tppDao().insertTpp(tpp)

        // WHEN - Get the tpp by id from the database
        val loaded = database.tppDao().getTppById(tpp.id)

        // THEN - The loaded data contains the expected values
        assertThat<Tpp>(loaded as Tpp, notNullValue())
        assertThat(loaded.id, `is`(tpp.id))
        assertThat(loaded.title, `is`(tpp.title))
        assertThat(loaded.description, `is`(tpp.description))
        assertThat(loaded.isFollowed, `is`(tpp.isFollowed))
    }

    @Test
    fun insertTppReplacesOnConflict() = runBlockingTest {
        // Given that a tpp is inserted
        val tpp = Tpp("Entity_CZ28173281", "title", "description")
        database.tppDao().insertTpp(tpp)

        // When a tpp with the same id is inserted
        val newTpp = Tpp("Entity_CZ28173282", "title2", "description2", true)
        database.tppDao().insertTpp(newTpp)

        // THEN - The loaded data contains the expected values
        val loaded = database.tppDao().getTppById(tpp.id)
        assertThat(loaded?.id, `is`(tpp.id))
        assertThat(loaded?.title, `is`("title"))
        assertThat(loaded?.description, `is`("description"))
        assertThat(loaded?.isFollowed, `is`(false))
    }

    @Test
    fun insertTppAndGetTpps() = runBlockingTest {
        // GIVEN - insert a tpp
        val tpp = Tpp("Entity_CZ28173281", "title", "description")
        database.tppDao().insertTpp(tpp)

        // WHEN - Get tpps from the database
        val tpps = database.tppDao().getTpps()

        // THEN - There is only 1 tpp in the database, and contains the expected values
        assertThat(tpps.size, `is`(1))
        assertThat(tpps[0].id, `is`(tpp.id))
        assertThat(tpps[0].title, `is`(tpp.title))
        assertThat(tpps[0].description, `is`(tpp.description))
        assertThat(tpps[0].isFollowed, `is`(tpp.isFollowed))
    }

    @Test
    fun updateTppAndGetById() = runBlockingTest {
        // When inserting a tpp
        val originalTpp = Tpp("Entity_CZ28173281", "title", "description")
        database.tppDao().insertTpp(originalTpp)

        // When the tpp is updated
        val updatedTpp = Tpp("Entity_CZ28173282", "new title", "new description", true, originalTpp.id, RecordStatus.UPDATED, originalTpp.id)
        database.tppDao().updateTpp(updatedTpp)

        // THEN - The loaded data contains the expected values
        val loaded = database.tppDao().getTppById(originalTpp.id)
        assertThat(loaded?.id, `is`(originalTpp.id))
        assertThat(loaded?.title, `is`("new title"))
        assertThat(loaded?.description, `is`("new description"))
        assertThat(loaded?.isFollowed, `is`(true))
    }

    @Test
    fun updateFollowedAndGetById() = runBlockingTest {
        // When inserting a tpp
        val tpp = Tpp("Entity_CZ28173281", "title", "description", true)
        database.tppDao().insertTpp(tpp)

        // When the tpp is updated
        database.tppDao().updateFollowed(tpp.id, false)

        // THEN - The loaded data contains the expected values
        val loaded = database.tppDao().getTppById(tpp.id)
        assertThat(loaded?.id, `is`(tpp.id))
        assertThat(loaded?.title, `is`(tpp.title))
        assertThat(loaded?.description, `is`(tpp.description))
        assertThat(loaded?.isFollowed, `is`(false))
    }

    @Test
    fun deleteTppByIdAndGettingTpps() = runBlockingTest {
        // Given a tpp inserted
        val tpp = Tpp("Entity_CZ28173281", "title", "description")
        database.tppDao().insertTpp(tpp)

        // When deleting a tpp by id
        database.tppDao().deleteTppById(tpp.id)

        // THEN - The list is empty
        val tpps = database.tppDao().getTpps()
        assertThat(tpps.isEmpty(), `is`(true))
    }

    @Test
    fun deleteTppsAndGettingTpps() = runBlockingTest {
        // Given a tpp inserted
        database.tppDao().insertTpp(Tpp("Entity_CZ28173281", "title", "description"))

        // When deleting all tpps
        database.tppDao().deleteTpps()

        // THEN - The list is empty
        val tpps = database.tppDao().getTpps()
        assertThat(tpps.isEmpty(), `is`(true))
    }

    @Test
    fun deleteUnfollowedTppsAndGettingTpps() = runBlockingTest {
        // Given a followed tpp inserted
        database.tppDao().insertTpp(Tpp("Entity_CZ28173281", "followed", "tpp", true))

        // When deleting followed tpps
        database.tppDao().deleteUnfollowedTpps()

        // THEN - The list is empty
        val tpps = database.tppDao().getTpps()
        assertThat(tpps.isEmpty(), `is`(true))
    }
}
