package com.applego.oblog.tppwatch.data.model

import java.util.*

/**
 * Services (as specified in Annex I to PSD2).
 */

enum class EbaService (val code: String, val psd2Code: String, val shortDescription: String, val description: String, val order: Int) {

    /**/NONE("NONE", "",  "No payment servie available.", "", 0),
    /**/PS_010("PS_010", "",  "Payment account cash deposit.", "1. Services enabling cash to be placed on a payment account as well as all the operations required for operating a payment account.", 1),
    PS_020("PS_020", "", "Payment account withdrawals.", "2. Services enabling cash withdrawals from a payment account as well as all the operations required for operating a payment account.", 2),
    PS_03A("PS_03A", "", "Direct debits execution.", "3a. Execution of direct debits, including one-off direct debits.",3),
    /**/PS_03B("PS_03B", "", "Card payment transactions execution.", "3b. Execution of payment transactions through a payment card or a similar device.", 4),
    /**/PS_03C("PS_03C", "", "Credit transfers execution incl. standing orders.", "3c. Execution credit transfers, including standing orders.", 5),
    PS_04A("PS_04A", "", "Credit line covered direct debits execution.", "4a. Execution of direct debits where funds are covered by a credit line for a payment service user, including one-off direct debits.", 6),
    PS_04B("PS_04B", "", "Credit line covered card based payment transactions", "4b. Execution of payment transactions through a payment card or similar device where funds are covered by a credit line for a payment service user.", 7),
    PS_04C("PS_04C", "", "Credit line covered credit transfer.", "4c. Execution of credit transfers where the funds are covered by a credit line for a payment service user, including standing orders.", 8),
    /**/PS_05A("PS_05A", "PIIS", "Payment instruments issuing.", "5. Issuing of payment instruments.", 9),
    /**/PS_05B("PS_05B", "", "Payment transaction acquiring.", "5. Acquiring of payment transactions.", 10),
    /**/PS_060("PS_060", "", "Money remittance.", "6. Money remittance.", 11),
    /**/PS_070("PS_070", "PIS", "Payment initiation services", "7. Payment initiation services.", 12),
    /**/PS_080("PS_080", "AIS", "Account information services", "8. Account information services.", 13),
    /**/ES_010("ES_010", "", "Electronic money issuing, distribution and redemption.", "Issuing, distribution and redemption of electronic money.", 14);



    companion object {
        val allEbaServiesMap = HashMap<String, EbaService>()
        val allEbaServies = arrayListOf<EbaService>()
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

        fun findPsd2Service(inService: String): EbaService {
            if (inService != null) {
                val psd2Service = inService.toUpperCase()
                for (serv in allEbaServiesMap) {
                    if (!serv.value.psd2Code.isNullOrBlank()) {
                        if (psd2Service.startsWith(serv.value.psd2Code.toUpperCase())) {
                            return serv.value
                        }
                    }
                }
            }
            return NONE
        }

    }
}
