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
        val ico = if ((icoJson!=null) && !icoJson.isJsonNull) icoJson.asString else ""

        val leiJson = ncaPropertiesJson?.get("lei")
        val lei = if ((leiJson!=null) && !leiJson.isJsonNull) leiJson.asString else ""

        val addressJson = ncaPropertiesJson?.get("adresa")
        val addressJsonNotNull : JsonObject = if ((addressJson!=null) && !addressJson.isJsonNull) addressJson.asJsonObject else JsonObject()
        val address = getAddress(addressJsonNotNull)

        val entAuthStartJson = ncaPropertiesJson?.get("datumVzniku")
        val entAuthStart = if ((entAuthStartJson != null) && !entAuthStartJson.isJsonNull) entAuthStartJson.asString else ""

        val entAuthEndJson = ncaPropertiesJson?.get("datumZaniku")
        var entAuthEnd = if ((entAuthEndJson != null) && !entAuthEndJson.isJsonNull) entAuthEndJson.asString else "N/A"

        val revoked = !entAuthEnd.isNullOrBlank() && !"N/A".contentEquals(entAuthEnd)

        // TODO: Replace with names corresponding to NCA API instead of using proprietary CNB czech field names
        val bankCodeJson = ncaPropertiesJson?.get("ciselni_kod")
        val bankCode = if ((bankCodeJson != null) && !bankCodeJson.isJsonNull) bankCodeJson.toString() else ""

        val ncaEntity = NcaEntity()
        ncaEntity._globalUrn = globalUrn
        ncaEntity._entityId = entityId
        ncaEntity._entityCode = entityCode
        ncaEntity._entityName = entityName
        ncaEntity._description = description
        ncaEntity._ncaEntityVersion = entityVersion
        ncaEntity._address = address
        ncaEntity.ico = ico
        ncaEntity.lei = lei
        ncaEntity.bankCode = bankCode
        ncaEntity.authStart = entAuthStart

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
