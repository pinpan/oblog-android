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

import com.applego.oblog.tppwatch.data.Tpp

/**
 * Function that does some trivial computation. Used to showcase unit tests.
 */
internal fun getActiveAndCompletedStats(tpps: List<Tpp>?): StatsResult {

    return if (tpps == null || tpps.isEmpty()) {
        StatsResult(0f, 0f)
    } else {
        val totalTpps = tpps.size
        val numberOfActiveTpps = tpps.count { it.isActive }
        StatsResult(
            activeTppsPercent = 100f * numberOfActiveTpps / tpps.size,
            completedTppsPercent = 100f * (totalTpps - numberOfActiveTpps) / tpps.size
        )
    }
}

data class StatsResult(val activeTppsPercent: Float, val completedTppsPercent: Float)
