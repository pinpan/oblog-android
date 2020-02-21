package com.applego.oblog.tppwatch.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.applego.oblog.tppwatch.tpps.TppsFilterType
import java.util.*

@Entity(tableName = "searchfilter")
data class SearchFilter @JvmOverloads constructor(
        @ColumnInfo(name = "title") var title: String = "",
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
    val userInterests: MutableList<TppsFilterType> = ArrayList()

    fun updateUserInterests(requestType: TppsFilterType) {
        // If already set -> remove it
        userInterests.forEach() {
            if (it.equals(requestType)) {
                userInterests.remove(it)
                return
            }
        }

        // Otherwise add it
        userInterests.add(requestType)
    }

    val all: Boolean
        get() {
            userInterests.forEach() {
                if (it.equals(TppsFilterType.ALL_TPPS)) {
                    return true
                }
            }
            return false
        }

    val psd2Only: Boolean
        get() {
            userInterests.forEach() {
                if (it.equals(TppsFilterType.PSD2_ONLY_TPPS)) {
                    return true
                }
            }
            return false
        }

    val active: Boolean
        get() {
            userInterests.forEach() {
                if (it.equals(TppsFilterType.USED_TPPS)) {
                    return true
                }
            }
            return false
        }

    val followed: Boolean
        get() {
            userInterests.forEach() {
                if (it.equals(TppsFilterType.FOLLOWED_TPPS)) {
                    return true
                }
            }
            return false
        }

    val showFis: Boolean
        get() {
            userInterests.forEach() {
                if (it.equals(TppsFilterType.FIS_AS_TPPS)) {
                    return true
                }
            }
            return false
        }

    val revokedOnly: Boolean
        get() {
            userInterests.forEach() {
                if (it.equals(TppsFilterType.REVOKED_ONLY_TPPS)) {
                    return true
                }
            }
            return false
        }

    val installed: Boolean
       get() {
           userInterests.forEach() {
               if (it.equals(TppsFilterType.USED_TPPS)) {
                   return true
               }
           }
           return false
       }

    fun allFilterTypes() : List<TppsFilterType> {
        var theList : ArrayList<TppsFilterType> = ArrayList<TppsFilterType>()
        theList.add(TppsFilterType.FIS_AS_TPPS)
        theList.add(TppsFilterType.PSD2_ONLY_TPPS)
        theList.add(TppsFilterType.USED_TPPS)
        theList.add(TppsFilterType.FOLLOWED_TPPS)
        theList.add(TppsFilterType.ONLY_PSD2_TPPS)

        return theList
    }

}
