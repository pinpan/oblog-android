package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.model.EbaEntity

class EbaEntitiesListResponse {

    var entitiesList = emptyList<EbaEntity>()
    var paging = Paging()

    constructor() {
        entitiesList = ArrayList<EbaEntity>()
    }

    constructor (items: List<EbaEntity>) {
        this.entitiesList = items
    }

    constructor (items: List<EbaEntity>, paging: Paging) : this(items) {
        this.paging = paging
    }
}
