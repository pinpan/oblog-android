package com.applego.oblog.tppwatch.data.source.local

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return if (date == null) null else date.getTime().toLong()
    }

    @TypeConverter
    fun toDate(date: String?): Date? {
        return if (date == null) null else TheDateFormat.parse(date)
    }

    @TypeConverter
    fun toDateString(date: Date?): String? {
        return if (date == null) null else date.toString()
    }


    @TypeConverter
    fun storedStringToServices(data: String?): List<Service> {
        val gson = Gson()
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Service>>() {

        }.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun myObjectsToStoredString(myObjects: List<Service>): String {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    object TheDateFormat : SimpleDateFormat() {}
}
