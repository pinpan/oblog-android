package com.applego.oblog.tppwatch.data.source.local

import androidx.room.TypeConverters

@TypeConverters(OblogTypeConverters::class)
enum class RecordStatus (val statusId: Int, val code: String ) {
    UNDEFINED(-1, "undefined")
    ,
    NEW(1, "NEW")
    ,
    DIRTY(3, "DIRTY")
    ,
    UPDATED(2, "UPDATED")
    ,
    REMOVED(4, "REMOVED")
    ,
    DELETED(5, "DELETED");

    /*public fun getStatusId() : Int {
        return statusId;
    }*/

    companion object {

        fun getRecordStatus(statusCode: String)  : RecordStatus {
            var recStat = values().find {
                it.code == statusCode
            }
            if (recStat == null) {
                recStat = UNDEFINED
            }
            return recStat
        }
    }
}

