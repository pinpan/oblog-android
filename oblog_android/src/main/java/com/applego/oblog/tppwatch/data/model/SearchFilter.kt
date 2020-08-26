package com.applego.oblog.tppwatch.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.applego.oblog.tppwatch.tpps.TppsFilterType
import java.util.*

@Entity(tableName = "searchfilter")
data class SearchFilter @JvmOverloads constructor(
        @ColumnInfo(name = "entityName") var title: String = "",
        @ColumnInfo(name = "searchDescription") var searchDescription: Boolean = false,
        @ColumnInfo(name = "countries") var countries: String = "",
        @ColumnInfo(name = "services") var services: String = "",
        @ColumnInfo(name = "creationtime") var created: Long = 0L,

        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

    @Embedded
    var instType = InstType.INST_PIAI
    var showFollowedOnly = false
    var showUsedOnly = false
    var showRevoked = false
    var showRevokedOnly = false
    var showBranches = false
    var showAgents = false

    fun updateUserSelection(requestType: TppsFilterType) {
        when (requestType) {
            TppsFilterType.ALL_INST -> {
                instType = InstType.ALL
            }
            TppsFilterType.PI_INST -> {
                instType = InstType.INST_PI
            }
            TppsFilterType.AI_INST -> {
                instType = InstType.INST_AI
            }
            TppsFilterType.PIAI_INST -> {
                instType = InstType.INST_PIAI
            }
            TppsFilterType.E_PI_INST -> {
                instType = InstType.INST_EPI
            }
            TppsFilterType.EMONEY_INST -> {
                instType = InstType.INST_EMI
            }
            TppsFilterType.E_EMONEY_INST -> {
                instType = InstType.INST_EEMI
            }
            TppsFilterType.NON_PSD2_INST -> {
                instType = InstType.NON_PSD2_INST
            }
            TppsFilterType.CREDIT_INST -> {
                instType = InstType.CIs
            }

            TppsFilterType.BRANCHES -> {
                showBranches = !showBranches
            }
            TppsFilterType.AGENTS -> {
                showAgents = !showAgents
            }

            TppsFilterType.FOLLOWED -> {
                showFollowedOnly = !showFollowedOnly
            }
            TppsFilterType.USED -> {
                showUsedOnly = !showUsedOnly
            }

            TppsFilterType.REVOKED -> {
                showRevoked = !showRevoked
                if (!showRevoked) {
                    showRevokedOnly = false
                }
            }
            TppsFilterType.REVOKED_ONLY -> {
                showRevokedOnly = !showRevokedOnly
                if (showRevokedOnly) {
                    showRevoked = true
                }
            }
        }
    }

    fun init() {
        instType = InstType.ALL
        showFollowedOnly = false
        showUsedOnly = false
        showRevoked = false
        showRevokedOnly = false
        showBranches = false
        showAgents = false
    }
}
