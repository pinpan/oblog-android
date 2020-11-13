package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.ListResponse
import com.google.gson.*
import java.lang.reflect.Type

class EbaEntitiesListResponseDeserializer : JsonDeserializer<ListResponse<Tpp>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ListResponse<Tpp> {

        var tppsListResponse = ListResponse<Tpp>()

        tppsListResponse.aList = TppListDeserializer.tppListDeserializer.convertFrom(json?.asJsonObject)
        tppsListResponse.paging = PagingDeserializer.pagingDeserializer.convertFrom(json?.asJsonObject)

        return tppsListResponse
    }

    companion object {
        private var INSTANCE: EbaEntitiesListResponseDeserializer? = null
        val tppsListResponseDeserializer: EbaEntitiesListResponseDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = EbaEntitiesListResponseDeserializer()
                }
                return INSTANCE!!
            }
    }
}