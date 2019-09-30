package com.applego.oblog.tppwatch.data.source.rest

import com.applego.oblog.tppwatch.data.Tpp
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class TppDeserializer : JsonDeserializer<Tpp> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Tpp {

        var jsonObject: JsonObject? = json?.asJsonObject

        val id: Int = jsonObject?.get("id")?.asInt ?: -1
        val name: String  = jsonObject?.get("name")?.getAsString() ?: ""
        val description: String = jsonObject?.get("description")?.asString ?: ""

        return Tpp(name, description)
    }
}