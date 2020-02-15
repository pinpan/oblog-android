package com.applego.oblog.tppwatch.data.source.local

import androidx.room.*
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
    //@JvmField
    //var serviceMap = HashMap<EbaService, MutableList<String>>()

    /**
     * Map of counties and list of services which are passported to the corresponding country
     */
    //@JvmField
    @Embedded
    var countryMap = HashMap<String, MutableList<Service>>()

    val serviceCountries: String
        get() = if (countryCode.isNotEmpty()) countryCode else id


    class CountryVisa {


        var country = String()
        var services : List<Service> = ArrayList<Service>()

        constructor(country: String, services: List<Service>) {
            this.country = country
            this.services = services
        }

        override fun equals(other: Any?): Boolean {
            return     (other == this)
                    || ((other as CountryVisa).hashCode() == hashCode())
        }

        val servicesAsString : String
            get() {
                val sb = StringBuilder()
                services.forEach { sb.append(it).append(" ,")}
                sb.removeSuffix(" ,")
                return sb.toString()
            }


    }

    val services : List<CountryVisa>
        get() {
            val myPasportedServices = ArrayList<CountryVisa>()
            countryMap.entries.forEach({myPasportedServices.add(CountryVisa(it.key, it.value))})
            return myPasportedServices
        }
}
