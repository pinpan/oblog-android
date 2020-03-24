package com.applego.oblog.tppwatch.statistics

import com.applego.oblog.tppwatch.data.model.Tpp

/**
 * Function that does some trivial computation. Used to showcase unit tests.
 */
internal fun getActiveAndFollowedStats(tpps: List<Tpp>?): StatsResult {

    return if (tpps == null || tpps.isEmpty()) {
        StatsResult(0f, 0f)
    } else {
        val numberOfActiveTpps = tpps.count { it.isActive()}
        val numberOfFollowedTpps = tpps.count { it.isFollowed()}
        StatsResult(
            activeTppsPercent = 100f * numberOfActiveTpps / tpps.size,
            followedTppsPercent = 100f * numberOfFollowedTpps / tpps.size
        )
    }
}

data class StatsResult(val activeTppsPercent: Float, val followedTppsPercent: Float)
