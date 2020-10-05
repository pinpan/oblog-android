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

enum class EbaEntityType(val code: String, val description: String, val order: Int) {
    ALL("ALL", "ALL entity types", 0),

    PSD_PI("PSD_PI", "Payment Institution.", 1),
    PSD_AISP("PSD_AISP", "Account Information Services Provider.", 2),
    PSD_EPI("PSD_EPI", "Exempted Payment Institution.", 3),
    PSD_EMI("PSD_EMI", "Electronic Money Institution.", 4),
    PSD_EEMI("PSD_EEMI", "Exempted Electronic Money Institution.", 5),
    PSD_EXC("PSD_EXC", "Service provider excluded from the scope of PSD2.", 6),
    PSD_ENL("PSD_ENL", "Institution referred to in points (4) to (23) of Article 2(5) of Directive 2013/36/EU that is entitled under national law to provide payment services", 7),

    PSD_BR("PSD_BR", "Branch of a payment institution, electronic money institution or account information service provider.", 8),
    PSD_AG("PSD_AG", "‘Agents' as legally defined in Article 4(38) of PSD2.", 9),
    CREDIT_INSTITUTION("CI", "No entity type specified initially.", 10),

    NONE("NONE", "No entity type specified initially.", 100);

    companion object {
        val allEntityTypesMap = HashMap<String, EbaEntityType>()
        val allEntityTypes = arrayListOf<EbaEntityType>()
        init {
            allEntityTypes.add(PSD_PI)
            allEntityTypes.add(PSD_EPI)
            allEntityTypes.add(PSD_EMI)
            allEntityTypes.add(PSD_EEMI)
            allEntityTypes.add(PSD_AISP)
            allEntityTypes.add(PSD_EXC)
            allEntityTypes.add(PSD_BR)
            allEntityTypes.add(PSD_AG)
            allEntityTypes.add(PSD_ENL)
            allEntityTypes.add(CREDIT_INSTITUTION)

            allEntityTypes.forEach {
                allEntityTypesMap.put(it.name, it)
            }
        }
    }
}
