package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.google.gson.*
import java.lang.reflect.Type
import kotlin.collections.ArrayList
import kotlin.collections.List

class TppListDeserializer : JsonDeserializer<List<Tpp>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<Tpp> {

        var items : ArrayList<Tpp> = arrayListOf()

        var jsonObject: JsonObject? = json?.asJsonObject

        val itemsJsonArray : JsonArray = jsonObject?.get("content")?.getAsJsonArray() ?: JsonArray();

        for (itemsJsonElement:JsonElement in itemsJsonArray) run {

            val itemJsonObject: JsonObject = itemsJsonElement.getAsJsonObject();
            var tpp : Tpp = TppDeserializer.tppDeserializer.convertFrom(itemJsonObject)

            items.add(tpp)
        }

        return items
    }
}