package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import com.google.gson.*
import java.lang.reflect.Type

class EbaEntitiesListResponseDeserializer : JsonDeserializer<TppsListResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TppsListResponse {

        var tppsListResponse = TppsListResponse()

        tppsListResponse.tppsList = TppListDeserializer.tppListDeserializer.convertFrom(json?.asJsonObject)
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