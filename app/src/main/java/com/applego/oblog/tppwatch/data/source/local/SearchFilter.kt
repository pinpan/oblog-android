package com.applego.oblog.tppwatch.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "searchfilter")
data class SearchFilter @JvmOverloads constructor(
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "searchDescription") var searchDescription: Boolean = false,
        @ColumnInfo(name = "countries") var countries: String = "",
        @ColumnInfo(name = "services") var services: String = "",
        @ColumnInfo(name = "followed") var psd2Only: Boolean = true,
        @ColumnInfo(name = "followed") var revokedOnly: Boolean = true,
        @ColumnInfo(name = "followed") var showFis: Boolean = true,
        @ColumnInfo(name = "followed") var followed: Boolean = true,
        @ColumnInfo(name = "installed") var installed: Boolean = true,
        @ColumnInfo(name = "active") var active: Boolean = true,
        @ColumnInfo(name = "creationtime") var created: Long = 0L,
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

}
