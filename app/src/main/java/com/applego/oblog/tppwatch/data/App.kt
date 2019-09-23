package com.applego.oblog.tppwatch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "apps")
data class App @JvmOverloads constructor(
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "description") var description: String = "",
        @ColumnInfo(name = "completed") var isCompleted: Boolean = false,
        @PrimaryKey @ColumnInfo(name = "appId") var id: String = UUID.randomUUID().toString()
)
{
    val titleForList: String
        get() = if (name.isNotEmpty()) name else description


    val isActive
        get() = !isCompleted

    val isEmpty
        get() = name.isEmpty() || description.isEmpty()

}
