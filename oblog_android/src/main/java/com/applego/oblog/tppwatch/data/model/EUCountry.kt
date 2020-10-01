package com.applego.oblog.tppwatch.data.model

/**
 * Used with the filter countriesSpinner in the tpps list.
 */
enum class EUCountry(val countryName: String, val isoCode:String, val order: Int, val aliases: String = "", val nca: String = "") {


    /**
     * Used to indicate any EU country
     */
    EU("All EU countries", "EU",0),

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
//    IS("ISLAND", 16),

    /**
     * Italy
     */
    IT("Italy", "IT", 15),

    /**
     * Lithuania
     */
    //LI("Liechtenstein", 18),

    /**
     * Latvia
     */
    LT("Lithuania", "LT", 16),

    /**
     * Luxembourg
     */
    LU("Luxembourg", "LU", 17),

    /**
     * Lithuania - Financial and Capital Market Commission
     */
    LV("Latvia", "LV", 18),

    /**
     * Malta
     */
    MT("Malta", "MT", 19),

    /**
     * Netherlands
     */
    NL("Netherlands", "NL", 20),

    /**
     * Norway
     */
    NO("Norway", "NO", 21),

    /**
     * Poland
     */
    PL("Poland", "PL", 22),

    /**
     * Portugal
     */
    PT("Portugal", "PT", 23),

    /**
     * Romania
     */
    RO("Romania", "RO", 24),

    /**
     * Sweden
     */
    SE("Sweden", "SE",25),

    /**
     * Slovenia
     */
    SI("Slovenia", "SI", 26),

    /**
     * Slovakia
     */
    SK("Slovakia", "SK", 27),

    /**
     * Spain
     */
    ES("Spain", "ES", 28),

    /**
     * Great Britain
     */
    GB("United Kingdom", "UK",29),

    /**
     * Used to indicate that no EU country was found
     */
    NEU("Not an EU country", "NEU", 100);

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
            allEUCountries.add(GB)

            allEUCountries.forEach {
                allEUCountriesMap.put(it.name, it)
            }
        }
    }
}
