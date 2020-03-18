package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.source.local.OblogTypeConverters
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.TppEntity
import com.google.gson.*
import java.lang.reflect.Type

class TppDeserializer : JsonDeserializer<Tpp> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Tpp? {

        var jsonObject: JsonObject? = json?.asJsonObject
        val tpp = convertFrom(jsonObject)
        return tpp
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

    fun convertFrom(jsonObject: JsonObject?) : Tpp? {
        if (jsonObject == null) {
            return null //Tpp()
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

        var tpp = Tpp(TppEntity(entityCode, entityName, description, jsonObject?.get("globalUrn")?.asString ?:"", ebaEntityVersion, "cz"))
        tpp.tppEntity._country = cou

        val services = jsonObject?.get("services").asJsonArray ?: JsonArray()
        if (services != null) {
            val passportedServices = __oblogTypeConverters.fromJsonElementToEbaPassport(services)
            tpp.tppEntity._ebaPassport = passportedServices
        }

        return tpp
    }

    private fun getStringFromJsonArray(jsounArray: JsonArray): String {

        var theString = ""
        for (jsonArrayElement:JsonElement in jsounArray) run {
            theString += jsonArrayElement.asString.removeSurrounding("\"")
            theString += "\\"
        }
        return theString.trimEnd(',',' ','\\')
    }
}