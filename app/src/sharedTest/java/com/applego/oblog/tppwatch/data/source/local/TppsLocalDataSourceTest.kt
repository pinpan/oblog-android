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
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.applego.oblog.tppwatch.MainCoroutineRule
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.succeeded
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
        val newTpp = Tpp("Entity_CZ28173281", "title", "description")
        newTpp.isFollowed = true
        localDataSource.saveTpp(newTpp)

        // WHEN  - Tpp retrieved by ID
        val result = localDataSource.getTpp(newTpp.id)

        // THEN - Same tpp is returned
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        assertThat(result.data.isFollowed, `is`(true))
    }

    @Test
    fun followedTpp_retrievedTppIsFollow() = runBlockingTest {
        // Given a new tpp in the persistent repository
        val newTpp = Tpp("Entity_CZ28173281", "title")
        localDataSource.saveTpp(newTpp)

        // When followed in the persistent repository
        localDataSource.followTpp(newTpp)
        val result = localDataSource.getTpp(newTpp.id)

        // Then the tpp can be retrieved from the persistent repository and is Followed
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.title, `is`(newTpp.title))
        assertThat(result.data.isFollowed, `is`(true))
    }

    @Test
    fun activateTpp_retrievedTppIsActive() = runBlockingTest {
        // Given a new followed tpp in the persistent repository
        val newTpp = Tpp("Entity_CZ28173281", "Some title", "Some description")
        localDataSource.saveTpp(newTpp)

        localDataSource.setTppActivateFlag(newTpp.id, true)

        // Then the tppk can be retrieved from the persistent repository and is active
        val result = localDataSource.getTpp(newTpp.id)

        assertThat(result.succeeded, `is`(true))
        result as Success

        assertThat(result.data.title, `is`("Some title"))
        assertThat(result.data.isFollowed, `is`(false))
    }

    @Test
    fun clearUnfollowedTpp_tppNotRetrievable() = runBlockingTest {
        // Given 2 new followed tpps and 1 active tpp in the persistent repository
        val newTpp1 = Tpp("Entity_CZ28173281", "title")
        val newTpp2 = Tpp("Entity_CZ28173282", "title2")
        val newTpp3 = Tpp("Entity_CZ28173283", "title3")
        localDataSource.saveTpp(newTpp1)
        localDataSource.followTpp(newTpp1)
        localDataSource.saveTpp(newTpp2)
        localDataSource.followTpp(newTpp2)
        localDataSource.saveTpp(newTpp3)
        // When followed tpps are cleared in the repository
        localDataSource.clearFollowedTpps()

        // Then the followed tpps cannot be retrieved and the active one can
        assertThat(localDataSource.getTpp(newTpp1.id).succeeded, `is`(false))
        assertThat(localDataSource.getTpp(newTpp2.id).succeeded, `is`(false))

        val result3 = localDataSource.getTpp(newTpp3.id)

        assertThat(result3.succeeded, `is`(true))
        result3 as Success

        assertThat(result3.data, `is`(newTpp3))
    }

    @Test
    fun deleteAllTpps_emptyListOfRetrievedTpp() = runBlockingTest {
        // Given a new tpp in the persistent repository and a mocked callback
        val newTpp = Tpp("Entity_CZ28173281", "title")

        localDataSource.saveTpp(newTpp)

        // When all tpps are deleted
        localDataSource.deleteAllTpps()

        // Then the retrieved tpps is an empty list
        val result = localDataSource.getTpps(TppsFilter()) as Success
        assertThat(result.data.isEmpty(), `is`(true))

    }

    @Test
    fun getTpps_retrieveSavedTpps() = runBlockingTest {
        // Given 2 new tpps in the persistent repository
        val newTpp1 = Tpp("Entity_CZ28173281", "title")
        val newTpp2 = Tpp("Entity_CZ28173282", "title2")

        localDataSource.saveTpp(newTpp1)
        localDataSource.saveTpp(newTpp2)
        // Then the tpps can be retrieved from the persistent repository
        val results = localDataSource.getTpps(TppsFilter()) as Success<List<Tpp>>
        val tpps = results.data
        assertThat(tpps.size, `is`(2))
    }
}
