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
package com.applego.oblog.tppwatch.tpps

/**
 * Used with the filter countriesSpinner in the tpps list.
 */
enum class TppsFilterType {

    /**
     * Use all Tpps.
     */
    ALL_TPPs,

    /**
     * Select followed tpps (used tpps are implicitly followed).
     */
    FOLLOWED_TPPs,

    /**
     * Select used tpps.
     */
    USED_TPPs,

    /**
     * Select PSD2 licensed TPPs - Payment Institutions (including Exempted PIS) and
     *  Account Information Services Providers.
     */
    PSD2_TPPs,

    /**
     * Select E-Money institutions including Exempted E-Money institutions.
     */
    E_MONEY_INSTs,

    /**
     * Select TPPs,Exempted from PSD2 scope.
     */
    NON_PSD2_TPPs,

    /**
     * Select Credit institutions - Banks aka ASPSPs.
     */
    CREDIT_INSTs,

    /**
     * Select revoked only Tpps.
     */
    REVOKED_TPPs,

    /**
     * select only revoked TPPs
     */
    REVOKED_ONLY_TPPs;


    companion object {
        val allFilterTypes = mutableListOf<TppsFilterType>()

        init {
            allFilterTypes.add(TppsFilterType.PSD2_TPPs)
            allFilterTypes.add(TppsFilterType.E_MONEY_INSTs)
            allFilterTypes.add(TppsFilterType.NON_PSD2_TPPs)
            allFilterTypes.add(TppsFilterType.CREDIT_INSTs)
            allFilterTypes.add(TppsFilterType.USED_TPPs)
            allFilterTypes.add(TppsFilterType.FOLLOWED_TPPs)
        }
    }
}
