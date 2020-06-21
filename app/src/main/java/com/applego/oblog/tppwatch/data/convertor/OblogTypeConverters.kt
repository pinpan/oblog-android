package com.applego.oblog.tppwatch.data.convertor

import androidx.room.TypeConverter
import com.applego.oblog.tppwatch.data.model.EbaPassport
import com.applego.oblog.tppwatch.data.model.EbaService
import com.applego.oblog.tppwatch.data.model.Psd2Service
import com.applego.oblog.tppwatch.data.source.local.RecordStatus
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.util.*

class OblogTypeConverters {

    val gson = Gson()

    val ebaPassportType = object : TypeToken<EbaPassport>() {
    }.getType()

    val ebaPassportMapListType = object : TypeToken<List<Map<String, List<Psd2Service>>>>() {
    }.getType()

    val ebaPassportCountryMapType = object : TypeToken<Map<String, MutableList<Psd2Service>>>() {
    }.getType()

    @TypeConverter
    fun toStatus(status: Int): RecordStatus {
        return if (status == RecordStatus.UNDEFINED.statusId) {
            RecordStatus.UNDEFINED
        } else if (status == RecordStatus.NEW.statusId) {
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
    fun fromEbaPassportToString(pass : EbaPassport) : String {
        return gson.toJson(pass, ebaPassportType)
    }

    @TypeConverter
    fun fromStringToEbaPassport(json : String) : EbaPassport {
        return gson.fromJson(json, ebaPassportType)
    }

    @TypeConverter
    fun fromJsonElementToEbaPassport(json : JsonElement) : EbaPassport {
        val aList: List<Map<String, List<Psd2Service>>> = gson.fromJson(json, ebaPassportMapListType)

        val ebaPassport = EbaPassport()
        ebaPassport.serviceMaps = aList

        val countryMap = HashMap<String, MutableList<Psd2Service>>()
        ebaPassport.countryMap = countryMap

        for (aMap in aList) {
            val entrySet: Set<Map.Entry<String, Any>> = aMap.entries
            for (entry in entrySet) {
                val countryCode = entry.key

                val theServices = ArrayList<Psd2Service>()
                countryMap.put(countryCode, theServices)

                if (entry.value is String) {
                    val ebaService = EbaService.findService(entry.value as String)
                    theServices.add(Psd2Service(ebaService.code, ebaService.psd2Code, ebaService.description))
                } else {
                    for (aService in entry.value as List<String>) {
                        val ebaService = EbaService.findService(aService)
                        theServices.add(Psd2Service(ebaService.code, ebaService.psd2Code, ebaService.description))
                    }
                }
            }
        }

        return ebaPassport
    }
}
