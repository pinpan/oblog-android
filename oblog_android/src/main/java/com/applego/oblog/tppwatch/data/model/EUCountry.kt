package com.applego.oblog.tppwatch.data.model

import com.applego.oblog.tppwatch.tpps.ResourcesUtils

/**
 * Used with the filter countriesSpinner in the tpps list.
 */
enum class EUCountry(val country: String, val order: Int, val aliases: String = "", val nca: String = "") {

    /**
     * Austria
     */
    AT("Austria", 0),

    /**
     * Belgium
     */
    BE("Belgium", 1),

    /**
     * Bulgaria
     */
    BG("Bulgaria", 2),

    /**
     * Republic of Cyprus
     */
    CY("Republic of Cyprus", 3),

    /**
     * Czech Republic
     */
    CZ("Czech Republic", 4),

    /**
     * Germany
     */
    DE("Germany", 5),

    /**
     * Denmark
     */
    DK("Denmark", 6),

    /**
     * Estonia
     */
    EE("Estonia", 7),

    /**
     * Finland
     */
    FI("Finland", 8),

    /**
     * France
     */
    FR("France", 9),

    /**
     * Greece
     */
    GR("Greece", 10),

    /**
     * Croatia
     */
    HR("Croatia", 11),

    /**
     * Hungary
     */
    HU("Hungary", 12),

    /**
     * Ireland
     */
    IE("Ireland", 13),

    /**
     * ISLAND
     */
//    IS("ISLAND", 16),

    /**
     * Italy
     */
    IT("Italy", 14),

    /**
     * Lithuania
     */
    //LI("Liechtenstein", 18),

    /**
     * Latvia
     */
    LT("Lithuania", 15),

    /**
     * Luxembourg
     */
    LU("Luxembourg", 16),

    /**
     * Lithuania - Financial and Capital Market Commission
     */
    LV("Latvia", 17),

    /**
     * Malta
     */
    MT("Malta", 18),

    /**
     * Netherlands
     */
    NL("Netherlands", 19),

    /**
     * Norway
     */
    NO("Norway", 20),

    /**
     * Poland
     */
    PL("Poland", 21),

    /**
     * Portugal
     */
    PT("Portugal", 22),

    /**
     * Romania
     */
    RO("Romania", 23),

    /**
     * Sweden
     */
    SE("Sweden", 24),

    /**
     * Slovenia
     */
    SI("Slovenia", 25),

    /**
     * Slovakia
     */
    SK("Slovakia", 26),

    /**
     * Spain
     */
    ES("Spain", 27),

    /**
     * Great Britain
     */
    UK("United Kingdom", 28),

    /**
     * Used to indicate that no EU country was found
     */
    EU("All EU countries", 29),

    /**
     * Used to indicate that no EU country was found
     */
    NEU("Not an EU country", 30);

    companion object {

        val allEUCountriesMap = hashMapOf<String, EUCountry>()
        val allEUCountries = arrayListOf<EUCountry>()
        init {
            allEUCountries.add(AT)
            allEUCountries.add(BE)
            allEUCountries.add(BG)
            allEUCountries.add(CY)
            allEUCountries.add(CZ)
            allEUCountries.add(DE)
            allEUCountries.add(DK)
            allEUCountries.add(EE)
            allEUCountries.add(FI)
            allEUCountries.add(FR)
            allEUCountries.add(GR)
            allEUCountries.add(HR)
            allEUCountries.add(HU)
            allEUCountries.add(IE)
            allEUCountries.add(IT)
            allEUCountries.add(LT)
            allEUCountries.add(LU)
            allEUCountries.add(LV)
            allEUCountries.add(MT)
            allEUCountries.add(NL)
            allEUCountries.add(NO)
            allEUCountries.add(PL)
            allEUCountries.add(PT)
            allEUCountries.add(RO)
            allEUCountries.add(SE)
            allEUCountries.add(SI)
            allEUCountries.add(SK)
            allEUCountries.add(ES)
            allEUCountries.add(UK)

            allEUCountries.forEach {
                allEUCountriesMap.put(it.name, it)
            }
        }
    }
}
