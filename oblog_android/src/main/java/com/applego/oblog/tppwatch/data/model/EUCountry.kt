package com.applego.oblog.tppwatch.data.model

/**
 * Used with the filter countriesSpinner in the tpps list.
 */
enum class EUCountry(val countryName: String, val isoCode:String, val order: Int, val aliases: String = "", val nca: String = "") {


    /**
     * Used to indicate any EU country
     */
    EU("All EU", "EU",0),

    /**
     * Austria
     */
    AT("Austria", "AT", 1),

    /**
     * Belgium
     */
    BE("Belgium", "BE",2),

    /**
     * Bulgaria
     */
    BG("Bulgaria", "BG", 3),

    /**
     * Republic of Cyprus
     */
    CY("Republic of Cyprus", "CY",4),

    /**
     * Czech Republic
     */
    CZ("Czech Republic", "CZ", 5),

    /**
     * Germany
     */
    DE("Germany", "DE", 6),

    /**
     * Denmark
     */
    DK("Denmark", "DK", 7),

    /**
     * Estonia
     */
    EE("Estonia", "EE", 8),

    /**
     * Finland
     */
    FI("Finland", "FI", 9),

    /**
     * France
     */
    FR("France", "FR", 10),

    /**
     * Greece
     */
    GR("Greece", "GR", 11),

    /**
     * Croatia
     */
    HR("Croatia", "HR", 12),

    /**
     * Hungary
     */
    HU("Hungary", "HU", 13),

    /**
     * Ireland
     */
    IE("Ireland", "IE", 14),

    /**
     * ISLAND
     */
    IS("ISLAND", "IS",15),

    /**
     * Italy
     */
    IT("Italy", "IT", 16),

    /**
     * Liechtenstein
     */
    LI("Liechtenstein", "LI",17),

    /**
     * Latvia
     */
    LT("Lithuania", "LT", 18),

    /**
     * Luxembourg
     */
    LU("Luxembourg", "LU", 19),

    /**
     * Lithuania - Financial and Capital Market Commission
     */
    LV("Latvia", "LV", 20),

    /**
     * Malta
     */
    MT("Malta", "MT", 21),

    /**
     * Netherlands
     */
    NL("Netherlands", "NL", 22),

    /**
     * Norway
     */
    NO("Norway", "NO", 23),

    /**
     * Poland
     */
    PL("Poland", "PL", 24),

    /**
     * Portugal
     */
    PT("Portugal", "PT", 25),

    /**
     * Romania
     */
    RO("Romania", "RO", 26),

    /**
     * Sweden
     */
    SE("Sweden", "SE",27),

    /**
     * Slovenia
     */
    SI("Slovenia", "SI", 28),

    /**
     * Slovakia
     */
    SK("Slovakia", "SK", 29),

    /**
     * Spain
     */
    ES("Spain", "ES", 30),

    /**
     * Great Britain
     */
    GB("United Kingdom", "UK",31),

    /**
     * Used to indicate that no EU country was found
     */
    NEU("Not an EU country", "NEU", 100);

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
