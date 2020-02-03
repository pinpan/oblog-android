package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.source.local.EbaPassport
import com.applego.oblog.tppwatch.data.source.local.OblogTypeConverters
import com.applego.oblog.tppwatch.data.source.local.Service
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.google.gson.*
import java.lang.reflect.Type

class TppServiceDeserializer : JsonDeserializer<Service> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Service {

        var jsonObject: JsonObject? = json?.asJsonObject

        return convertFrom(jsonObject)
    }

    val __oblogTypeConverters = OblogTypeConverters()

    companion object {
        private var INSTANCE: TppServiceDeserializer? = null
        //val tppDeserializer: TppServiceDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = TppServiceDeserializer()
                }
                return INSTANCE!!
            }
    }

    fun convertFrom(jsonObject: JsonObject?) : Service {
        if (jsonObject == null) {
            return Service();
        }

        val ebaEntityVersion: String = jsonObject?.get("ebaEntityVersion")?.asString ?: ""
        val description: String = jsonObject?.get("description")?.asString ?: ""

        val country = jsonObject.get("ENT_COU_RES").asString
        var service = Service(country, description, jsonObject?.get("globalUrn")?.asString ?:"")

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