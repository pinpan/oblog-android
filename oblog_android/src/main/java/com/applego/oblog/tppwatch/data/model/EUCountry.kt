package com.applego.oblog.tppwatch.data.model

/**
 * Used with the filter countriesSpinner in the tpps list.
 */
enum class EUCountry(val countryName: String, val isoCode: String, val nca: String = "", val aliases: String = "", val order: Int) {


    /**
     * Used to indicate any EU country
     */
    EU("All EU", "EU", nca = "EBA", order = 0),

    /**
     * Austria
     */
    AT("Austria", "AT", nca = "FMA", order = 1),

    /**
     * Belgium
     */
    BE("Belgium", "BE", nca = "", order = 2),

    /**
     * Bulgaria
     */
    BG("Bulgaria", "BG", nca = "BNB", order = 3),

    /**
     * Republic of Cyprus
     */
    CY("Republic of Cyprus", "CY", nca = "", order = 4),

    /**
     * Czech Republic
     */
    CZ("Czech Republic", "CZ", nca = "CNB", order = 5),

    /**
     * Germany
     */
    DE("Germany", "DE", nca = "BAFIN", order = 6),

    /**
     * Denmark
     */
    DK("Denmark", "DK", nca = "", order = 7),

    /**
     * Estonia
     */
    EE("Estonia", "EE", nca = "", order = 8),

    /**
     * Finland
     */
    FI("Finland", "FI", nca = "", order = 9),

    /**
     * France
     */
    FR("France", "FR", nca = "", order = 10),

    /**
     * Greece
     */
    GR("Greece", "GR", nca = "", order = 11),

    /**
     * Croatia
     */
    HR("Croatia", "HR", nca = "", order = 12),

    /**
     * Hungary
     */
    HU("Hungary", "HU", nca = "", order = 13),

    /**
     * Ireland
     */
    IE("Ireland", "IE", nca = "", order = 14),

    /**
     * ICELAND
     */
    IS("ICELAND", "IS", nca = "", order = 15),

    /**
     * Italy
     */
    IT("Italy", "IT", nca = "", order = 16),

    /**
     * Liechtenstein
     */
    LI("Liechtenstein", "LI", nca = "", order = 17),

    /**
     * Latvia
     */
    LT("Lithuania", "LT", nca = "", order = 18),

    /**
     * Luxembourg
     */
    LU("Luxembourg", "LU", nca = "", order = 19),

    /**
     * Lithuania - Financial and Capital Market Commission
     */
    LV("Latvia", "LV", nca = "", order = 20),

    /**
     * Malta
     */
    MT("Malta", "MT", nca = "", order = 21),

    /**
     * Netherlands
     */
    NL("Netherlands", "NL", nca = "", order = 22),

    /**
     * Norway
     */
    NO("Norway", "NO", nca = "", order = 23),

    /**
     * Poland
     */
    PL("Poland", "PL", nca = "", order = 24),

    /**
     * Portugal
     */
    PT("Portugal", "PT", nca = "", order = 25),

    /**
     * Romania
     */
    RO("Romania", "RO", nca = "", order = 26),

    /**
     * Sweden
     */
    SE("Sweden", "SE", nca = "", order = 27),

    /**
     * Slovenia
     */
    SI("Slovenia", "SI", nca = "", order = 28),

    /**
     * Slovakia
     */
    SK("Slovakia", "SK", nca = "", order = 29),

    /**
     * Spain
     */
    ES("Spain", "ES", nca = "", order = 30),

    /**
     * Great Britain
     */
    GB("United Kingdom", "GB", nca = "", order = 31),

    /**
     * Used to indicate that no EU country was found
     */
    NEU("Not an EU country", "NEU", nca = "", order = 100);

    companion object {

        val allEUCountriesMap = hashMapOf<String, EUCountry>()
        val allEUCountries  = arrayListOf<EUCountry>()
        val allEUCountriesWithEU = arrayListOf<EUCountry>()
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
            allEUCountries.add(ES)
            allEUCountries.add(GB)

            allEUCountriesWithEU.add(EU)
            for(n in 0..allEUCountries.size-1) {
                allEUCountriesMap.put(allEUCountries[n].name, allEUCountries[n])
                allEUCountriesWithEU.add(allEUCountries[n])
            }
        }
    }
}
