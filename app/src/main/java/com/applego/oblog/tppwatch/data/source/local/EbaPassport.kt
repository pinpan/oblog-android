package com.applego.oblog.tppwatch.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "ebaPassport")
@TypeConverters(OblogTypeConverters::class)
data class EbaPassport @JvmOverloads constructor(
        @ColumnInfo(name = "countryCode") var countryCode: String = "",
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

    /**
     * Map of services and corresponding list of countries to which the service is passported
     */
    @JvmField
    var serviceMap = HashMap<EbaService, List<String>>()

    /**
     * Map of counties and list of services which are passported to the corresponding country
     */
    @JvmField
    var countryMap = HashMap<String, List<Service>>()
}
