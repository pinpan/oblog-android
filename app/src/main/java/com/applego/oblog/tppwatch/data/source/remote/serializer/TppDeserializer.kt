package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityProperties
import com.google.gson.*
import java.lang.reflect.Type
import timber.log.Timber

class TppDeserializer : JsonDeserializer<Tpp> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Tpp? {

        var jsonObject: JsonObject? = json?.asJsonObject
        val tpp = convertFrom(jsonObject)
        return tpp
    }

    val __oblogTypeConverters = OblogTypeConverters()

    companion object {
        private var INSTANCE: TppDeserializer? = null
        val tppDeserializer: TppDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = TppDeserializer()
                }
                return INSTANCE!!
            }
    }

    fun convertFrom(jsonObject: JsonObject?) : Tpp? {
        if (jsonObject == null) {
            return null //Tpp()
        }

        val entityCode: String  = jsonObject?.get("entityCode")?.asString ?: ""
        var entityId: String   =  jsonObject.get("entityId")?.asString ?: entityCode
        if (!entityId.isNullOrBlank()) {
            entityId = getEntityId(entityId)
        }

        val ebaPropertiesJson = jsonObject.get("ebaProperties")?.asJsonObject

        val ent_add = ebaPropertiesJson?.get("ENT_ADD")
        val enityAddress = if (ent_add is JsonArray) getStringFromJsonArray(ent_add.asJsonArray) else (ent_add?.asString ?: "")

        /*when (ent_add) {
            is JsonArray -> {
                ent_add.asJsonArray.forEach() { addrPart ->
                    enityAddress += addrPart.asString + ", "
                }
                enityAddress = enityAddress.trim(',', ' ')
            }
            is JsonElement -> {
                enityAddress = ent_add.asString
            }
        }*/
        val entAuthStart_End = ebaPropertiesJson?.get("ENT_AUT")?.asJsonArray
        val entAuthStart = entAuthStart_End?.get(0)?.asString ?: ""
        var entAuthEnd = ""
        if (entAuthStart_End?.size()!! > 1) {
            entAuthEnd = entAuthStart_End?.get(1)?.asString ?: ""
        }

        val nameJson = jsonObject?.get("entityName")
        val entityName = if (nameJson is JsonArray) getStringFromJsonArray(nameJson.asJsonArray) else nameJson.asString

        val ebaProperties = EbaEntityProperties(
                  ebaPropertiesJson?.get("ENT_COD_TYP")?.asString ?: ""
                , ebaPropertiesJson?.get("ENT_NAT_REF_COD")?.asString ?: ""
                , entityName //ebaPropertiesJson?.get("ENT_NAM")?.asString ?: ""

                , enityAddress

                , ebaPropertiesJson?.get("ENT_TOW_CIT_RES")?.asString ?: ""
                , ebaPropertiesJson?.get("ENT_POS_COD")?.asString ?: ""
                , ebaPropertiesJson?.get("ENT_COU_RES")?.asString ?: ""
                , entAuthStart
                , entAuthEnd
        )

        val globalUrn = jsonObject?.get("globalUrn")?.asString ?:""
        val country: String = ebaProperties.countryOfResidence
        val ebaEntityVersion: String = jsonObject?.get("ebaEntityVersion")?.asString ?: ""
        // val status: String = jsonObject?.get("status")?.asString ?: ""
        val description: String = jsonObject?.get("description")?.asString ?: ""

        val ebaEntity = EbaEntity(_entityId = entityId, _entityCode = entityCode, _entityName = entityName, _description = description, _globalUrn = globalUrn, _ebaEntityVersion = ebaEntityVersion, _country = country)
        ebaEntity.ebaProperties = ebaProperties
        var tpp = Tpp(ebaEntity)

        val services = jsonObject?.get("services").asJsonArray ?: JsonArray()
        if (services != null) {
            val passportedServices = __oblogTypeConverters.fromJsonElementToEbaPassport(services)
            tpp.ebaEntity._ebaPassport = passportedServices
        }

        return tpp
    }

    private fun getEntityId(entityCode: String): String {

        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // "entityCode": "PSD_EMI!GB_FCA!900016",
        // "entityId":   "PSD_EMI!GB_FCA!900016",
        // "entityCode": "PSD_EMI!GB_FCA!900645"
        // "entityId":   "PSD_EMI!GB_FCA!900645",
        var entityId: String = entityCode
        if (!entityCode.isNullOrBlank()) {
            val codeParts: List<String>  = entityCode.split("!")
            if (codeParts.size == 0) {
                Timber.w("Tpp Entity code %s can't be split", entityCode)
            } else {
                entityId = codeParts.get(codeParts.size-1).trim()
            }
        }


        return entityId
    }

    private fun getStringFromJsonArray(jsounArray: JsonArray): String {

        var theString = ""
        for (jsonArrayElement:JsonElement in jsounArray) run {
            theString += jsonArrayElement.asString.removeSurrounding("\"")
            theString += "\\"
        }
        return theString.trimEnd(',',' ','\\')
    }
}


/**

{
    "content": [
        {
            "entityId": "CZ_CNB!03570967",
            "entityName": [
                "Benxy s.r.o. "
            ],
            "entityCode": "CZ_CNB!03570967",
            "services": [
                {
                    "CZ": [
                        "PS_03A",
                        "PS_03B",
                        "PS_03C",
                        "PS_05A",
                        "PS_05B",
                        "PS_070",
                        "PS_080"
                    ]
                }
            ],
            "ebaProperties": {
                "ENT_NAM": "Benxy s.r.o. ",
                "ENT_COD_TYP": "NON_LEI",
                "ENT_COU_RES": "CZ",
                "ENT_NAT_REF_COD": "03570967",
                "ENT_ADD": "Evropská 2690/17",
                "ENT_TOW_CIT_RES": "Praha",
                "ENT_POS_COD": "16000",
                "ENT_AUT": [
                "2019-04-12"
                ]
            }
        },

        {
            "entityId": "CZ_CNB!03570967",
            "entityName": [
                "Benxy s.r.o. "
            ],
            "entityCode": "CZ_CNB!03570967",
            "services": [
                {
                    "CZ": [
                        "PS_03A",
                        "PS_03B",
                        "PS_03C",
                        "PS_05A",
                        "PS_05B"
                    ]
                }
            ],
            "ebaProperties": {
                "ENT_NAM": "Benxy s.r.o. ",
                "ENT_COD_TYP": "NON_LEI",
                "ENT_COU_RES": "CZ",
                "ENT_NAT_REF_COD": "03570967",
                "ENT_ADD": "Evropská 2690/17",
                "ENT_TOW_CIT_RES": "Praha",
                "ENT_POS_COD": "16000",
                "ENT_AUT": [
                    "2015-03-27",
                    "2019-04-12"
                ]
            }
        }
    ],

    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "unpaged": false,
        "paged": true
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "number": 0,
    "size": 10,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 2,
    "first": true,
    "empty": false
}
*/


/* CNB - returns on
https://api.oblog.org:8443/api/nca-registry/cz/03570967

{
  "content": [
    {
      "entityId": "03570967",
      "entityName": [
        "Benxy s.r.o."
      ],
      "entityCode": "03570967",
      "services": [],
      "ebaProperties": {}
    }
  ],

  "pageable": "INSTANCE",
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "number": 0,
  "size": 1,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
*/
