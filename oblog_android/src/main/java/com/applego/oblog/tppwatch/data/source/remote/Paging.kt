package com.applego.oblog.tppwatch.data.source.remote

class Paging (
    //{"sort":{"sorted":false,"unsorted":true,"empty":true},"pageSize":25,"pageNumber":1,"offset":25,"paged":true,"unpaged":false}
    var size: Int = 10,
    var page: Int = 0,
    var offset: Int = 0,
    var sorted: Boolean = true,
    var sortBy: String = "name",
    var first: Boolean = true,
    var last: Boolean = false,
    var totalPages: Int = -1,
    var totalElements: Int = -1
    ){



}
