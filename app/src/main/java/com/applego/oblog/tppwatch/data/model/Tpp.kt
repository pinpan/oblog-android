package com.applego.oblog.tppwatch.data.model

import androidx.room.Ignore

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 */
class Tpp : TppModel {

    constructor(ebaEntity: EbaEntity, ncaEntity: NcaEntity) {
        this.ebaEntity = ebaEntity
        this.ncaEntity = ncaEntity
    }
/*
    constructor(ebaEntity: EbaEntity, ncaEntity: NcaEntity, appsPortfolio: AppsPortfolio ) {
        this.ebaEntity = ebaEntity
        this.ncaEntity = ncaEntity
        this.appsPortfolio = appsPortfolio
    }*/

    var ebaEntity : EbaEntity

    var ncaEntity : NcaEntity

    /**
     * One and only apps portfolio instance
     */
    var appsPortfolio = AppsPortfolio()
    override fun getApps() = appsPortfolio.appsList


    override fun getId() = ebaEntity.getId()

    override fun getEntityId() = ebaEntity.getEntityId()

    override fun getEntityCode() = ebaEntity.getEntityCode()

    override fun getGlobalUrn() = ebaEntity.getGlobalUrn()

    override fun getEntityName() = ebaEntity.getEntityName()

    override fun getEbaEntityVersion() = ebaEntity.getEbaEntityVersion()

    override fun getDescription() = ebaEntity.getDescription()

    override fun getCountry() = ebaEntity.getCountry()

    override fun getEbaPassport() = ebaEntity.getEbaPassport()

    override fun getStatus() = ebaEntity.getStatus()

    override fun isUsed() = ebaEntity.isUsed()

    override fun isFis(): Boolean = ebaEntity.isFis()

    override fun isPsd2(): Boolean = ebaEntity.isPsd2()

    override fun isFollowed(): Boolean = ebaEntity.isFollowed()

    fun setFollowed(f: Boolean) {
        ebaEntity.followed = f
    }

    fun setUsed(a: Boolean) {
        ebaEntity.used = a
    }

    override fun getTitleForList(): String = ebaEntity.getTitleForList()

    fun equals(other: Tpp) : Boolean {
        return (other != null) && other.getId().equals(getId());
    }

    // TODO#2: Consider Following fields
    //  details aka properties from EBA
    //  apps,
    //  tppRoles, - CZ has, Eba hasn't
}
