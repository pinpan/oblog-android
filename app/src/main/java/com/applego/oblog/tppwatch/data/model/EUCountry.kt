/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applego.oblog.tppwatch.data.model

/**
 * Used with the filter countriesSpinner in the tpps list.
 */
enum class EUCountry(val country: String, val aliases: String = "", val nca: String = "") {

    /**
     * Austria
     */
    AT("Austria"),

    /**
     * Belgium
     */
    BE("Belgium"),

    /**
     * Bulgaria
     */
    BG("Bulgaria"),

    /**
     * Republic of Cyprus
     */
    CY("Republic of Cyprus"),

    /**
     * Czech Republic
     */
    CZ("Czech Republic"),

    /**
     * Germany
     */
    DE("Germany"),

    /**
     * Denmark
     */
    DK("Denmark"),

    /**
     * Estonia
     */
    EE("Estonia"),

    /**
     * Spain
     */
    ES("Spain"),

    /**
     * Finland
     */
    FI("Finland"),

    /**
     * France
     */
    FR("France"),

    /**
     * Great Britain
     */
    GB("Great Britain"),

    /**
     * Greece
     */
    GR("Greece"),

    /**
     * Croatia
     */
    HR("Croatia"),

    /**
     * Hungary
     */
    HU("Hungary"),

    /**
     * Ireland
     */
    IE("Ireland"),

    /**
     * ISLAND
     */
    IS("ISLAND"),

    /**
     * Italy
     */
    IT("Italy"),

    /**
     * Lithuania
     */
    LI("Liechtenstein"),

    /**
     * Latvia
     */
    LT("Lithuania"),

    /**
     * Luxembourg
     */
    LU("Luxembourg"),

    /**
     * Lithuania - Financial and Capital Market Commission
     */
    LV("Latvia"),

    /**
     * Malta
     */
    MT("Malta"),

    /**
     * Netherlands
     */
    NL("Netherlands"),

    /**
     * Norway
     */
    NO("Norway"),

    /**
     * Poland
     */
    PL("Poland"),

    /**
     * Portugal
     */
    PT("Portugal"),

    /**
     * Romania
     */
    RO("Romania"),

    /**
     * Sweden
     */
    SE("Sweden"),

    /**
     * Slovenia
     */
    SI("Slovenia"),

    /**
     * Slovakia
     */
    SK("Slovakia"),

    NEU("NotInEU");

    companion object {

        val allEUCountriesMap = hashMapOf<String, EUCountry>()
        val allEUCountries = arrayListOf<EUCountry>()
        init {
            allEUCountries.add(AT)
            allEUCountries.add(BE)
            allEUCountries.add(BG)
            allEUCountries.add(CZ)
            allEUCountries.add(CY)
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
