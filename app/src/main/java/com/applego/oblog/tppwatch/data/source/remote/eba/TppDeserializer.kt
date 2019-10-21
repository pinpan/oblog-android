package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.source.local.RecordStatus
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
            entityName = getStringFromJsonArray(jsonObject?.get("entityName").asJsonArray)
        } else {
            entityName = nameJson.asString
        }

        //val entityName: String  = jsonObject?.get("entityName")?.getAsString() ?: ""
        val ebaEntityVersion: String = jsonObject?.get("ebaEntityVersion")?.asString ?: ""
        val status: String = jsonObject?.get("status")?.asString ?: ""
        val description: String = jsonObject?.get("description")?.asString ?: ""

        return Tpp(entityCode, entityName, description, false, jsonObject?.get("globalUrn")?.asString ?:"", RecordStatus.getRecordStatus(status), ebaEntityVersion) //, entityId)
    }

    private fun getStringFromJsonArray(jsounArray: JsonArray): String {

        var theString = ""
        for (jsonArrayElement:JsonElement in jsounArray) run {
            theString += jsonArrayElement
            theString += ", "
        }
        return theString.trimEnd(',',' ')
    }
}