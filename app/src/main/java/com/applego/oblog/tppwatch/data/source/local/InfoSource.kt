package com.applego.oblog.tppwatch.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Immutable model class for a InfoSource.
 * In order to compile with Room, we can't use @JvmOverloads to generate multiple constructors.
 *
 * @param title       title of the InfoSource
 * @param id          id of the InfoSource
 */
@Entity(tableName = "infosources")
data class InfoSource @JvmOverloads constructor(
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "sourceType") var sourceType: String = "",  // TODO: Define SourceType enum - GOV, MEDIA, SOCIAL, EBA, NCA...
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

}
