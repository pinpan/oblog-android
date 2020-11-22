package com.applego.oblog.tppwatch.data.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters

@Entity(tableName = "entity_nca_properties")
@TypeConverters(OblogTypeConverters::class)
open class NcaCZEntityProperties : NcaEntityProperties (EUCountry.CZ.isoCode, EUCountry.CZ.nca) {

}
