package com.applego.oblog.tppwatch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "apps")
data class App @JvmOverloads constructor(
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "description") var description: String? = "",
        @ColumnInfo(name = "web_link") var webAddr: String? = "",
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
)
{
    fun update(anAppName: String, aDescription: String, aWebAddr: String?) {
        if (anAppName != null) {
            name = anAppName
        }
        if (aDescription != null) {
            description = aDescription
        }
        if (aWebAddr != null) {
            webAddr = aWebAddr
        }
    }

    @ColumnInfo
    var tppId: String = ""
//    var followed: Boolean = true
//    var used: Boolean = true
}
