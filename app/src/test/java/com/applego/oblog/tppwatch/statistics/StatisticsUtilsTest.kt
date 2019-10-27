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

package com.applego.oblog.tppwatch.statistics

import com.applego.oblog.tppwatch.data.source.local.Tpp
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Unit tests for [getActiveAndFollowedStats].
 */
class StatisticsUtilsTest {

    @Test
    fun getActiveAndFollowedStats_noFollowed() {
        val tpps = listOf(
                Tpp("Entity_CZ28173281", "title", "desc")
        )
        // When the list of tpps is computed with an active tpp
        val result = getActiveAndFollowedStats(tpps)

        // Then the percentages are 100 and 0
        assertThat(result.activeTppsPercent, `is`(100f))
        assertThat(result.followedTppsPercent, `is`(0f))
    }

    @Test
    fun getActiveAndFollowedStats_noActive() {
        var tpp1 = Tpp("Entity_CZ28173281", "title", "desc")
        tpp1.isFollowed = true
        val tpps = listOf(
                tpp1
        )
        // When the list of tpps is computed with a followed tpp
        val result = getActiveAndFollowedStats(tpps)

        // Then the percentages are 0 and 100
        assertThat(result.activeTppsPercent, `is`(0f))
        assertThat(result.followedTppsPercent, `is`(100f))
    }

    @Test
    fun getActiveAndFollowedStats_both() {
        // Given 3 followed tpps and 2 active tpps
        var tpp1 = Tpp("Entity_CZ28173281", "title", "desc")
            tpp1.isFollowed = true
        var tpp2 = Tpp("Entity_CZ28173282", "title", "desc")
            tpp2.isFollowed = true
        var tpp3 = Tpp("Entity_CZ28173283", "title", "desc")
            tpp3.isFollowed = true

        var tpp4 = Tpp("Entity_CZ28173284", "title", "desc")
        var tpp5 = Tpp("Entity_CZ28173285", "title", "desc")

        val tpps = listOf(
                tpp1,
                tpp2,
                tpp3,
                tpp4,
                tpp5
        )
        // When the list of tpps is computed
        val result = getActiveAndFollowedStats(tpps)

        // Then the result is 40-60
        assertThat(result.activeTppsPercent, `is`(40f))
        assertThat(result.followedTppsPercent, `is`(60f))
    }

    @Test
    fun getActiveAndFollowdStats_error() {
        // When there's an error loading stats
        val result = getActiveAndFollowedStats(null)

        // Both active and followed tpps are 0
        assertThat(result.activeTppsPercent, `is`(0f))
        assertThat(result.followedTppsPercent, `is`(0f))
    }

    @Test
    fun getActiveAndFollowedStats_empty() {
        // When there are no tpps
        val result = getActiveAndFollowedStats(emptyList())

        // Both active and followed tpps are 0
        assertThat(result.activeTppsPercent, `is`(0f))
        assertThat(result.followedTppsPercent, `is`(0f))
    }
}
