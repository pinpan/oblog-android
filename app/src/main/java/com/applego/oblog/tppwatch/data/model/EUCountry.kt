package com.applego.oblog.tppwatch.data.model

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
     * Spain
     */
    ES("Spain", 8),

    /**
     * Finland
     */
    FI("Finland", 9),

    /**
     * France
     */
    FR("France", 10),

    /**
     * Great Britain
     */
    GB("Great Britain", 11),

    /**
     * Greece
     */
    GR("Greece", 12),

    /**
     * Croatia
     */
    HR("Croatia", 13),

    /**
     * Hungary
     */
    HU("Hungary", 14),

    /**
     * Ireland
     */
    IE("Ireland", 15),

    /**
     * ISLAND
     */
    IS("ISLAND", 16),

    /**
     * Italy
     */
    IT("Italy", 17),

    /**
     * Lithuania
     */
    LI("Liechtenstein", 18),

    /**
     * Latvia
     */
    LT("Lithuania", 19),

    /**
     * Luxembourg
     */
    LU("Luxembourg", 20),

    /**
     * Lithuania - Financial and Capital Market Commission
     */
    LV("Latvia", 21),

    /**
     * Malta
     */
    MT("Malta", 22),

    /**
     * Netherlands
     */
    NL("Netherlands", 22),

    /**
     * Norway
     */
    NO("Norway", 23),

    /**
     * Poland
     */
    PL("Poland", 24),

    /**
     * Portugal
     */
    PT("Portugal", 25),

    /**
     * Romania
     */
    RO("Romania", 26),

    /**
     * Sweden
     */
    SE("Sweden", 27),

    /**
     * Slovenia
     */
    SI("Slovenia", 28),

    /**
     * Slovakia
     */
    SK("Slovakia", 29),

    /**
     * Usd to indicate that no EU country was found
     */
    NEU("NotInEU", 30);

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
            allEUCountries.add(ES)
            allEUCountries.add(FI)
            allEUCountries.add(FR)
            allEUCountries.add(GB)
            allEUCountries.add(GR)
            allEUCountries.add(HR)
            allEUCountries.add(HU)
            allEUCountries.add(IE)
            allEUCountries.add(IS)
            allEUCountries.add(IT)
            allEUCountries.add(LI)
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

            allEUCountries.forEach {
                allEUCountriesMap.put(it.name, it)
            }
        }
    }
}
