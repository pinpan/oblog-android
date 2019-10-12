package com.applego.oblog.tppwatch.data.source.local

import androidx.room.TypeConverter

class StatusConverter {
    @TypeConverter
    fun toStatus(status: Int): RecordStatus {
        return if (status == RecordStatus.NEW.statusId) {
            RecordStatus.NEW
        } else if (status == RecordStatus.DIRTY.statusId) {
            RecordStatus.DIRTY
        } else if (status == RecordStatus.UPDATED.statusId) {
            RecordStatus.UPDATED
        } else if (status == RecordStatus.REMOVED.statusId) {
            RecordStatus.REMOVED
        } else if (status == RecordStatus.DELETED.statusId) {
            RecordStatus.DELETED
        } else {
            throw IllegalArgumentException("Could not recognize status")
        }
    }

    @TypeConverter
    fun toInteger(recordStatus: RecordStatus): Int? {
        return recordStatus.statusId
    }
}