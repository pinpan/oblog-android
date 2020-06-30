package com.applego.oblog.tppwatch.data.model

import java.util.*

/**
 * EBA EntityType (as specified in PSD2 and EMD2).
 *
 * ‘Payment institutions' as legally defined in Article 4(4) of PSD2;
 * ‘Exempted payment institutions' under Article 32 of PSD2;
 * ‘Account information service providers' under Article 33 of PSD2;
 * ‘Electronic money institutions' as legally defined in Article 2(1) of EMD2;
 * ‘Exempted electronic money institutions' under Article 9 of EMD2;
 * ‘Agents' as legally defined in Article 4(38) of PSD2;
 * ‘EEA branches' as legally defined in Article 4(39) of PSD2;
 * ‘Institutions entitled under national law to provide payment services' under Article 2(5) of PSD2;
 * ‘Service providers excluded from the scope of PSD2' under points (i) and (ii) of point (k) and point (l) of Article 3 of PSD2.
 */

enum class EbaEntityType (val code: String, val description: String) {

    NONE("NONE", "No entity type specified initially."),

    PSD_PI("PSD_PI", "Payment Institution."),
    PSD_EPI("PSD_EPI", "Exempted Payment Institution."),
    PSD_EMI("PSD_EMI", "Electronic Money Institution."),
    PSD_EEMI("PSD_EEMI", "Exempted Electronic Money Institution."),
    PSD_AISP("PSD_AISP", "Account Information Services Provider."),
    PSD_EXC("PSD_EXC", "Service provider excluded from the scope of PSD2."),
    PSD_ENL("PSD_ENL", "Institution referred to in points (4) to (23) of Article 2(5) of Directive 2013/36/EU that is entitled under national law to provide payment services"),
    PSD_BR("PSD_BR", "Branch of a payment institution, electronic money institution or account information service provider."),
    PSD_AG("PSD_AG", "‘Agents' as legally defined in Article 4(38) of PSD2."),

    NON_PSD_SP("NON_PSD_SP", "Service providers excluded from the scope of PSD2 under points (i) and (ii) of point (k) and point (l) of Article 3 of PSD2."),
    CREDIT_INSTITUTION("CI", "No entity type specified initially.");

    companion object {
        val allEntityTypes = HashMap<String, EbaEntityType>()
        init {
            allEntityTypes.put(PSD_PI.code, PSD_PI)
            allEntityTypes.put(PSD_EPI.code, PSD_EPI)
            allEntityTypes.put(PSD_EMI.code, PSD_EMI)
            allEntityTypes.put(PSD_EEMI.code, PSD_EEMI)
            allEntityTypes.put(PSD_AISP.code, PSD_AISP)

            allEntityTypes.put(PSD_EXC.code, PSD_EXC)
            allEntityTypes.put(PSD_ENL.code, PSD_ENL)
            allEntityTypes.put(PSD_BR.code, PSD_BR)
            allEntityTypes.put(PSD_AG.code, PSD_AG)
        }
    }
}
