package com.applego.oblog.tppwatch.data.source.local

import java.util.*

/**
 * Services (as specified in Annex I to PSD2).
 */

enum class EbaService (val code: String, val psd2Code: String, val shortDescription: String, val description: String) {

    /**/NONE("NONE", "",  "No payment servie available.", ""),
    /**/PS_010("PS_010", "",  "Payment account cash deposit.", "1. Services enabling cash to be placed on a payment account as well as all the operations required for operating a payment account."),
    PS_020("PS_020", "", "Payment account withdrawals.", "2. Services enabling cash withdrawals from a payment account as well as all the operations required for operating a payment account."),
    PS_03A("PS_03A", "", "Direct debits execution.", "3a. Execution of direct debits, including one-off direct debits."),
    /**/PS_03B("PS_03B", "", "Card payment transactions execution.", "3b. Execution of payment transactions through a payment card or a similar device."),
    /**/PS_03C("PS_03C", "", "Credit transfers execution incl. standing orders.", "3c. Execution credit transfers, including standing orders."),
    PS_04A("PS_04A", "", "Credit line covered direct debits execution.", "4a. Execution of direct debits where funds are covered by a credit line for a payment service user, including one-off direct debits."),
    PS_04B("PS_04B", "", "Credit line covered card based payment transactions", "4b. Execution of payment transactions through a payment card or similar device where funds are covered by a credit line for a payment service user."),
    PS_04C("PS_04C", "", "Credit line covered credit transfer.", "4c. Execution of credit transfers where the funds are covered by a credit line for a payment service user, including standing orders."),
    /**/PS_05A("PS_05A", "PIIS", "Payment instruments issuing.", "5. Issuing of payment instruments."),
    /**/PS_05B("PS_05B", "", "Payment transaction acquiring.", "5. Acquiring of payment transactions."),
    /**/PS_060("PS_060", "", "Money remittance.", "6. Money remittance."),
    /**/PS_070("PS_070", "PIS", "Payment initiation services", "7. Payment initiation services."),
    /**/PS_080("PS_080", "AIS", "Account information services", "8. Account information services."),
    /**/ES_010("ES_010", "", "Electronic money issuing, distribution and redemption.", "Issuing, distribution and redemption of electronic money.");



    companion object {
        val allServies = HashMap<String, EbaService>()
        init {
            allServies.put("PS_010", PS_010)
            allServies.put("PS_020", PS_020)
            allServies.put("PS_03A", PS_03A)
            allServies.put("PS_03B", PS_03B)
            allServies.put("PS_03C", PS_03C)
            allServies.put("PS_04A", PS_04A)
            allServies.put("PS_04B", PS_04B)
            allServies.put("PS_04C", PS_04C)
            allServies.put("PS_05A", PS_05A)
            allServies.put("PS_05B", PS_05B)
            allServies.put("PS_05B", PS_05B)
            allServies.put("PS_060", PS_060)
            allServies.put("PS_070", PS_070)
            allServies.put("PS_080", PS_080)
            allServies.put("ES_010", ES_010)
        }

        fun findService(code: String) : EbaService {
            var aService = allServies.get(code)
            if (aService == null) {
                aService = NONE
            }
            return aService
        }

    }
}
