package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import com.google.gson.*
import java.lang.reflect.Type

class NcaEntitiesListResponseDeserializer : JsonDeserializer<TppsListResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TppsListResponse {

        var tppsListResponse = TppsListResponse()

        tppsListResponse.tppsList = TppListDeserializer.tppListDeserializer.convertFrom(json?.asJsonObject)
        tppsListResponse.paging = PagingDeserializer.pagingDeserializer.convertFrom(json?.asJsonObject)

        return tppsListResponse
    }

    companion object {
        private var INSTANCE: NcaEntitiesListResponseDeserializer? = null
        val tppsListResponseDeserializer: NcaEntitiesListResponseDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = NcaEntitiesListResponseDeserializer()
                }
                return INSTANCE!!
            }
    }
}