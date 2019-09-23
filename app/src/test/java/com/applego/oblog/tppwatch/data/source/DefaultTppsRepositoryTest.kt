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
package com.applego.oblog.tppwatch.data.source

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.Tpp
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
@ExperimentalCoroutinesApi
class DefaultTppsRepositoryTest {

    private val tpp1 = Tpp("Title1", "Description1")
    private val tpp2 = Tpp("Title2", "Description2")
    private val tpp3 = Tpp("Title3", "Description3")
    private val newTpp = Tpp("Title new", "Description new")
    private val remoteTpps = listOf(tpp1, tpp2).sortedBy { it.id }
    private val localTpps = listOf(tpp3).sortedBy { it.id }
    private val newTpps = listOf(tpp3).sortedBy { it.id }
    private lateinit var tppsRemoteDataSource: FakeDataSource
    private lateinit var tppsLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tppsRepository: DefaultTppsRepository

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        tppsRemoteDataSource = FakeDataSource(remoteTpps.toMutableList())
        tppsLocalDataSource = FakeDataSource(localTpps.toMutableList())
        // Get a reference to the class under test
        tppsRepository = DefaultTppsRepository(
            tppsRemoteDataSource, tppsLocalDataSource, Dispatchers.Unconfined
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTpps_emptyRepositoryAndUninitializedCache() = runBlockingTest {
        val emptySource = FakeDataSource()
        val tppsRepository = DefaultTppsRepository(
            emptySource, emptySource, Dispatchers.Unconfined
        )

        assertThat(tppsRepository.getTpps(true) is Success).isTrue()
    }

    @Test
    fun getTpps_repositoryCachesAfterFirstApiCall() = runBlockingTest {
        // Trigger the repository to load data, which loads from remote and caches
        val initial = tppsRepository.getTpps(true)

        tppsRemoteDataSource.tpps = newTpps.toMutableList()

        val second = tppsRepository.getTpps()

        // Initial and second should match because we didn't force a refresh
        assertThat(second).isEqualTo(initial)
    }

    @Test
    fun getTpps_requestsAllTppsFromRemoteDataSource() = runBlockingTest {
        // When tpps are requested from the tpps repository
        val tpps = tppsRepository.getTpps(true) as Success

        // Then tpps are loaded from the remote data source
        assertThat(tpps.data).isEqualTo(remoteTpps)
    }

    @Test
    fun saveTpp_savesToCacheLocalAndRemote() = runBlockingTest {
        // Make sure newTpp is not in the remote or local datasources or cache
        assertThat(tppsRemoteDataSource.tpps).doesNotContain(newTpp)
        assertThat(tppsLocalDataSource.tpps).doesNotContain(newTpp)
        assertThat((tppsRepository.getTpps(true) as? Success)?.data).doesNotContain(newTpp)

        // When a tpp is saved to the tpps repository
        tppsRepository.saveTpp(newTpp)

        // Then the remote and local sources are called and the cache is updated
        assertThat(tppsRemoteDataSource.tpps).contains(newTpp)
        assertThat(tppsLocalDataSource.tpps).contains(newTpp)

        val result = tppsRepository.getTpps(true) as? Success
        assertThat(result?.data).contains(newTpp)
    }

    @Test
    fun getTpps_WithDirtyCache_tppsAreRetrievedFromRemote() = runBlockingTest {
        // First call returns from REMOTE
        val tpps = tppsRepository.getTpps(true)

        // Set a different list of tpps in REMOTE
        tppsRemoteDataSource.tpps = newTpps.toMutableList()

        // But if tpps are cached, subsequent calls load from cache
        val cachedTpps = tppsRepository.getTpps()
        assertThat(cachedTpps).isEqualTo(tpps)

        // Now force remote loading
        val refreshedTpps = tppsRepository.getTpps(true) as Success

        // Tpps must be the recently updated in REMOTE
        assertThat(refreshedTpps.data).isEqualTo(newTpps)
    }

    @Test
    fun getTpps_WithDirtyCache_remoteUnavailable_error() = runBlockingTest {
        // Make remote data source unavailable
        tppsRemoteDataSource.tpps = null

        // Load tpps forcing remote load
        val refreshedTpps = tppsRepository.getTpps(false)

        // Result should be an error
        assertThat(refreshedTpps).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun getTpps_WithRemoteDataSourceUnavailable_tppsAreRetrievedFromLocal() = runBlockingTest {
        // When the remote data source is unavailable
        tppsRemoteDataSource.tpps = null

        // The repository fetches from the local source
        assertThat((tppsRepository.getTpps(true) as Success).data).isEqualTo(localTpps)
    }

    @Test
    fun getTpps_WithBothDataSourcesUnavailable_returnsError() = runBlockingTest {
        // When both sources are unavailable
        tppsRemoteDataSource.tpps = null
        tppsLocalDataSource.tpps = null

        // The repository returns an error
        assertThat(tppsRepository.getTpps()).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun getTpps_refreshesLocalDataSource() = runBlockingTest {
        val initialLocal = tppsLocalDataSource.tpps!!.toList()

        // First load will fetch from remote
        val newTpps = (tppsRepository.getTpps(true) as Success).data

        assertThat(newTpps).isEqualTo(remoteTpps)
        assertThat(newTpps).isEqualTo(tppsLocalDataSource.tpps)
        assertThat(tppsLocalDataSource.tpps).isNotEqualTo(initialLocal)
    }

    //@Test
    fun saveTpp_savesTppToRemoteAndUpdatesCache() = runBlockingTest {
        // Save a tpp
        tppsRepository.saveTpp(newTpp)

        // Verify it's in all the data sources
        assertThat(tppsLocalDataSource.tpps).contains(newTpp)
        assertThat(tppsRemoteDataSource.tpps).contains(newTpp)

        // Verify it's in the cache
        tppsLocalDataSource.deleteAllTpps() // Make sure they don't come from local
        tppsRemoteDataSource.deleteAllTpps() // Make sure they don't come from remote
        val result = tppsRepository.getTpps(true) as Success
        assertThat(result.data).contains(newTpp)
    }

    @Test
    fun completeTpp_completesTppToServiceAPIUpdatesCache() = runBlockingTest {
        // Save a tpp
        tppsRepository.saveTpp(newTpp)

        // Make sure it's active
        assertThat((tppsRepository.getTpp(newTpp.id, true) as Success).data.isCompleted).isFalse()

        // Mark is as complete
        tppsRepository.completeTpp(newTpp.id)

        // Verify it's now completed
        assertThat((tppsRepository.getTpp(newTpp.id) as Success).data.isCompleted).isTrue()
    }

    @Test
    fun completeTpp_activeTppToServiceAPIUpdatesCache() = runBlockingTest {
        // Save a tpp
        tppsRepository.saveTpp(newTpp)
        tppsRepository.completeTpp(newTpp.id)

        // Make sure it's completed
        assertThat((tppsRepository.getTpp(newTpp.id, true) as Success).data.isActive).isFalse()

        // Mark is as active
        tppsRepository.activateTpp(newTpp.id)

        // Verify it's now activated
        val result = tppsRepository.getTpp(newTpp.id, true) as Success
        assertThat(result.data.isActive).isTrue()
    }

    @Test
    fun getTpp_repositoryCachesAfterFirstApiCall() = runBlockingTest {
        // Trigger the repository to load data, which loads from remote
        tppsRemoteDataSource.tpps = mutableListOf(tpp1)
        tppsRepository.getTpp(tpp1.id, true)

        // Configure the remote data source to store a different tpp
        tppsRemoteDataSource.tpps = mutableListOf(tpp2)

        val tpp1SecondTime = tppsRepository.getTpp(tpp1.id) as Success
        val tpp2SecondTime = tppsRepository.getTpp(tpp2.id) as Success

        // Both work because one is in remote and the other in cache
        assertThat(tpp1SecondTime.data.id).isEqualTo(tpp1.id)
        assertThat(tpp2SecondTime.data.id).isEqualTo(tpp2.id)
    }

    @Test
    fun getTpp_forceRefresh() = runBlockingTest {
        // Trigger the repository to load data, which loads from remote and caches
        tppsRemoteDataSource.tpps = mutableListOf(tpp1)
        tppsRepository.getTpp(tpp1.id)

        // Configure the remote data source to return a different tpp
        tppsRemoteDataSource.tpps = mutableListOf(tpp2)

        // Force refresh
        val tpp1SecondTime = tppsRepository.getTpp(tpp1.id, true)
        val tpp2SecondTime = tppsRepository.getTpp(tpp2.id, true)

        // Only tpp2 works because the cache and local were invalidated
        assertThat((tpp1SecondTime as? Success)?.data?.id).isNull()
        assertThat((tpp2SecondTime as? Success)?.data?.id).isEqualTo(tpp2.id)
    }

    @Test
    fun clearCompletedTpps() = runBlockingTest {
        val completedTpp = tpp1.copy().apply { isCompleted = true }
        tppsRemoteDataSource.tpps = mutableListOf(completedTpp, tpp2)
        tppsRepository.clearCompletedTpps()

        val tpps = (tppsRepository.getTpps(true) as? Success)?.data

        assertThat(tpps).hasSize(1)
        assertThat(tpps).contains(tpp2)
        assertThat(tpps).doesNotContain(completedTpp)
    }

    @Test
    fun deleteAllTpps() = runBlockingTest {
        val initialTpps = (tppsRepository.getTpps(true) as? Success)?.data

        // Delete all tpps
        tppsRepository.deleteAllTpps()

        // Fetch data again
        val afterDeleteTpps = (tppsRepository.getTpps(true) as? Success)?.data

        // Verify tpps are empty now
        assertThat(initialTpps).isNotEmpty()
        assertThat(afterDeleteTpps).isEmpty()
    }

    @Test
    fun deleteSingleTpp() = runBlockingTest {
        val initialTpps = (tppsRepository.getTpps(true) as? Success)?.data

        // Delete first tpp
        tppsRepository.deleteTpp(tpp1.id)

        // Fetch data again
        val afterDeleteTpps = (tppsRepository.getTpps(true) as? Success)?.data

        // Verify only one tpp was deleted
        assertThat(afterDeleteTpps?.size).isEqualTo(initialTpps!!.size - 1)
        assertThat(afterDeleteTpps).doesNotContain(tpp1)
    }
}

