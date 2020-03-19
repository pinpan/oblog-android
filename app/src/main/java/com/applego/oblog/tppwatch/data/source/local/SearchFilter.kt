package com.applego.oblog.tppwatch.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.applego.oblog.tppwatch.tpps.TppsFilterType
import com.applego.oblog.tppwatch.tpps.TppsFilterType.Companion.allFilterTypes
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

    //@ColumnInfo(name = "psd2_only") var psd2Only: Boolean = true,
    //@ColumnInfo(name = "revoked_only") var revokedOnly: Boolean = true,
    //@ColumnInfo(name = "show_fis") var showFis: Boolean = true,
    //@ColumnInfo(name = "followed") var followed: Boolean = true,
    //@ColumnInfo(name = "active") var active: Boolean = true,
    //@ColumnInfo(name = "installed") var installed: Boolean = true,

    @Embedded
    var userSelectedFilterTypes: MutableMap<TppsFilterType, Boolean> = HashMap<TppsFilterType, Boolean>()
/*
        get() {
            if (userSelectedFilterTypes == null) {
                userSelectedFilterTypes
            }
        }
        set(m : MutableMap<TppsFilterType, Boolean>) {
            if (userSelectedFilterTypes == null) {
                userSelectedFilterTypes = m
            }
        }
*/


    fun updateUserSelection(requestType: TppsFilterType) {

        var wasChecked = userSelectedFilterTypes.get(requestType) ?: false

        // If all was checked
        val isAllClicked = TppsFilterType.ALL_TPPS.equals(requestType)
        if (isAllClicked) {
            userSelectedFilterTypes.put(TppsFilterType.ALL_TPPS, !wasChecked)
            setAll(!wasChecked)
        } else {
            userSelectedFilterTypes.put(requestType, !wasChecked)
            userSelectedFilterTypes.put(TppsFilterType.ALL_TPPS, all)
        }
    }

    private fun setAll(b: Boolean) {
        allFilterTypes.forEach {userSelectedFilterTypes.put(it, b)}
    }

    val all: Boolean
        get() {
            if (userSelectedFilterTypes == null) {
                return false
            }

            allFilterTypes.forEach() {
                val value = userSelectedFilterTypes.get(it)
                if ((value == null) || !value) {
                    return false
                }
            }

            return true
        }

    val noneSelected: Boolean
        get() {
            return userSelectedFilterTypes.isNullOrEmpty()
        }

    val psd2Only: Boolean
        get() {
            return userSelectedFilterTypes.get(TppsFilterType.PSD2_TPPS) ?: false
        }

    val active: Boolean
        get() {
            return userSelectedFilterTypes.get(TppsFilterType.USED_TPPS) ?: false
        }

    val followed: Boolean
        get() {
            return userSelectedFilterTypes.get(TppsFilterType.FOLLOWED_TPPS) ?: false
        }

    val showFis: Boolean
        get() {
            return userSelectedFilterTypes.get(TppsFilterType.FIS_AS_TPPS) ?: false
        }

    val revokedOnly: Boolean
        get() {
            return userSelectedFilterTypes.get(TppsFilterType.REVOKED_TPPS) ?: false
        }

    val installed: Boolean
       get() {
           return userSelectedFilterTypes.get(TppsFilterType.USED_TPPS) ?: false
       }
}
