package com.applego.oblog.tppwatch.data.source.local

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 */
class Tpp : TppModel {

    constructor(entity : TppEntity) {
        tppEntity = entity
    }

    var tppEntity : TppEntity

    override fun getId() = tppEntity.getId()

    override fun getEntityId() = tppEntity.getEntityId()

    override fun getEntityCode() = tppEntity.getEntityCode()

    override fun getGlobalUrn() = tppEntity.getGlobalUrn()

    override fun getEntityName() = tppEntity.getEntityName()

    override fun getEbaEntityVersion() = tppEntity.getEbaEntityVersion()

    override fun getDescription() = tppEntity.getDescription()

    override fun getCountry() = tppEntity.getCountry()

    override fun getEbaPassport() = tppEntity.getEbaPassport()

    override fun getStatus() = tppEntity.getStatus()

    override fun isActive() = tppEntity.isActive()

    override fun isFis(): Boolean = tppEntity.isFis()

    override fun isPsd2(): Boolean = tppEntity.isPsd2()

    override fun isFollowed(): Boolean = tppEntity.isFollowed()

    fun setFollowed(f: Boolean) {
        tppEntity.followed = f
    }

    fun setActive(a: Boolean) {
        tppEntity.active = a
    }

    override fun getTitleForList(): String = tppEntity.getTitleForList()

    fun equals(other: Tpp) : Boolean {
        return (other != null) && other.getId().equals(getId());
    }

    // TODO#2: Consider Following fields
    //  details aka properties from EBA
    //  apps,
    //  tppRoles, - CZ has, Eba hasn't
}
