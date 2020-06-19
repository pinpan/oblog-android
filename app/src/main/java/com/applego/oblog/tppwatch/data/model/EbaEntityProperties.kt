package com.applego.oblog.tppwatch.data.model

import androidx.room.*
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import java.util.*

/**
 * Immutable model class for Eba Tpp properties.
 *
 */
//@Entity(tableName = "tpps")
//@TypeConverters(OblogTypeConverters::class)
data class EbaEntityProperties @JvmOverloads constructor(
        /*
        {ENT_COD_TYP: "NON_LEI"}
        {ENT_NAT_REF_COD: "03570967"}
        {ENT_NAM: "Benxy s.r.o. "}
        {ENT_ADD: "Evropská 2690/17"}
        {ENT_TOW_CIT_RES: "Praha"}
        {ENT_POS_COD: "16000"}
        {ENT_COU_RES: "CZ"}
        {ENT_AUT: ["2015-03-27", "2019-04-12"]}
        */
      @ColumnInfo(name = "codeType") val codeType: String = ""
     ,@ColumnInfo(name = "nationalReferenceCode") val nationalReferenceCode: String = ""
     ,@ColumnInfo(name = "entityNames") val entityNames: String = ""
     ,@ColumnInfo(name = "address") val address: String = ""
     ,@ColumnInfo(name = "cityOfResidence") val cityOfResidence: String = ""
     ,@ColumnInfo(name = "postalCode") val postalCode: String = ""
     ,@ColumnInfo(name = "entCouRes") val countryOfResidence: String = ""
     ,@ColumnInfo(name = "authorizationStart") val authorizationStart: String = ""
     ,@ColumnInfo(name = "autorizationEnd") val authorizationEnd: String = ""

    //, @PrimaryKey @ColumnInfo(name = "properties_db_id") var _db_id: String = UUID.randomUUID().toString()
) {

}
