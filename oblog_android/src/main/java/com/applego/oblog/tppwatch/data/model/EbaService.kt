package com.applego.oblog.tppwatch.data.model

import java.util.*

/**
 * Services (as specified in Annex I to PSD2).
 */

enum class EbaService (val code: String, val psd2Code: String, val shortDescription: String, val description: String, val order: Int) {

    NONE("NONE", "NOPE",  "Payment service not defined", "", 0),
    ALL("ALL", "ALL",  "All services", "An aggregation item for all services: PS_010, PS_020, PS_03A ...", 1),
    ALL_PSD2("ALL_PSD2", "PSD2",  "All PSD2 services", "An aggregation item for all PSD2 services: AIS, PIS, PII", 2),
    AIS_PIS("AIS_PIS", "AIS-PIS",  "AIS and PIS only", "An aggregation item for Account information and Payment initiation services: AIS, PIS", 2),
    PS_010("PS_010", "EMS_PS_010",  "Payment account cash deposit", "1. Services enabling cash to be placed on a payment account as well as all the operations required for operating a payment account.", 3),
    PS_020("PS_020", "EMS_PS_020", "Payment account withdrawals", "2. Services enabling cash withdrawals from a payment account as well as all the operations required for operating a payment account.", 4),
    PS_03A("PS_03A", "EMS_PS_03A", "Direct debits execution", "3a. Execution of direct debits, including one-off direct debits.",5),
    PS_03B("PS_03B", "EMS_PS_03B", "Card payment transactions execution", "3b. Execution of payment transactions through a payment card or a similar device.", 6),
    PS_03C("PS_03C", "EMS_PS_03C", "Credit transfers execution incl. standing orders", "3c. Execution credit transfers, including standing orders.", 7),
    PS_04A("PS_04A", "EMS_PS_04A", "Credit line covered direct debits execution", "4a. Execution of direct debits where funds are covered by a credit line for a payment service user, including one-off direct debits.", 8),
    PS_04B("PS_04B", "EMS_PS_04B", "Credit line covered card based payment transactions", "4b. Execution of payment transactions through a payment card or similar device where funds are covered by a credit line for a payment service user.", 9),
    PS_04C("PS_04C", "EMS_PS_04C", "Credit line covered credit transfer", "4c. Execution of credit transfers where the funds are covered by a credit line for a payment service user, including standing orders.", 10),
    PS_05A("PS_05A", "PIIS", "Payment instruments issuing services (PIIS)", "5. Issuing of payment instruments.", 11),
    PS_05B("PS_05B", "EMS_PS_05B", "Payment transaction acquiring", "5. Acquiring of payment transactions.", 12),
    PS_060("PS_060", "EMS_PS_060", "Money remittance", "6. Money remittance.", 13),
    PS_070("PS_070", "PIS", "Payment initiation services (PIS)", "7. Payment initiation services.", 14),
    PS_080("PS_080", "AIS", "Account information services (AIS)", "8. Account information services.", 15),
    ES_010("ES_010", "EMS_ES_010", "Electronic money issuing, distribution and redemption.", "Issuing, distribution and redemption of electronic money.", 16);

    companion object {
        val allEbaServiesMap = HashMap<String, EbaService>()
        val allEbaServies = arrayListOf<EbaService>()

        val aisAndPisServiceMap = HashMap<String, EbaService>()
        val allPsd2ServiceMap = HashMap<String, EbaService>()
        val allPsd2Services = arrayListOf<EbaService>()

        val psd2ServiesWithAllOption = arrayListOf<EbaService>()
        val psd2ServiesWithAll_AllOptions = arrayListOf<EbaService>()

        init {
            allEbaServies.add(PS_010)
            allEbaServies.add(PS_020)
            allEbaServies.add(PS_03A)
            allEbaServies.add(PS_03B)
            allEbaServies.add(PS_03C)
            allEbaServies.add(PS_04A)
            allEbaServies.add(PS_04B)
            allEbaServies.add(PS_04C)
            allEbaServies.add(PS_05A)
            allEbaServies.add(PS_05B)
            allEbaServies.add(PS_05B)
            allEbaServies.add(PS_060)
            allEbaServies.add(PS_070)
            allEbaServies.add(PS_080)
            allEbaServies.add(ES_010)

            allPsd2Services.add(PS_080)
            allPsd2Services.add(PS_070)
            allPsd2Services.forEach {
                aisAndPisServiceMap.put(it.name, it)
            }
            allPsd2Services.add(PS_05A)
            allPsd2Services.forEach {
                allPsd2ServiceMap.put(it.name, it)
            }

            psd2ServiesWithAllOption.add(ALL_PSD2)
            psd2ServiesWithAllOption.add(AIS_PIS)
            psd2ServiesWithAllOption.addAll(allPsd2Services)

            psd2ServiesWithAll_AllOptions.add(ALL)
            psd2ServiesWithAll_AllOptions.addAll(psd2ServiesWithAllOption)

            allEbaServies.forEach {
                allEbaServiesMap.put(it.name, it)
            }
        }

        fun findService(code: String) : EbaService {
            var aService = allEbaServiesMap.get(code)
            if (aService == null) {
                aService = NONE
            }
            return aService
        }

        fun findServiceByCode(inService: String): EbaService {
            for (serv in allPsd2Services) {
                if (!serv.psd2Code.isNullOrBlank()) {
                    if (serv.psd2Code.toUpperCase().startsWith(inService.toUpperCase())) {
                        return serv
                    }
                }
            }
            return NONE
        }

        fun findServiceByShortDescription(inService: String): EbaService {
            for (serv in allPsd2Services) {
                if (!serv.shortDescription.isNullOrBlank()) {
                    if (serv.shortDescription.toUpperCase().startsWith(inService.toUpperCase())) {
                        return serv
                    }
                }
            }
            return NONE
        }
    }
}
