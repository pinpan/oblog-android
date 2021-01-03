package com.applego.oblog.tppwatch.statistics

import com.applego.oblog.tppwatch.data.model.Tpp

/**
 * Function that does some trivial computation. Used to showcase unit tests.
 */
/*
internal fun getUsedAndFollowedStats(tpps: List<Tpp>?): StatsResult {

    return if (tpps == null || tpps.isEmpty()) {
        StatsResult(0f, 0f)
    } else {
        val numberOfUsedTpps = tpps.count { it.isUsed()}
        val numberOfFollowedTpps = tpps.count { it.isFollowed()}
        StatsResult(
            usedTppsPercent = 100f * numberOfUsedTpps / tpps.size,
            followedTppsPercent = 100f * numberOfFollowedTpps / tpps.size
        )
    }
}
*/

//data class StatsResult(val usedTppsPercent: Float, val followedTppsPercent: Float)
