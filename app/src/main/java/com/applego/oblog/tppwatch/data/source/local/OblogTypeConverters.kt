package com.applego.oblog.tppwatch.data.source.local

import android.R.attr.valueType
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*


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

    val ebaPassportsMapType = TypeToken.getParameterized(HashMap::class.java, String::class.java, List::class.java).type
/*

    TypeToken.getParameterized(HashMap.class, String.class, List<Service>.class) {
    }.getType()
*/

    val ebaPassportMapListType = object : TypeToken<List<Map<String, List<Service>>>>() {
    }.getType()

    val ebaPassportCountryMapType = object : TypeToken<Map<String, MutableList<Service>>>() {
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

   /* @TypeConverter
    fun fromJsonElementToEbaPassport(json : JsonElement) : EbaPassport {
        val aPassport: EbaPassport = gson.fromJson<EbaPassport>(json, ebaPassportType)
        return aPassport
    }*/

    @TypeConverter
    fun fromEbaPassportListToJsonElement(passportsList : List<Map<String, List<Service>>>) : JsonElement {
        val jsonElement = gson.toJsonTree(passportsList, ebaPassportMapListType)
        return jsonElement
    }


    @TypeConverter
    fun fromEbaPassportToString(pass : EbaPassport) : String {
        val jsonElement = gson.toJson(pass, ebaPassportType)

        return jsonElement
    }

    @TypeConverter
    fun fromStringToEbaPassport(json : String) : EbaPassport {
        return gson.fromJson(json, ebaPassportType)
        //val jsonElement : JsonElement = gson.fromJson(json, ebaPassportType)
        //return fromJsonElementToEbaPassport(jsonElement)
    }

   /* @TypeConverter
    fun fromJsonElementToEbaPassportCountryMap(json : String) : Map<String, List<Service>> {
        val countryMap : Map<String, List<Service>> = gson.fromJson(json, ebaPassportCountryMapType)
        return countryMap
    }

    @TypeConverter
    fun fromEbaPassportCountryMapToJsonElement(passCountryMap : Map<String, List<Service>>) : JsonElement {
        val jsonElement = gson.toJsonTree(passCountryMap, ebaPassportCountryMapType)

        return jsonElement
    }
*/
    @TypeConverter
    fun fromEbaPassportToJsonElement(pass : EbaPassport) : JsonElement {
        val jsonElement = gson.toJsonTree(pass, ebaPassportsMapType)

        return jsonElement
    }

    @TypeConverter
    fun fromJsonElementToEbaPassport(json : JsonElement) : EbaPassport {
        val aList: List<Map<String, Any>> = gson.fromJson(json, ebaPassportMapListType) //ebaPass   portCountryMapType) //ebaPassportsMapType) //<List<Map<String, String>>>

        //val ebaPassportsList = ArrayList<EbaPassport>()

        val ebaPassport = EbaPassport()
        val countryMap = HashMap<String, MutableList<Service>>()
        ebaPassport.countryMap = countryMap

        //val serviceMap = HashMap<EbaService, MutableList<String>>()
        //ebaPassport.serviceMap = serviceMap

        for (aMap in aList) {
            //ebaPassportsList.add(ebaPassport)

            val entrySet: Set<Map.Entry<String, Any>> = aMap.entries
            for (entry in entrySet) {
                val countryCode = entry.key

                val theServices = ArrayList<Service>()
                countryMap.put(countryCode, theServices)

                if (entry.value is String) {
                    val ebaService = EbaService.findService(entry.value as String)
                    /*var serviceCountries = serviceMap.get(ebaService)
                    if (serviceCountries == null) {
                        serviceCountries = mutableListOf<String>()
                        //serviceMap.put(ebaService, serviceCountries)
                    }*/
                     //        serviceCountries.add(entry.key)
                    // TODO: Make Service a shared immutable value object
                    theServices.add(Service(ebaService.code, ebaService.description))
                } else {
                    for (aService in entry.value as List<String>) {
                        val ebaService = EbaService.findService(aService)
                        //var serviceCountries = serviceMap.get(ebaService)
                        //if (serviceCountries == null) {
                        //    serviceCountries = mutableListOf<String>()
                        //    serviceMap.put(ebaService, serviceCountries)
                        //}
                        //serviceCountries.add(entry.key)

                        theServices.add(Service(ebaService.code, ebaService.description))
                    }
                }
            }
        }

        return ebaPassport
    }

    @TypeConverter
    fun fromEbaPassportsMapToJsonElement(aMap : Map<String, List<Service>>) : JsonElement {
        return gson.toJsonTree(aMap, ebaPassportsMapType)
    }

    @TypeConverter
    fun fromJsonStringToEbaPassportsMap(json : String) : Map<String, List<Service>> {
        val aMap: Map<String, List<Service>> = gson.fromJson<Map<String, List<Service>>>(json, ebaPassportCountryMapType)
        return aMap
    }

    @TypeConverter
    fun fromEbaPassportsMapToJsonString(aMap : Map<String, List<Service>>) : String {
        return gson.toJsonTree(aMap, ebaPassportCountryMapType).asString
    }

    @TypeConverter
    fun fromJsonElementToEbaPassportsMap(json : JsonElement) : Map<String, List<Service>> {
        val aMap: Map<String, List<Service>> = gson.fromJson<Map<String, List<Service>>>(json, ebaPassportsMapType)
        return aMap
    }

    @TypeConverter
    fun fromArrayLisr(list: ArrayList<String>) : String {
        val json = gson.toJson(list)
        return json
    }
}
