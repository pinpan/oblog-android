package com.applego.oblog.tppwatch.data

class Paging {
    //{"sort":{"sorted":false,"unsorted":true,"empty":true},"pageSize":25,"pageNumber":1,"offset":25,"paged":true,"unpaged":false}

    var sorted: Boolean = false

    var size: Int = 10
    var page: Int = 0
    var offset: Int = 10
}
