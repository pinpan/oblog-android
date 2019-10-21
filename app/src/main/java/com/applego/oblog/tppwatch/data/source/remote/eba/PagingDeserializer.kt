package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.Paging
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type


class PagingDeserializer: JsonDeserializer<Paging> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Paging {

        var jsonObject: JsonObject? = json?.asJsonObject

        return convertFrom(jsonObject)
    }

    companion object {
        private var INSTANCE: PagingDeserializer? = null
        val pagingDeserializer: PagingDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = PagingDeserializer()
                }
                return INSTANCE!!
            }
    }


    fun convertFrom(jsonObject: JsonObject?) : Paging {
        var paging = Paging()

        if (jsonObject != null) {

            val paged: Boolean = jsonObject.get("paged")?.asBoolean?: false
            if (paged) {

                paging.size = jsonObject.get("pageSize")?.asInt ?: -1
                paging.page = jsonObject.get("pageNumber")?.asInt ?: -1
                paging.offset = jsonObject.get("offset")?.asInt ?: -1
                paging.sorted =  false;
                val sortJsonObject: JsonObject? = jsonObject.get("sort")?.asJsonObject ?: null
                if (sortJsonObject != null) {
                    paging.sorted = sortJsonObject.get("sorted")?.asBoolean ?: false
                }
            }
        }

        return paging
    }
}
