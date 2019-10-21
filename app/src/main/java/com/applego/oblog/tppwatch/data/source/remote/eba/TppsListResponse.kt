package com.applego.oblog.tppwatch.data.source.remote.eba

import com.applego.oblog.tppwatch.data.Paging
import com.applego.oblog.tppwatch.data.source.local.Tpp

class TppsListResponse {

    var tppsList: List<Tpp>?
    lateinit var paging: Paging

    constructor() {
        tppsList = ArrayList<Tpp>()
    }

    constructor (items: List<Tpp>) {
        this.tppsList = items
    }

    constructor (items: List<Tpp>, paging: Paging) : this(items) {
        this.paging = paging
    }
}
