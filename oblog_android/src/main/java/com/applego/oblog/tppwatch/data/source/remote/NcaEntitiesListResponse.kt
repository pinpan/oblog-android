package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.model.NcaEntity

class NcaEntitiesListResponse {

    var entitiesList = emptyList<NcaEntity>()
    var paging = Paging()

    constructor() {
        entitiesList = ArrayList<NcaEntity>()
    }

    constructor (items: List<NcaEntity>) {
        this.entitiesList = items
    }

    constructor (items: List<NcaEntity>, paging: Paging) : this(items) {
        this.paging = paging
    }
}
