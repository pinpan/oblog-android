package com.applego.oblog.tppwatch.data.source.local

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken


class OblogTypeConverters {

    val simpleDateFormat = SimpleDateFormat.getDateInstance()

    val gson = Gson()

    val ebaPassportType = object : TypeToken<EbaPassport>() {
    }.getType()

    val tppServiceType = object : TypeToken<Service>() {
    }.getType()

    val tppServiceListType = object : TypeToken<List<Service>>() {
    }.getType()

    val ebaPassportListType = object : TypeToken<List<EbaPassport>>() {
    }.getType()

    val ebaPassportsMapType = object : TypeToken<Map<String, List<EbaService>>>() {
    }.getType()

    val ebaPassportMapListType = object : TypeToken<List<Map<String, Service>>>() {
    }.getType()


    @TypeConverter
    //@JvmStatic
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
    //@JvmStatic
    fun toInteger(recordStatus: RecordStatus): Int? {
        return recordStatus.statusId
    }

    @TypeConverter
    //@JvmStatic
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    //@JvmStatic
    fun toTimestamp(date: Date?): Long? {
        return if (date == null) null else date.getTime().toLong()
    }

    @TypeConverter
    //@JvmStatic
    fun toDate(date: String?): Date? {
        return if (date == null) null else simpleDateFormat.parse(date)
    }

    @TypeConverter
    //@JvmStatic
    fun toDateString(date: Date?): String? {
        return if (date == null) null else date.toString()
    }


    @TypeConverter
    //@JvmStatic
    fun storedStringToServices(data: String?): List<Service> {
        if (data == null) {
            return Collections.emptyList()
        }

        return gson.fromJson(data, tppServiceListType)
    }

    @TypeConverter
    //@JvmStatic
    fun jsonToServices(data: String?): Service {
        return gson.fromJson(data, tppServiceListType)
    }

    @TypeConverter
    //@JvmStatic
    fun myObjectsToStoredString(myObjects: List<Service>): String {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun fromEbaPassportList(passports: List<EbaPassport>) : String {
        var jsonString = gson.toJson(passports)

        return jsonString
    }

    @TypeConverter
    fun toEbaPassportList(json : String) : List<EbaPassport> {
        val aList: List<EbaPassport> = gson.fromJson<List<EbaPassport>>(json, ebaPassportListType)
        return aList
    }

    @TypeConverter
    fun fromJsonElementToEbaService(json : JsonElement) : Service {
        val aService: Service = gson.fromJson<Service>(json, tppServiceType)
        return aService
    }

    @TypeConverter
    fun fromJsonElementToEbaServiceList(json : JsonElement) : List<Service> {
        val aList: List<Service> = gson.fromJson<List<Service>>(json, tppServiceListType)
        return aList
    }

    @TypeConverter
    fun fromJsonElementToEbaPassport(json : JsonElement) : EbaPassport {
        val aPassport: EbaPassport = gson.fromJson<EbaPassport>(json, ebaPassportType)
        return aPassport
    }

    @TypeConverter
    fun fromEbaPassportListToJsonElement(passportsList : List<Map<String, List<Service>>>) : JsonElement {
        val jsonElement = JsonArray()
        return jsonElement
    }

    @TypeConverter
    fun fromJsonElementToEbaPassportList(json : JsonElement) : List<EbaPassport> {
        val aList: List<Map<String, Any>> = gson.fromJson<List<Map<String, String>>>(json, ebaPassportMapListType)

        val ebaPassportsList = ArrayList<EbaPassport>()

        for (aMap in aList) {
            val ebaPassport = EbaPassport()
            ebaPassportsList.add(ebaPassport)

            val countryMap = HashMap<String, List<Service>>()
            ebaPassport.countryMap = countryMap

            val serviceMap = HashMap<EbaService, List<String>>()
            ebaPassport.serviceMap = serviceMap

            val entrySet: Set<Map.Entry<String, Any>> = aMap.entries
            for (entry in entrySet) {
                val countryCode = entry.key
                val theServices = ArrayList<Service>()
                countryMap.put(countryCode, theServices)
                if (entry.value is String) {
                    val ebaService = EbaService.findService(entry.value as String)
                    theServices.add(Service(ebaService.code, ebaService.description))
                } else {
                    for (aService in entry.value as List<String>) {
                        val ebaService = EbaService.findService(aService)
                        theServices.add(Service(ebaService.code, ebaService.description))
                    }
                }
            }
        }

        return ebaPassportsList
    }

    @TypeConverter
    fun fromJsonElementToEbaPassportsMap(json : JsonElement) : Map<String, List<EbaService>> {
        val aMap: Map<String, List<EbaService>> = gson.fromJson<Map<String, List<EbaService>>>(json, ebaPassportsMapType)
        return aMap
    }

    @TypeConverter
    fun fromArrayLisr(list: ArrayList<String>) : String {
        val json = gson.toJson(list)
        return json
    }
}
