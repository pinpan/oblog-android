package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import com.applego.oblog.tppwatch.data.model.*
import com.google.gson.*
import java.lang.reflect.Type
import timber.log.Timber
import java.util.*

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
        val entityType: String  = jsonObject?.get("entityType")?.asString ?: ""
        if (entityType.isNullOrBlank()) {
            Timber.w("Importing from registry an EBA TPP with empty entityType! It will not occur in type related statistics.")
        }
        var entityId: String   =  jsonObject.get("entityId")?.asString ?: entityCode
        if (!entityId.isNullOrBlank()) {
            entityId = getEntityId(entityId)
        }

        val ebaPropertiesJson = jsonObject.get("ebaProperties")?.asJsonObject
        val ent_add = ebaPropertiesJson?.get("ENT_ADD")
        val enityAddress = if (ent_add is JsonArray) getStringFromJsonArray(ent_add.asJsonArray) else (ent_add?.asString ?: "")
        val entAuthStart_End = ebaPropertiesJson?.get("ENT_AUT")?.asJsonArray
        val entAuthStart = entAuthStart_End?.get(0)?.asString ?: ""
        var entAuthEnd = "N/A"
        if (entAuthStart_End?.size()!! > 1) {
            entAuthEnd = entAuthStart_End?.get(1)?.asString ?: "N/A"
        }

        val nameJson = jsonObject.get("entityName")
        val entityName = if (nameJson is JsonArray) getStringFromJsonArray(nameJson.asJsonArray) else nameJson.asString

        var entityTowCitRes = ""
        val entTowCitRes = ebaPropertiesJson.get("ENT_TOW_CIT_RES")
        if (entTowCitRes == null) {
            // dummy
        } else if (entTowCitRes.isJsonArray) {
            // TODO: Concatenate all strings in the array
            entityTowCitRes = entTowCitRes.asJsonArray[0].asString
        } else {
            entityTowCitRes = entTowCitRes.asString
        }

        var entityPosCod = ""
        val entPosCod = ebaPropertiesJson.get("ENT_POS_COD")
        if (entPosCod == null) {
            // Dummy
        } else if (entPosCod.isJsonArray) {
            // TODO: Concatenate all strings in the array
            entityPosCod = entPosCod.asJsonArray[0].asString
        } else {
            entityPosCod = entPosCod.asString
        }

        var entityCouRes = ""
        val entCouRes = ebaPropertiesJson.get("ENT_COU_RES")
        if (entCouRes == null) {
            // dummy
        } else if (entCouRes.isJsonArray) {
            entityCouRes = entCouRes.asJsonArray[0].asString
        } else {
            entityCouRes = entCouRes.asString
        }

        val ebaProperties = EbaEntityProperties(
                  ebaPropertiesJson.get("ENT_COD_TYP")?.asString ?: ""
                , ebaPropertiesJson.get("ENT_NAT_REF_COD")?.asString ?: ""
                , entityName //ebaPropertiesJson?.get("ENT_NAM")?.asString ?: ""
                , enityAddress
                , entityTowCitRes
                , entityPosCod
                , entityCouRes
                , entAuthStart
                , entAuthEnd
        )

        val globalUrn = jsonObject.get("globalUrn")?.asString ?:""
        val country: String = ebaProperties.countryOfResidence
        val ebaEntityVersion: String = jsonObject.get("entityVersion")?.asString ?: ""
        // val status: String = jsonObject?.get("status")?.asString ?: ""
        val description: String = jsonObject.get("description")?.asString ?: ""

        val ebaEntity = EbaEntity(_entityId = entityId, _entityCode = entityCode, _entityName = entityName, _description = description, _globalUrn = globalUrn, _ebaEntityVersion = ebaEntityVersion, _country = country, entityType = EbaEntityType.valueOf(entityType))
        ebaEntity.ebaProperties = ebaProperties
        var tpp = Tpp(ebaEntity, NcaEntity())

        val services = jsonObject.get("services")
        try {
            //val servicesJsonArray = services?.asJsonArray ?: JsonArray()
            if (services != null) {
                val serviceJson = if ((services != null) && services.isJsonArray) services.asJsonArray else JsonArray()
                val passportedServices = __oblogTypeConverters.fromJsonElementToEbaPassport(serviceJson)
                tpp.ebaEntity._ebaPassport = passportedServices
            }
        } catch (t: Throwable ) {
            Timber.e(t)
        }

        return tpp
    }

    private fun getEntityId(entityCode: String): String {
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
