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
    var serviceMap = HashMap<String, MutableList<EUCountry>>()

    val services : List<ServiceVisa>
        get() {
            val myPasportedServices = ArrayList<ServiceVisa>()
            serviceMap.forEach({
                myPasportedServices.add(ServiceVisa(it.key, EbaService.findService(it.key).shortDescription, it.value))
            })
            return myPasportedServices
        }

    class ServiceVisa {

        var service = String()
        var serviceDetail = String()
        var countries : List<EUCountry> = ArrayList<EUCountry>()

        constructor(service: String, detail: String, countries: List<EUCountry>) {
            this.service = service
            this.serviceDetail = detail
            this.countries = countries
        }

        val serviceNameAndCode : String
            get() {
                return StringBuilder().append("(").append(service).append(") ").append(serviceDetail).toString()
            }

        val countriesAsString : String
            get() {
                val sb = StringBuilder()
                for (c in countries) {
                    sb.append(c.name).append(" ,")
                }

                val str = sb.removeSuffix(" ,")
                return str.toString()
            }

        // #TODO-PZA: Implement hash and equals
    }
}
