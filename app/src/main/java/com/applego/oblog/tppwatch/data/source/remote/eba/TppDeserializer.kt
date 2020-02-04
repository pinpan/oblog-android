package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.source.local.EbaPassport
import com.applego.oblog.tppwatch.data.source.local.OblogTypeConverters
import com.applego.oblog.tppwatch.data.source.local.Service
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*
import java.util.Locale.filter
import java.util.stream.Collectors

class TppDeserializer : JsonDeserializer<Tpp> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Tpp {

        var jsonObject: JsonObject? = json?.asJsonObject

        return convertFrom(jsonObject)
    }

    val __oblogTypeConverters = OblogTypeConverters()

    companion object {
        private var INSTANCE: TppDeserializer? = null
        val tppDeserializer: TppDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = TppDeserializer()
                }
                return INSTANCE!!
            }
    }

    fun convertFrom(jsonObject: JsonObject?) : Tpp {
        if (jsonObject == null) {
            return Tpp();
        }
        val entityId: String = jsonObject.get("entityId")?.asString ?:""
        val entityCode: String  = jsonObject?.get("entityCode")?.getAsString() ?: ""

        val nameJson = jsonObject?.get("entityName")
        var entityName = if (nameJson is JsonArray) getStringFromJsonArray(nameJson.asJsonArray) else nameJson.asString

        val ebaProperties = jsonObject.get("ebaProperties").asJsonObject
        val cou = ebaProperties.get("ENT_COU_RES").asString

        val ebaEntityVersion: String = jsonObject?.get("ebaEntityVersion")?.asString ?: ""
        // val status: String = jsonObject?.get("status")?.asString ?: ""
        val description: String = jsonObject?.get("description")?.asString ?: ""

        var tpp = Tpp(entityCode, entityName, description, jsonObject?.get("globalUrn")?.asString ?:"", ebaEntityVersion)
        tpp.country = cou

        val services = jsonObject?.get("services").asJsonArray ?: JsonArray()
        if (services != null) {
            val allPassportedServices = __oblogTypeConverters.fromJsonElementToEbaPassportList(services)
            tpp.ebaPassports = allPassportedServices
        }

        return tpp
    }

/*
    @Override
    fun deserialize(elem : JsonElement, type : Type, jsonDeserializationContext : JsonDeserializationContext) : Map<String, Date> {
        return elem.getAsJsonObject()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().isJsonPrimitive())
            .filter(e -> e.getValue().getAsJsonPrimitive().isString())
            .collect(Collectors.toMap(Map.Entry::getKey, e -> formatDate(e.getValue())));
     }
*/

    private fun getStringFromJsonArray(jsounArray: JsonArray): String {

        var theString = ""
        for (jsonArrayElement:JsonElement in jsounArray) run {
            theString += jsonArrayElement.asString.removeSurrounding("\"")
            theString += "\\"
        }
        return theString.trimEnd(',',' ','\\')
    }
}