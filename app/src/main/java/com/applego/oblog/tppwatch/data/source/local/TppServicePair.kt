package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Embedded

class TppServicePair {

    @Embedded(prefix = "tpp_")
    lateinit var tpp : Tpp

    @Embedded
    var service : Service? = null
}