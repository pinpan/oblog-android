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

    fun isEMI(): Boolean
    fun isPI(): Boolean
    fun isAI(): Boolean
    fun isPIAI(): Boolean
    fun isEPI(): Boolean
    fun isE_EMI(): Boolean

    fun isASPSP(): Boolean

    fun isPSD2(): Boolean

    fun isCI(): Boolean

    fun isNonPsd2Sp(): Boolean

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
