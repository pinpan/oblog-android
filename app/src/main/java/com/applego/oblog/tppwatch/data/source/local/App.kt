package com.applego.oblog.tppwatch.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "apps")
data class App @JvmOverloads constructor(
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "description") var description: String = "",
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
)
{
}
