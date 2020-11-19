package com.applego.oblog.tppwatch.data.source.remote.serializer

import com.applego.oblog.tppwatch.data.model.*
import com.google.gson.*
import java.lang.reflect.Type
import timber.log.Timber

class NcaEntityDeserializer : JsonDeserializer<NcaEntity> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): NcaEntity? {

        var jsonObject: JsonObject? = json?.asJsonObject
        val ncaEntity = convertFrom(jsonObject)
        return ncaEntity
    }

    //val __oblogTypeConverters = OblogTypeConverters()

    companion object {
        private var INSTANCE: NcaEntityDeserializer? = null
        val ncaEntityDeserializer: NcaEntityDeserializer
            get() {
                if (INSTANCE == null) {
                    INSTANCE = NcaEntityDeserializer()
                }
                return INSTANCE!!
            }
    }

    fun convertFrom(jsonObject: JsonObject?) : NcaEntity? {
        if (jsonObject == null) {
            return null
        }

        val entityCode: String  = jsonObject?.get("entityCode")?.asString ?: ""

        var entityId: String   =  jsonObject.get("entityId")?.asString ?: entityCode
        if (!entityId.isNullOrBlank()) {
            entityId = getEntityId(entityId)
        }

        val nameJson = jsonObject.get("entityName")
        val entityName = if (nameJson is JsonArray) getStringFromJsonArray(nameJson.asJsonArray) else nameJson.asString

        val entityType: String  = jsonObject?.get("entityType")?.asString ?: ""
        if (entityType.isNullOrBlank()) {
            Timber.w("Importing from registry an EBA TPP with empty entityType! It will not occur in type related statistics.")
        }

        val description: String = jsonObject.get("description")?.asString ?: ""

        val globalUrn = jsonObject.get("globalUrn")?.asString ?:""

        val entityVersionJson = jsonObject.get("entityVersion")
        val entityVersion: String = (if ((entityVersionJson != null) && (!(entityVersionJson is JsonNull))) jsonObject.get("entityVersion") .asString else "")

        val ncaPropertiesJson = jsonObject.get("ncaProperties")?.asJsonObject

        val icoJson = ncaPropertiesJson?.get("ico")

        val addressJson = ncaPropertiesJson?.get("adresa")
        val addressJsonNotNull : JsonObject = if ((addressJson!=null) && !addressJson.isJsonNull) addressJson.asJsonObject else JsonObject()
        val address = getAddress(addressJsonNotNull)

/*
        val ent_add = ebaPropertiesJson?.get("ENT_ADD")
        val enityAddress = if (ent_add is JsonArray) getStringFromJsonArray(ent_add.asJsonArray) else (ent_add?.asString ?: "")
        val entAuthStart_End = ebaPropertiesJson?.get("ENT_AUT")?.asJsonArray
        val entAuthStart = entAuthStart_End?.get(0)?.asString ?: ""
        var entAuthEnd = "N/A"
        if (entAuthStart_End?.size()!! > 1) {
            entAuthEnd = entAuthStart_End?.get(1)?.asString ?: "N/A"
        }
        val revoked = !entAuthEnd.isNullOrBlank()
                            && !"N/A".contentEquals(entAuthEnd)


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
                , entityName
                , enityAddress
                , entityTowCitRes
                , entityPosCod
                , entityCouRes
                , entAuthStart
                , entAuthEnd
        )

*/
        val ncaEntity = NcaEntity()
        ncaEntity._globalUrn = globalUrn
        ncaEntity._entityId = entityId
        ncaEntity._entityCode = entityCode
        ncaEntity._entityName = entityName
        ncaEntity._description = description
        ncaEntity._ncaEntityVersion = entityVersion
        ncaEntity._address = address
        ncaEntity.ico = icoJson.toString()

        return ncaEntity
    }

    private fun getAddress(addressJson: JsonObject): Address {
        val address = Address()

        val ruianCodeJson = addressJson.get("ruian")
        address.ruianCode =  if( (ruianCodeJson != null) && !ruianCodeJson.isJsonNull) ruianCodeJson.asString else ""

        val countryJson = addressJson.get("stat")
        address.country =  if( (countryJson!=null) && !countryJson.isJsonNull) countryJson.asString else ""

        val municipalityJson = addressJson.get("obec")
        address.municipality =  if( (municipalityJson!=null) && !municipalityJson.isJsonNull) municipalityJson.asString else ""

        val quarterJson = addressJson.get("castObce")
        address.quarter =  if( (quarterJson!=null) && !quarterJson.isJsonNull) quarterJson.asString else ""

        val zipCodeJson = addressJson.get("psc")
        address.zipCode =  if( (zipCodeJson!=null) && !zipCodeJson.isJsonNull) zipCodeJson.asString else ""

        val streetJson = addressJson.get("ulice")
        address.street =  if( (streetJson!=null) && !streetJson.isJsonNull) streetJson.asString else ""

        val descriptionalNumberJson = addressJson.get("cisloPopisne")
        address.descriptionalNumber =  if( (descriptionalNumberJson!=null) && !descriptionalNumberJson.isJsonNull) descriptionalNumberJson.asInt else -1

        val orientationalNumberJson = addressJson.get("cisloOrientacni")
        address.orientationalNumber =  if ((orientationalNumberJson != null) && !orientationalNumberJson.isJsonNull) orientationalNumberJson.asInt else -1

        val discriminatorLetterJson = addressJson.get("cisloOrientacniPismeno")
        address.discriminatorLetter =  if( (discriminatorLetterJson!=null) && !discriminatorLetterJson.isJsonNull) discriminatorLetterJson.asString else ""

        val unstructuredAddressJson = addressJson.get("nestrukturovanaAdresa")
        address.unstructuredAddress =  if( (unstructuredAddressJson != null) && !unstructuredAddressJson.isJsonNull) unstructuredAddressJson.asString else ""

        return address
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
            theString += jsonArrayElement.asString.trim().removeSurrounding("\"").trim()
            theString += "\\"
        }
        return theString.trimEnd(',',' ','\\')
    }
}
