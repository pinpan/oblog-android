package com.applego.oblog.tppwatch.data.model

/**
 * Used in main TppsFragment filter
 */
enum class InstType {

    /**
     * No criteria selected
     */
    NONE,

    /**
     * No criteria selected
     */
    ALL,

    /**
     * Account Information institutions
     */
    INST_AI,


    /**
     * Payment Initiation institutions
     */
    INST_PI,

    /**
     * AI + PI institutions
     */
     INST_PIAI,

    /**
     * Exempted PI institutions
     */
     INST_EPI,

    /**
     * Electronic Money Institutions
     */
    INST_EMI,

    /**
     * Exempted EMI institutions
     */
    INST_EEMI,

    /**
     * Institutions exempted from PSD2 scope.
     */
    NON_PSD2_INST,

    /**
     * CIs.
     */
    CIs;

    companion object {
        val allPspTypes = mutableListOf<InstType>()

        init {
            allPspTypes.add(INST_PI)
            allPspTypes.add(INST_AI)
            allPspTypes.add(INST_PIAI)
            allPspTypes.add(INST_EPI)
            allPspTypes.add(INST_EMI)
            allPspTypes.add(INST_EEMI)
            allPspTypes.add(NON_PSD2_INST)
            allPspTypes.add(CIs)
        }
    }
}
