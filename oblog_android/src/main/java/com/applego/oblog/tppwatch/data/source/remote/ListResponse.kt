package com.applego.oblog.tppwatch.data.source.remote

open class ListResponse<T> {

    var aList = emptyList<T>()
    var paging = Paging()

    constructor() {
        aList = ArrayList<T>()
    }

    constructor (items: List<T>) {
        this.aList = items
    }

    constructor (items: List<T>, paging: Paging) : this(items) {
        this.paging = paging
    }
}
