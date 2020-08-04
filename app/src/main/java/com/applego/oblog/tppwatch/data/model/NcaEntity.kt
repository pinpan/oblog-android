package com.applego.oblog.tppwatch.data.model

import androidx.room.*
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
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
@Entity(tableName = "nca_entity")
@TypeConverters(OblogTypeConverters::class)
data class NcaEntity @JvmOverloads constructor(
        @ColumnInfo(name = "entityId") val _entityId: String = "",
        @ColumnInfo(name = "entityCode") val _entityCode: String = "",
        @ColumnInfo(name = "entityName") var _entityName: String = "",
        @ColumnInfo(name = "description") var _description: String = "",
        @ColumnInfo(name = "globalUrn") var _globalUrn: String = "",
        @ColumnInfo(name = "ebaEntityVersion") var _ebaEntityVersion: String = "",
        @ColumnInfo(name = "country") var _country: String = "",

        @PrimaryKey @ColumnInfo(name = "id") var _id: String = UUID.randomUUID().toString()
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
    fun getId() = _id

    fun getTitleForList(): String {
        return getEntityName()
    }


    // TODO#: Consider Following fields
    //  details aka properties from EBA
    //  tppRoles, - CZ has, Eba hasn't
    //  apps,
}
