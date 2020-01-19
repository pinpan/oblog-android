package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.google.gson.*
import java.lang.reflect.Type

class TppDeserializer : JsonDeserializer<Tpp> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Tpp {

        var jsonObject: JsonObject? = json?.asJsonObject

        return convertFrom(jsonObject)
    }


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
        var entityName = ""
        val nameJson = jsonObject?.get("entityName")
        if (nameJson is JsonArray) {
            entityName = getStringFromJsonArray(nameJson.asJsonArray)
        } else {
            entityName = nameJson.asString
        }

        val ebaProperties = jsonObject.get("ebaProperties").asJsonObject
        val cou = ebaProperties.get("ENT_COU_RES").asString

        //val entityName: String  = jsonObject?.get("entityName")?.getAsString() ?: ""
        val ebaEntityVersion: String = jsonObject?.get("ebaEntityVersion")?.asString ?: ""
        val status: String = jsonObject?.get("status")?.asString ?: ""
        val description: String = jsonObject?.get("description")?.asString ?: ""

        var tpp = Tpp(entityCode, entityName, description, jsonObject?.get("globalUrn")?.asString ?:"", ebaEntityVersion)
        tpp.country = cou

        return tpp
    }

    private fun getStringFromJsonArray(jsounArray: JsonArray): String {

        var theString = ""
        for (jsonArrayElement:JsonElement in jsounArray) run {
            theString += jsonArrayElement.asString.removeSurrounding("\"")
            theString += "\\"
        }
        return theString.trimEnd(',',' ')
    }
}