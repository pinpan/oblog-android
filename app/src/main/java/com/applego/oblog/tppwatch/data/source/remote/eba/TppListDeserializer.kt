package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.google.gson.*
import java.lang.reflect.Type
import kotlin.collections.ArrayList
import kotlin.collections.List

class TppListDeserializer : JsonDeserializer<List<Tpp>> {

    companion object {
        private var INSTANCE: TppListDeserializer? = null
        val tppListDeserializer: TppListDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = TppListDeserializer()
                }
                return INSTANCE!!
            }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<Tpp> {

        var items : List<Tpp> = convertFrom(json?.asJsonObject)

        return items
    }

    fun convertFrom(jsonObject: JsonObject?) : List<Tpp> {
        var items : ArrayList<Tpp> = arrayListOf()

        if (jsonObject != null) {

            val itemsJsonArray : JsonArray = jsonObject?.get("content")?.getAsJsonArray() ?: JsonArray();

            for (itemsJsonElement:JsonElement in itemsJsonArray) run {

                val itemJsonObject: JsonObject = itemsJsonElement.getAsJsonObject();
                var tpp : Tpp = TppDeserializer.tppDeserializer.convertFrom(itemJsonObject)

                items.add(tpp)
            }
        }

        return items
    }
}