package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.model.Psd2Service
import com.google.gson.*
import java.lang.reflect.Type

class TppServiceDeserializer : JsonDeserializer<Psd2Service> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Psd2Service {

        var jsonObject: JsonObject? = json?.asJsonObject

        return convertFrom(jsonObject)
    }

    companion object {
        private var INSTANCE: TppServiceDeserializer? = null
            get() {
                if (INSTANCE == null) {
                    INSTANCE = TppServiceDeserializer()
                }
                return INSTANCE!!
            }
    }

    fun convertFrom(jsonObject: JsonObject?) : Psd2Service {
        if (jsonObject == null) {
            return Psd2Service();
        }

        val ebaEntityVersion: String = jsonObject?.get("ebaEntityVersion")?.asString ?: ""
        val description: String = jsonObject?.get("description")?.asString ?: ""

        val country = jsonObject.get("ENT_COU_RES").asString
        var service = Psd2Service(country, description, jsonObject?.get("globalUrn")?.asString
                ?: "")

        return service
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