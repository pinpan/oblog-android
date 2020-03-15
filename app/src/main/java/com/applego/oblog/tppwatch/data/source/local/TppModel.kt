package com.applego.oblog.tppwatch.data.source.local


interface TppModel  {

    //var isFis: Boolean

    //var isPsd2: Boolean

    //var isActive: Boolean

    //var isFollowed: Boolean

    // var status: RecordStatus


    fun getEntityCode(): String

    fun getTitle(): String

    fun getDescription(): String

    fun getGlobalUrn(): String

    fun getEbaEntityVersion(): String

    fun getId(): String

    fun isFis(): Boolean

    fun isPsd2(): Boolean

    fun isActive(): Boolean

    fun isFollowed(): Boolean

    fun getCountry(): String

    fun getStatus(): RecordStatus

    fun getEbaPassport() : EbaPassport


    fun getTitleForList(): String
    // TODO#: Consider Following fields
    //  details aka properties from EBA
    //  tppRoles, - CZ has, Eba hasn't
    //  apps,
}
