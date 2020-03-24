package com.applego.oblog.tppwatch.data.source.local

import androidx.room.*
import java.util.*

/**
 * Immutable model class for Eba Tpp properties.
 *
 */
@Entity(tableName = "tpps")
@TypeConverters(OblogTypeConverters::class)
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
      @ColumnInfo(name = "ENT_COD_TYP") val entCodTyp: String = ""
     ,@ColumnInfo(name = "ENT_NAT_REF_COD") val entNatRefCod: String = ""
     ,@ColumnInfo(name = "ENT_NAM") val entNam: String = ""
     ,@ColumnInfo(name = "ENT_ADD") val entAdd: String = ""
     ,@ColumnInfo(name = "ENT_TOW_CIT_RES") val entTowCitRes: String = ""
     ,@ColumnInfo(name = "ENT_POS_COD") val entPosCod: String = ""
     ,@ColumnInfo(name = "ENT_COU_RES") val entCowRes: String = ""
     ,@ColumnInfo(name = "ENT_AUT") val entAut: String = ""

    , @PrimaryKey @ColumnInfo(name = "id") var _id: String = UUID.randomUUID().toString()
) {

}
