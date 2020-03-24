package com.applego.oblog.tppwatch.statistics

import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.model.EbaEntity
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Unit tests for [getActiveAndFollowedStats].
 */
class StatisticsUtilsTest {

    @Test
    fun getActiveAndFollowedStats_noFollowed() {
        val aTppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        aTppEntity.active = true
        val tpps = listOf(
                Tpp(aTppEntity)
        )
        // When the list of tpps is computed with an active ebaEntity
        val result = getActiveAndFollowedStats(tpps)

        // Then the percentages are 100 and 0
        assertThat(result.activeTppsPercent, `is`(100f))
        assertThat(result.followedTppsPercent, `is`(0f))
    }

    @Test
    fun getActiveAndFollowedStats_noActive() {
        var tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
        tppEntity1.followed = true
        val tpps = listOf(
                Tpp(tppEntity1)
        )
        // When the list of tpps is computed with a followed ebaEntity
        val result = getActiveAndFollowedStats(tpps)

        // Then the percentages are 0 and 100
        assertThat(result.activeTppsPercent, `is`(0f))
        assertThat(result.followedTppsPercent, `is`(100f))
    }

    @Test
    fun getActiveAndFollowedStats_both() {
        // Given 3 followed tpps and 2 active tpps
        var tpp1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
            tpp1.followed = true
        var tpp2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
            tpp2.followed = true
        var tpp3 = EbaEntity(_entityId = "28173283", _entityCode = "Entity_CZ28173283", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
            tpp3.followed = true

        var tpp4 = EbaEntity(_entityId = "28173284", _entityCode = "Entity_CZ28173284", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
            tpp4.active = true
        var tpp5 = EbaEntity(_entityId = "28173285", _entityCode = "Entity_CZ28173285", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz")
            tpp5.active = true

        val tpps = listOf(
                Tpp(tpp1),
                Tpp(tpp2),
                Tpp(tpp3),
                Tpp(tpp4),
                Tpp(tpp5)
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
