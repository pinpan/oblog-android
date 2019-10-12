package com.applego.oblog.tppwatch.data.source.local

import androidx.room.TypeConverter
import androidx.room.TypeConverters

@TypeConverters(StatusConverter::class)
enum class RecordStatus (val statusId: Int, val code: String ) {
      NEW (1, "NEW")
    , DIRTY(3, "DIRTY")
    , UPDATED(2, "UPDATED")
    , REMOVED(4, "REMOVED")
    , DELETED(5, "DELETED")

    /*public fun getStatusId() : Int {
        return statusId;
    }*/

}