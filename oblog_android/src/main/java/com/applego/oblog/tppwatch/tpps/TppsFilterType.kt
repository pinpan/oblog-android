package com.applego.oblog.tppwatch.tpps

/**
 * Used with the filter countriesSpinner in the tpps list.
 */
enum class TppsFilterType {

    /**
     * All Institutions
     */
    ALL_INST,

    /**
     * Account Information Institutions
     */
    AI_INST,

    /**
     * Payment Institutions
     */
    PI_INST,

    /**
     * Exempted Payment Institutions
     */
    E_PI_INST,

    /**
     * ALL Pure PSD2 Institutions - Payment Institutions and Account Information Institutions
     */
    PIAI_INST,

    /**
     * E-Money institutions.
     */
    EMONEY_INST,

    /**
     * Exempted E-Money institutions.
     */
    E_EMONEY_INST,

    /**
     * Select TPPs,Exempted from PSD2 scope.
     */
    NON_PSD2_INST,

    /**
     * Select Credit institutions - Banks aka ASPSPs.
     */
    CREDIT_INST,

    /**
     * Select revoked only Tpps.
     */
    REVOKED,

    /**
     * Select only revoked TPPs
     */
    REVOKED_ONLY,

    /**
     *  Denotes TPP's branches
     */
    BRANCHES,

    /**
     * Denotes Tpp's agents
     */
    AGENTS,

    /**
     * Denotes Tpp's agents
     */
    NATIONAL_LOW_INST,

    /**
     * Select followed tpps (used tpps are implicitly followed).
     */
    FOLLOWED,

    /**
     * Select used tpps.
     */
    USED;

    companion object {
        val allFilterTypes = mutableListOf<TppsFilterType>()

        init {
            allFilterTypes.add(TppsFilterType.AI_INST)
            allFilterTypes.add(TppsFilterType.PI_INST)
            allFilterTypes.add(TppsFilterType.PIAI_INST)
            allFilterTypes.add(TppsFilterType.E_PI_INST)
            allFilterTypes.add(TppsFilterType.EMONEY_INST)
            allFilterTypes.add(TppsFilterType.E_EMONEY_INST)
            allFilterTypes.add(TppsFilterType.NON_PSD2_INST)
            allFilterTypes.add(TppsFilterType.NATIONAL_LOW_INST)

            allFilterTypes.add(TppsFilterType.CREDIT_INST)
            allFilterTypes.add(TppsFilterType.USED)
            allFilterTypes.add(TppsFilterType.FOLLOWED)
        }
    }
}
