package com.applego.oblog.tppwatch.data.model

import com.applego.oblog.tppwatch.data.source.local.RecordStatus


interface TppModel  {

    fun getId(): String

    fun getEntityId(): String

    fun getEntityCode(): String

    fun getEntityName(): String

    fun getGlobalUrn(): String

    fun getEbaEntityVersion(): String

    fun getDescription(): String

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
