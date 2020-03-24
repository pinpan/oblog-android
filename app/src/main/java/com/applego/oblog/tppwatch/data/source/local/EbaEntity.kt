
package com.applego.oblog.tppwatch.data.source.local

import androidx.room.*
import java.util.*

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param entityId    TPP legal entity ID provided by home country
 * @param entityCode  entityCode assigned by EBA. Inconsistently includes TPP home country, Tpp Home NCA ID and TPP's NCA given ID
 * @param entityName  entityName of the tpp
 * @param description description of the tpp
 * @param isFollowed  whether or not this tpp is followed
 * @param id          id of the tpp
 */
@Entity(tableName = "tpps")
@TypeConverters(OblogTypeConverters::class)
data class EbaEntity @JvmOverloads constructor(
        @ColumnInfo(name = "entityId") val _entityId: String = "",
        @ColumnInfo(name = "entityCode") val _entityCode: String = "",
        @ColumnInfo(name = "entityName") var _entityName: String = "",
        @ColumnInfo(name = "description") var _description: String = "",
        @ColumnInfo(name = "globalUrn") var _globalUrn: String = "",
        @ColumnInfo(name = "ebaEntityVersion") var _ebaEntityVersion: String = "",
        @ColumnInfo(name = "country") var _country: String = "",

        @PrimaryKey @ColumnInfo(name = "id") var _id: String = UUID.randomUUID().toString()
) : TppModel {

    @Ignore
    override fun getEntityId() = _entityId

    @Ignore
    override fun getEntityCode() = _entityCode

    @Ignore
    override fun getEntityName() = _entityName

    @Ignore
    override fun getDescription() = _description

    @Ignore
    override fun getGlobalUrn() = _globalUrn

    @Ignore
    override fun getEbaEntityVersion() = _ebaEntityVersion

    @Ignore
    override fun getCountry() = _country

    @Ignore
    override fun getId() = _id

    @ColumnInfo(name = "fis")
    var fis: Boolean = false

    @ColumnInfo(name = "psd2")
    var psd2: Boolean = false

    @ColumnInfo(name = "followed")
    var followed: Boolean = false

    @ColumnInfo(name = "active")
    var active: Boolean = false

    @ColumnInfo(name = "status")
    var _status: RecordStatus = RecordStatus.NEW

    var _ebaPassport : EbaPassport = EbaPassport()
    @Ignore
    override fun getEbaPassport() = _ebaPassport

    @Ignore
    override fun isFollowed() : Boolean = followed

    @Ignore
    override fun isActive() : Boolean = active

    @Ignore
    override fun isFis(): Boolean = fis

    @Ignore
    override fun isPsd2(): Boolean = psd2

    @Ignore
    override fun getStatus() = _status

    override fun getTitleForList(): String {
        return (getEntityName()) ?: getDescription()
    }


    // TODO#: Consider Following fields
    //  details aka properties from EBA
    //  tppRoles, - CZ has, Eba hasn't
    //  apps,
}
