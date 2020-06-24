package com.applego.oblog.tppwatch.statistics

import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.NcaEntity
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Unit tests for [getUsedAndFollowedStats].
 */
class StatisticsUtilsTest {

    @Test
    fun getUsedAndFollowedStats_noFollowed() {
        val aTppEntity = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
        aTppEntity.used = true
        val tpps = listOf(
                Tpp(aTppEntity, NcaEntity())
        )
        // When the list of tpps is computed with an used ebaEntity
        val result = getUsedAndFollowedStats(tpps)

        // Then the percentages are 100 and 0
        assertThat(result.usedTppsPercent, `is`(100f))
        assertThat(result.followedTppsPercent, `is`(0f))
    }

    @Test
    fun getUsedAndFollowedStats_noUsed() {
        var tppEntity1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
        tppEntity1.followed = true
        val tpps = listOf(
                Tpp(tppEntity1, NcaEntity())
        )
        // When the list of tpps is computed with a followed ebaEntity
        val result = getUsedAndFollowedStats(tpps)

        // Then the percentages are 0 and 100
        assertThat(result.usedTppsPercent, `is`(0f))
        assertThat(result.followedTppsPercent, `is`(100f))
    }

    @Test
    fun getUsedAndFollowedStats_both() {
        // Given 3 followed tpps and 2 used tpps
        var tpp1 = EbaEntity(_entityId = "28173281", _entityCode = "Entity_CZ28173281", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
            tpp1.followed = true
        var tpp2 = EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
            tpp2.followed = true
        var tpp3 = EbaEntity(_entityId = "28173283", _entityCode = "Entity_CZ28173283", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
            tpp3.followed = true

        var tpp4 = EbaEntity(_entityId = "28173284", _entityCode = "Entity_CZ28173284", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
            tpp4.used = true
        var tpp5 = EbaEntity(_entityId = "28173285", _entityCode = "Entity_CZ28173285", _entityName = "entityName", _description = "desc", _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE)
            tpp5.used = true

        val tpps = listOf(
                Tpp(tpp1, NcaEntity()),
                Tpp(tpp2, NcaEntity()),
                Tpp(tpp3, NcaEntity()),
                Tpp(tpp4, NcaEntity()),
                Tpp(tpp5, NcaEntity())
        )
        // When the list of tpps is computed
        val result = getUsedAndFollowedStats(tpps)

        // Then the result is 40-60
        assertThat(result.usedTppsPercent, `is`(40f))
        assertThat(result.followedTppsPercent, `is`(60f))
    }

    @Test
    fun getUsedAndFollowdStats_error() {
        // When there's an error loading stats
        val result = getUsedAndFollowedStats(null)

        // Both used and followed tpps are 0
        assertThat(result.usedTppsPercent, `is`(0f))
        assertThat(result.followedTppsPercent, `is`(0f))
    }

    @Test
    fun getUsedAndFollowedStats_empty() {
        // When there are no tpps
        val result = getUsedAndFollowedStats(emptyList())

        // Both used and followed tpps are 0
        assertThat(result.usedTppsPercent, `is`(0f))
        assertThat(result.followedTppsPercent, `is`(0f))
    }
}
