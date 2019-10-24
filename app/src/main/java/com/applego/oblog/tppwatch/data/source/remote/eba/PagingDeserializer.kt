package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.Paging
import com.google.gson.*
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


    fun convertFrom(jsonObject: JsonObject?): Paging {
        var paging = Paging()

        if (jsonObject != null) {
            val pageableElement: JsonElement = jsonObject.get("pageable");
            if (pageableElement != null) {
                val pageableObject = pageableElement.asJsonObject
                val paged: Boolean = pageableObject.get("paged")?.asBoolean ?: false
                if (paged) {
                    paging.size = pageableObject.get("pageSize")?.asInt ?: -1
                    paging.page = pageableObject.get("pageNumber")?.asInt ?: -1
                    paging.offset = pageableObject.get("offset")?.asInt ?: -1
                    paging.first = jsonObject.get("first")?.asBoolean ?: false
                    paging.last = jsonObject.get("last")?.asBoolean ?: false
                    paging.totalPages = jsonObject.get("totalPages")?.asInt ?: -1
                    paging.totalElements = jsonObject.get("totalElements")?.asInt ?: -1
                    paging.sorted = false;
                    val sortJsonObject: JsonObject? = pageableObject.get("sort")?.asJsonObject ?: null
                    if (sortJsonObject != null) {
                        paging.sorted = sortJsonObject.get("sorted")?.asBoolean ?: false
                        //paging.sortBy = sortJsonObject.get("sorted")?.asBoolean ?: false
                    }
                }
            }
        }
        return paging
    }
}
