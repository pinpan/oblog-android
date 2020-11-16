package com.applego.oblog.tppwatch.data.model

import androidx.room.*
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import com.applego.oblog.tppwatch.data.source.local.RecordStatus
import java.util.*

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param entityId    TPP legal entity ID provided by home country
 * @param entityCode  entityCode assigned by EBA. Inconsistently includes TPP home country, Tpp Home NCA ID and TPP's NCA given ID
 * @param entityName  entityName of the tpp
 * @param description description of the tpp
 * @param globalUrn  entity globalUrn assigned by OBLOG
 * @param ebaEntityVersion EBA entity version
 * @param country  entity country of origin
 *
 * @param db_id  OBLOG database id of the tpp
 */
@Entity(tableName = "tpps")
@TypeConverters(OblogTypeConverters::class)
data class EbaEntity @JvmOverloads constructor(
        @ColumnInfo(name = "entityId") val _entityId: String = "",
        @ColumnInfo(name = "entityCode") val _entityCode: String = "",
        @ColumnInfo(name = "entityName") var _entityName: String,
        @ColumnInfo(name = "description") var _description: String,
        @ColumnInfo(name = "globalUrn") val _globalUrn: String,
        @ColumnInfo(name = "ebaEntityVersion") var _ebaEntityVersion: String,
        @ColumnInfo(name = "country") val _country: String,
        @ColumnInfo(name = "entityType") val _entityType: EbaEntityType, //  TODO: Move up after entityName Fix tests!
        @PrimaryKey @ColumnInfo(name = "db_id") var _db_id: String = UUID.randomUUID().toString()
) : OblogEntity() {

    @Ignore
    fun getEntityId() = _entityId

    @Ignore
    fun getEntityCode() = _entityCode

    @Ignore
    fun getEntityType() = _entityType

    @Ignore
    fun getEntityName() = _entityName

    @Ignore
    fun getDescription() = _description

    @Ignore
    fun getGlobalUrn() = _globalUrn

    @Ignore
    fun getEbaEntityVersion() = _ebaEntityVersion

    @Ignore
    fun getCountry() = _country

    @Ignore
    fun getId() = _db_id

    @Embedded
    var ebaProperties = EbaEntityProperties()

    @Ignore
    fun getAuthorizationDate() = ebaProperties.authorizationStart
/*

    @ColumnInfo(name = "psd2")
    var psd2: Boolean = (entityType.equals(EbaEntityType.PSD_PI)
                      || entityType.equals(EbaEntityType.PSD_EPI)
                      || entityType.equals(EbaEntityType.PSD_AISP)
                      || entityType.equals(EbaEntityType.PSD_AG)
                      || entityType.equals(EbaEntityType.PSD_BR)
            )
*/

/*
    @ColumnInfo(name = "fis")
    var fis: Boolean = !psd2

*/
    //@ColumnInfo(name = "followed")
    //var followed: Boolean = false

    //@ColumnInfo(name = "used")
    //var used: Boolean = false

    @ColumnInfo(name = "revoked")
    var revoked: Boolean = false

    //@ColumnInfo(name = "status")
    //var _status: RecordStatus = RecordStatus.NEW

    var _ebaPassport : EbaPassport = EbaPassport()

    @Ignore
    fun getEbaPassport() = _ebaPassport

    @Ignore
    fun isFollowed() : Boolean = followed

    @Ignore
    fun isUsed() : Boolean = used

    @Ignore
    fun isRevoked(): Boolean = revoked //!ebaProperties.authorizationEnd.isNullOrBlank()
                            //&& !"N/A".contentEquals(ebaProperties.authorizationEnd)

    @Ignore
    fun isAllPSD2(): Boolean = (
           _entityType.equals(EbaEntityType.PSD_PISP)
        || _entityType.equals(EbaEntityType.PSD_AISP)
        || _entityType.equals(EbaEntityType.PSD_EPI)
        || _entityType.equals(EbaEntityType.PSD_EMI)
        || _entityType.equals(EbaEntityType.PSD_EEMI)
        || _entityType.equals(EbaEntityType.PSD_EXC)
        || _entityType.equals(EbaEntityType.PSD_ENL)
        //|| entityType.equals(EbaEntityType.PSD_BR)
        //|| entityType.equals(EbaEntityType.PSD_AG)
            )

    @Ignore
    fun isPI(): Boolean = (
            _entityType.equals(EbaEntityType.PSD_PISP)
            )

    @Ignore
    fun isAI(): Boolean = (
            _entityType.equals(EbaEntityType.PSD_AISP)
            )

    @Ignore
    fun isPIAI(): Boolean = (
            isPI() || isAI()
            )

    @Ignore
    fun isEPI(): Boolean = (
            _entityType.equals(EbaEntityType.PSD_EPI)
            )

    @Ignore
    fun isCI(): Boolean = !isAllPSD2()


    fun isEMI(): Boolean = (
           _entityType.equals(EbaEntityType.PSD_EMI)
    )

    fun isE_EMI(): Boolean = (
        _entityType.equals(EbaEntityType.PSD_EEMI)
    )

    fun isNonPSD2Sp(): Boolean = (
           _entityType.equals(EbaEntityType.PSD_EXC)
    )

    @Ignore
    fun getStatus() = _status

    fun getTitleForList(): String {
        return getEntityName()
    }

    fun isBranch(): Boolean = (
        _entityType.equals(EbaEntityType.PSD_BR)
    )

    fun isAgent(): Boolean = (
        _entityType.equals(EbaEntityType.PSD_AG)
    )
}
