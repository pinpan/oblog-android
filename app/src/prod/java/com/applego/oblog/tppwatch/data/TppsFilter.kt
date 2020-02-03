package com.applego.oblog.tppwatch.data

import com.applego.oblog.tppwatch.data.source.local.EbaService

class TppsFilter {
    var  tppName: String = ""
    var  services: List<String>? = null


    var  country: String = ""
    var  pasportedTo: List<String>? = null
    var  orderBy: String = ""

    public fun withName(aName: String) : TppsFilter {
        tppName = aName
        return this;
    }

    public fun withServices(services: List<String>) : TppsFilter {
        this.services = services
        return this;
    }

    public fun withCountry(aCountry: String) : TppsFilter {
        this.country = aCountry
        return this;
    }

    public fun withPasportedTo(pasportedTo: List<String>) : TppsFilter {
        this.pasportedTo = pasportedTo
        return this;
    }

    public fun withOrderBy(orderBy: String) : TppsFilter {
        this.orderBy = orderBy
        return this;
    }
}
