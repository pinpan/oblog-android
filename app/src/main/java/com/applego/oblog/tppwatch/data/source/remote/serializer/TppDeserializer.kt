package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.source.local.OblogTypeConverters
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.TppEntity
import com.google.gson.*
import java.lang.reflect.Type

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

        val entityId: String = jsonObject.get("entityId")?.asString ?:""
        val entityCode: String  = jsonObject?.get("entityCode")?.getAsString() ?: ""

        val nameJson = jsonObject?.get("entityName")
        var entityName = if (nameJson is JsonArray) getStringFromJsonArray(nameJson.asJsonArray) else nameJson.asString

        val ebaProperties = jsonObject.get("ebaProperties")?.asJsonObject
        val cou = ebaProperties?.get("ENT_COU_RES")?.asString

        val ebaEntityVersion: String = jsonObject?.get("ebaEntityVersion")?.asString ?: ""
        // val status: String = jsonObject?.get("status")?.asString ?: ""
        val description: String = jsonObject?.get("description")?.asString ?: ""

        var tpp = Tpp(TppEntity(_entityId = "", _entityCode = entityCode, _entityName = entityName, _description = description, _globalUrn = jsonObject?.get("globalUrn")?.asString ?:"", _ebaEntityVersion = ebaEntityVersion, _country = "cz"))
        tpp.tppEntity._country = cou ?: ""

        val services = jsonObject?.get("services").asJsonArray ?: JsonArray()
        if (services != null) {
            val passportedServices = __oblogTypeConverters.fromJsonElementToEbaPassport(services)
            tpp.tppEntity._ebaPassport = passportedServices
        }

        return tpp
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
