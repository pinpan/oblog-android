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
        @ColumnInfo(name = "entityType") val entityType: EbaEntityType, // TODO: Move up after entityName Fix tests!
        @PrimaryKey @ColumnInfo(name = "db_id") var _db_id: String = UUID.randomUUID().toString()
) {

    @Ignore
    fun getEntityId() = _entityId

    @Ignore
    fun getEntityCode() = _entityCode

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

    @ColumnInfo(name = "psd2")
    var psd2: Boolean = (entityType.equals(EbaEntityType.PSD_PI)
                      || entityType.equals(EbaEntityType.PSD_EMI)
                      || entityType.equals(EbaEntityType.PSD_AISP)
                      || entityType.equals(EbaEntityType.PSD_AG)
                      || entityType.equals(EbaEntityType.PSD_BR)
                      || entityType.equals(EbaEntityType.PSD_EPI)
                      || entityType.equals(EbaEntityType.PSD_EEMI)
            )

    @ColumnInfo(name = "fis")
    var fis: Boolean = !psd2

    @ColumnInfo(name = "followed")
    var followed: Boolean = false

    @ColumnInfo(name = "used")
    var used: Boolean = false

    @ColumnInfo(name = "revoked")
    var revoked: Boolean = false

    @ColumnInfo(name = "status")
    var _status: RecordStatus = RecordStatus.NEW

    var _ebaPassport : EbaPassport = EbaPassport()

    @Ignore
    fun getEbaPassport() = _ebaPassport

    @Ignore
    fun isFollowed() : Boolean = followed

    @Ignore
    fun isUsed() : Boolean = used

    @Ignore
    fun isRevoked(): Boolean = !ebaProperties.authorizationEnd.isNullOrBlank()
                            && !"N/A".contentEquals(ebaProperties.authorizationEnd)

    @Ignore
    fun isFis(): Boolean = !isPsd2()//fis

    @Ignore
    fun isPsd2(): Boolean = (entityType.equals(EbaEntityType.PSD_PI)
            || entityType.equals(EbaEntityType.PSD_EMI)
            || entityType.equals(EbaEntityType.PSD_AISP)
            || entityType.equals(EbaEntityType.PSD_BR)
            || entityType.equals(EbaEntityType.PSD_AG)
            || entityType.equals(EbaEntityType.PSD_EPI)
            || entityType.equals(EbaEntityType.PSD_EEMI)
            )//psd2

    @Ignore
    fun getStatus() = _status

    fun getTitleForList(): String {
        return getEntityName()
    }

/*
    @ColumnInfo(name="ENT_NAM")
    var ebaEntityName: String = "Payver Limited"

    @ColumnInfo(name="ENT_COD_TYP")
    var ebaEntityCodeType:String = "NON_LEI"

    @ColumnInfo(name="ENT_COU_RES")
    var ebaEntityCountryOfRES: String = "GB"

    @ColumnInfo(name="ENT_NAT_REF_COD")
    var ebaEntityNationalReferenceCode: String = "901016"

    @ColumnInfo(name="ENT_NAM_COM")
    var ebaEntityNameCOM:String = "Payver Limited"

    @ColumnInfo(name="ENT_ADD")
    var ebaEntityAddress = ""
    //[
    //    "Enfield",
    //    "134 Percival Road",
    //    "Middlesex"
    // ]

    @ColumnInfo(name="ENT_TOW_CIT_RES")
    var ebaEntityTownCityRES: String = "London"

    @ColumnInfo(name="ENT_POS_COD")
    var ebaEntitiyPostalCode: String = "EN1 1QU"

    @ColumnInfo(name="EBA_ENT_AUTH")
    var dateAuth: Date = Date()

*/

    //  tppRoles, - CZ has, Eba hasn't

/* Sample record
    "ebaProperties":{
        "ENT_NAM":"Payver Limited",
        "ENT_COD_TYP":"NON_LEI",
        "ENT_COU_RES":"GB",
        "ENT_NAT_REF_COD":"901016",
        "ENT_NAM_COM":"Payver Limited",
        "ENT_ADD":[
        "Enfield",
        "134 Percival Road",
        "Middlesex"
        ],
        "ENT_TOW_CIT_RES":"London",
        "ENT_POS_COD":"EN1 1QU",
        "ENT_AUT":[
        "2020-02-20"
        ]
    }
*/

}
