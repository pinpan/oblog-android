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

enum class TppType (val code: Int, val description: String) {

    NONE(0, "No entity type specified initially."),

    PSD_PI(1, "Payment Institution."),
    PSD_EPI(2, "Exempted Payment Institution."),
    PSD_AISP(4, "Account Information Services Provider."),
    PSD_EMI(8, "Electronic Money Institution."),
    PSD_EEMI(16, "Exempted Electronic Money Institution."),

    PSD_AGENT(32, "‘Agents' as legally defined in Article 4(38) of PSD2."),
    PSD_BRANCH(64, "‘EEA branches' as legally defined in Article 4(39) of PSD2."),
    PSD_NLE_SP(128, "Institutions entitled under national law to provide payment services' under Article 2(5) of PSD2."),
    NON_PSD_SP(256, "Service providers excluded from the scope of PSD2 under points (i) and (ii) of point (k) and point (l) of Article 3 of PSD2."),
    PSD2(512, "ALL TPPs");

    companion object {
        val allEntityTypes = HashMap<Int, TppType>()
        init {
            allEntityTypes.put(PSD_PI.code, PSD_PI)
            allEntityTypes.put(PSD_AISP.code, PSD_AISP)
            allEntityTypes.put(PSD_EMI.code, PSD_EMI)
            allEntityTypes.put(PSD_EPI.code, PSD_EPI)
            allEntityTypes.put(PSD_EEMI.code, PSD_EEMI)
        }
    }
}
