package com.applego.oblog.tppwatch.data.model

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

    constructor(ebaEntity: EbaEntity) {
        this.ebaEntity = ebaEntity
    }

    var ebaEntity : EbaEntity

    var ncaEntity : NcaEntity? = null

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

    override fun isRevoked() = ebaEntity.isRevoked()

    override fun isPSD2(): Boolean = ebaEntity.isAllPSD2()
    override fun isPI(): Boolean = ebaEntity.isPI()
    override fun isAI(): Boolean = ebaEntity.isAI()
    override fun isPIAI(): Boolean = ebaEntity.isPIAI()
    override fun isEPI(): Boolean = ebaEntity.isEPI()
    override fun isEMI(): Boolean = ebaEntity.isEMI()
    override fun isE_EMI(): Boolean = ebaEntity.isE_EMI()
    override fun isNonPsd2Sp(): Boolean = ebaEntity.isNonPSD2Sp()

    override fun isCI(): Boolean = ebaEntity.isCI()
    override fun isASPSP(): Boolean = ebaEntity.isCI()

    override fun isFollowed(): Boolean = ebaEntity.isFollowed()

    fun setFollowed(f: Boolean) {
        ebaEntity.followed = f
    }

    fun setUsed(a: Boolean) {
        ebaEntity.used = a
    }

    override fun getTitleForList(): String = ebaEntity.getTitleForList()

    fun equals(other: Tpp) : Boolean {
        return other.getId().equals(getId());
    }

    // TODO#:
    //   Consider exposing following fields
    //     - details aka properties from EbaEntityProperties
    //     - tppRoles, - CZ has, Eba hasn't
}
