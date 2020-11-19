package com.applego.oblog.tppwatch.data.model

import androidx.room.*
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import java.util.*

@Entity(tableName = "entityAddress")
@TypeConverters(OblogTypeConverters::class)
data class Address
        @JvmOverloads constructor(
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
    ) {

    @ColumnInfo
    var street = "" //"ulice": "Vyskočilova",

    @ColumnInfo
    var descriptionalNumber = -1 //"cisloPopisne": 1442,

    @ColumnInfo
    var orientationalNumber = -1 //"cisloOrientacni": 1,

    @ColumnInfo
    var discriminatorLetter = "" // "cisloOrientacniPismeno": "b",

    @ColumnInfo
    var quarter = "" //""castObce": "Michle",

    @ColumnInfo
    var municipality = "" //""obec": "Praha",

    @ColumnInfo
    var zipCode = "" //""psc": "14000",

    // Country ISO code
    @ColumnInfo
    var country =  "" //""stat": "CZ"

    @ColumnInfo
    var ruianCode = "" //""ruian": 25559869,

    @ColumnInfo
    var unstructuredAddress = "" //""nestrukturovanaAdresa": "Vyskočilova 1442/1b, Michle, 140 00 Praha 4",

}