package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.source.remote.NcaEntitiesListResponse
import com.google.gson.*
import java.lang.reflect.Type

class NcaEntitiesListResponseDeserializer : JsonDeserializer<NcaEntitiesListResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): NcaEntitiesListResponse {

        var ncaEntitiesListResponse = NcaEntitiesListResponse()

        ncaEntitiesListResponse.aList = NcaEntitiesListDeserializer.ncaEntitiesListDeserializer.convertFrom(json?.asJsonObject)
        ncaEntitiesListResponse.paging = PagingDeserializer.pagingDeserializer.convertFrom(json?.asJsonObject)

        return ncaEntitiesListResponse
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