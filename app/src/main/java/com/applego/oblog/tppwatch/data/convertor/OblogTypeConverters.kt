package com.applego.oblog.tppwatch.data.convertor

import androidx.room.TypeConverter
import com.applego.oblog.tppwatch.data.model.*
import com.applego.oblog.tppwatch.data.model.EUCountry.Companion.allEUCountriesMap
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
    fun toEntitytype(type: String) : EbaEntityType {
        return EbaEntityType.valueOf(type)
    }

    @TypeConverter
    fun fromEntitytype(type: EbaEntityType) : String {
        return type.code
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
        val aList: List<Map<String, Any /*List<Psd2Service>*/>> = gson.fromJson(json, ebaPassportMapListType)

        val ebaPassport = EbaPassport()
        //TODO-Transform: ebaPassport.serviceMaps = aList

        //val countryMap = HashMap<String, MutableList<Psd2Service>>()
        //ebaPassport.countryMap = countryMap

        val serviceMap = HashMap<String, MutableList<EUCountry>>()
        ebaPassport.serviceMap = serviceMap

        for (aMap in aList) {
            val countryCode: String = aMap.get("country") as String
            val servicesList = aMap.get("list") as ArrayList<String>

            val theServices = ArrayList<Psd2Service>()
            //countryMap.put(countryCode, theServices)

            for (aService in servicesList) {
                var serviceCountries = serviceMap.get(aService)
                if (serviceCountries == null) {
                    serviceCountries = ArrayList<EUCountry>()
                    serviceMap.put(aService, serviceCountries)
                }
                serviceCountries.add(allEUCountriesMap.get(countryCode) ?: EUCountry.NEU)

                val ebaService = EbaService.findService(aService)
                theServices.add(Psd2Service(ebaService.code, ebaService.psd2Code, ebaService.description))
            }
        }

        return ebaPassport
    }
}
