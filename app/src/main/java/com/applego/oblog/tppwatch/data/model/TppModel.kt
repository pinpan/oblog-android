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

    fun isASPSP(): Boolean

    fun isPsd2Tpp(): Boolean

    fun isUsed(): Boolean

    fun isFollowed(): Boolean

    fun getCountry(): String

    fun getStatus(): RecordStatus

    fun getEbaPassport() : EbaPassport

    fun getApps() : List<App>

    fun getTitleForList(): String

    fun isRevoked(): Boolean

    // TODO#: Consider Following fields
    //  tppRoles, - CZ has, Eba hasn't
}
