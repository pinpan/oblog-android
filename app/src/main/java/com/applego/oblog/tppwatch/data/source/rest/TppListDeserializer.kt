package com.applego.oblog.tppwatch.data.source.rest

import com.applego.oblog.tppwatch.data.Tpp
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List

class TppListDeserializer : JsonDeserializer<List<Tpp>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<Tpp> {

        //val items = ArrayList()
        var items : ArrayList<Tpp> = arrayListOf()

        var jsonObject: JsonObject? = json?.asJsonObject

        val itemsJsonArray : JsonArray = jsonObject?.get("content")?.getAsJsonArray() ?: JsonArray();

        for (itemsJsonElement:JsonElement in itemsJsonArray) run {
            val itemJsonObject: JsonObject = itemsJsonElement.getAsJsonObject();
            val id: String = itemJsonObject.get("entityId")?.asString ?:""
            val name: String = itemJsonObject.get("entityName")?.getAsString() ?: ""
            val description: String = itemJsonObject?.get("description")?.asString ?: ""

            items.add(Tpp(name , description));
        }

        return items
    }
}