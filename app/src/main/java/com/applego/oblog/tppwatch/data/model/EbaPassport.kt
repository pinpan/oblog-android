package com.applego.oblog.tppwatch.data.model

import androidx.room.*
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import java.util.*

@Entity(tableName = "ebaPassports")
@TypeConverters(OblogTypeConverters::class)
data class EbaPassport @JvmOverloads constructor(
        @ColumnInfo(name = "countryCode") var countryCode: String = "",
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

    /**
     * Map of counties and list of services which are passported to the corresponding country
     */
    @Embedded
    var countryMap = HashMap<String, MutableList<Psd2Service>>()

    @Embedded
    var serviceMaps : List<Map<String, List<Psd2Service>>> = ArrayList<Map<String, MutableList<Psd2Service>>>()

    val services : List<CountryVisa>
        get() {
            val myPasportedServices = ArrayList<CountryVisa>()
            countryMap.entries.forEach({myPasportedServices.add(CountryVisa(it.key, it.value))})
            return myPasportedServices
        }

    class CountryVisa {

        var country = String()
        var services : List<Psd2Service> = ArrayList<Psd2Service>()

        constructor(country: String, services: List<Psd2Service>) {
            this.country = country
            this.services = services
        }

        val servicesAsString : String
            get() {
                val sb = StringBuilder()
                services.forEach { sb.append(if (!it.psd2Code.isNullOrEmpty()) it.psd2Code else it.title).append(" ,")}
                val str = sb.removeSuffix(" ,")
                return str.toString()
            }

        // #TODO-PZA: Implement hash and equals
    }
}