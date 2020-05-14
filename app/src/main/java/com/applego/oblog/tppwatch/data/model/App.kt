package com.applego.oblog.tppwatch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "apps")
data class App @JvmOverloads constructor(
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "description") var description: String? = "",
        @ColumnInfo(name = "web_link") var webLink: String? = "",
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
)
{
    @ColumnInfo
    var tppId: String = ""

    /*fun getId2() :  String {
      return this.id;
    }*/

    var followed: Boolean = true
    var used: Boolean = true

}
