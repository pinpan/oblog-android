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
    var pspType: PspType = PspType.ALL_TPPS //MutableMap<TppsFilterType, Boolean> = HashMap<TppsFilterType, Boolean>()
    var showFollowedOnly = false //MutableMap<TppsFilterType, Boolean> = HashMap<TppsFilterType, Boolean>()
    var showUsedOnly = false //MutableMap<TppsFilterType, Boolean> = HashMap<TppsFilterType, Boolean>()
    var showRevoked = false //MutableMap<TppsFilterType, Boolean> = HashMap<TppsFilterType, Boolean>()

    //var userSelectedFilterTypes: MutableMap<TppsFilterType, Boolean> = HashMap<TppsFilterType, Boolean>()

    fun updateUserSelection(requestType: TppsFilterType) {
        when (requestType) {
            TppsFilterType.ALL_TPPS -> {
                pspType = PspType.ALL_TPPS
            }
            TppsFilterType.ONLY_FIS_AS_TPPS -> {
                pspType = PspType.ONLY_ASPSPs
            }
            TppsFilterType.ONLY_PSD2_TPPS -> {
                pspType = PspType.ONLY_AIS_PISP_CISP
            }
            TppsFilterType.FOLLOWED_TPPS -> {
                showFollowedOnly = !showFollowedOnly
            }
            TppsFilterType.USED_TPPS -> {
                showUsedOnly = !showUsedOnly
            }
            TppsFilterType.REVOKED_TPPS -> {
                showRevoked = !showRevoked
            }
        }
    }

    fun init() {
        pspType = PspType.ALL_TPPS
        showFollowedOnly = false
        showUsedOnly = false
        showRevoked = false
    }
}
