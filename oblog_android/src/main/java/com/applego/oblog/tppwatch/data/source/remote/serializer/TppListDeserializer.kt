package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.model.Tpp
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

    fun convertFrom(jsonObject: JsonObject?) : ArrayList<Tpp> {
        var items : ArrayList<Tpp> = arrayListOf()

        if (jsonObject != null) {

            val itemsJsonArray : JsonArray = jsonObject?.get("content")?.getAsJsonArray() ?: JsonArray();

            for (itemsJsonElement:JsonElement in itemsJsonArray) run {

                val itemJsonObject: JsonObject = itemsJsonElement.getAsJsonObject();
                var tpp : Tpp? = TppDeserializer.tppDeserializer.convertFrom(itemJsonObject)
                if (tpp != null) {
                    items.add(tpp)
                } else {
                    //TODO: log()
                }
            }
        }

        return items
    }
}