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

package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Tpp
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Unit tests for [getActiveAndCompletedStats].
 */
class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted() {
        val tpps = listOf(
            Tpp("title", "desc", isCompleted = false)
        )
        // When the list of tpps is computed with an active tpp
        val result = getActiveAndCompletedStats(tpps)

        // Then the percentages are 100 and 0
        assertThat(result.activeTppsPercent, `is`(100f))
        assertThat(result.completedTppsPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_noActive() {
        val tpps = listOf(
            Tpp("title", "desc", isCompleted = true)
        )
        // When the list of tpps is computed with a completed tpp
        val result = getActiveAndCompletedStats(tpps)

        // Then the percentages are 0 and 100
        assertThat(result.activeTppsPercent, `is`(0f))
        assertThat(result.completedTppsPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_both() {
        // Given 3 completed tpps and 2 active tpps
        val tpps = listOf(
            Tpp("title", "desc", isCompleted = true),
            Tpp("title", "desc", isCompleted = true),
            Tpp("title", "desc", isCompleted = true),
            Tpp("title", "desc", isCompleted = false),
            Tpp("title", "desc", isCompleted = false)
        )
        // When the list of tpps is computed
        val result = getActiveAndCompletedStats(tpps)

        // Then the result is 40-60
        assertThat(result.activeTppsPercent, `is`(40f))
        assertThat(result.completedTppsPercent, `is`(60f))
    }

    @Test
    fun getActiveAndCompletedStats_error() {
        // When there's an error loading stats
        val result = getActiveAndCompletedStats(null)

        // Both active and completed tpps are 0
        assertThat(result.activeTppsPercent, `is`(0f))
        assertThat(result.completedTppsPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_empty() {
        // When there are no tpps
        val result = getActiveAndCompletedStats(emptyList())

        // Both active and completed tpps are 0
        assertThat(result.activeTppsPercent, `is`(0f))
        assertThat(result.completedTppsPercent, `is`(0f))
    }
}
