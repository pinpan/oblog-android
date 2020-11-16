package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.google.gson.*
import java.lang.reflect.Type
import kotlin.collections.ArrayList
import kotlin.collections.List

class NcaEntitiesListDeserializer : JsonDeserializer<List<NcaEntity>> {

    companion object {
        private var INSTANCE: NcaEntitiesListDeserializer? = null
        val ncaEntitiesListDeserializer: NcaEntitiesListDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = NcaEntitiesListDeserializer()
                }
                return INSTANCE!!
            }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<NcaEntity> {

        var items : List<NcaEntity> = convertFrom(json?.asJsonObject)

        return items
    }

    fun convertFrom(jsonObject: JsonObject?) : ArrayList<NcaEntity> {
        var items : ArrayList<NcaEntity> = arrayListOf()

        if (jsonObject != null) {

            val itemsJsonArray : JsonArray = jsonObject?.get("content")?.getAsJsonArray() ?: JsonArray();

            for (itemsJsonElement:JsonElement in itemsJsonArray) run {

                val itemJsonObject: JsonObject = itemsJsonElement.getAsJsonObject();
                var ncaEntity : NcaEntity? = NcaEntityDeserializer.ncaEntityDeserializer.convertFrom(itemJsonObject)
                if (ncaEntity != null) {
                    items.add(ncaEntity)
                } else {
                    //TODO: log()
                }
            }
        }

        return items
    }
}