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
    var pspType: PspType = PspType.ALL_PSD2
    var showFollowedOnly = false
    var showUsedOnly = false
    var showRevoked = false
    var showRevokedOnly = false

    fun updateUserSelection(requestType: TppsFilterType) {
        when (requestType) {
            TppsFilterType.ALL_TPPs -> {
                pspType = PspType.ALL_PSD2
            }
            TppsFilterType.PSD2_TPPs -> {
                pspType = PspType.PSD2_TPPs
            }
            TppsFilterType.E_MONEY_INSTs -> {
                pspType = PspType.EMIs
            }
            TppsFilterType.NON_PSD2_TPPs -> {
                pspType = PspType.NON_PSD2_TPPs
            }
            TppsFilterType.CREDIT_INSTs -> {
                pspType = PspType.CIs
            }

            TppsFilterType.FOLLOWED_TPPs -> {
                showFollowedOnly = !showFollowedOnly
            }
            TppsFilterType.USED_TPPs -> {
                showUsedOnly = !showUsedOnly
            }

            TppsFilterType.REVOKED_TPPs -> {
                showRevoked = !showRevoked
            }
            TppsFilterType.REVOKED_ONLY_TPPs -> {
                showRevokedOnly = !showRevokedOnly
            }
        }
    }

    fun init() {
        pspType = PspType.ALL_PSD2
        showFollowedOnly = false
        showUsedOnly = false
        showRevoked = false
        showRevokedOnly = false
    }
}
