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
     * No criteria selected
     */
    //NONE,

    /**
     * Use all Tpps.
     */
    ALL_TPPs,

    /**
     * Filter followed tpps (used tpps are implicitly followed).
     */
    FOLLOWED_TPPs,

    /**
     * Filter used tpps.
     */
    USED_TPPs,

    /**
     * Filter tpps, which have only PSD2 licence but are not FIS.
     */
    //PSD2_TPPS,

    /**
     * Filter tpps, which have PSD2 licence, including FIS.
     */
    ONLY_PSD2_TPPs,

    /**
     * Filter FIS.
     */
    ONLY_PSD2_FIs,

    /**
     * Filter revoked only Tpps.
     */
    REVOKED_TPPs;


    companion object {
        val allFilterTypes = mutableListOf<TppsFilterType>()

        init {
            allFilterTypes.add(TppsFilterType.ONLY_PSD2_FIs)
            //allFilterTypes.add(TppsFilterType.PSD2_TPPS)
            allFilterTypes.add(TppsFilterType.USED_TPPs)
            allFilterTypes.add(TppsFilterType.FOLLOWED_TPPs)
            allFilterTypes.add(TppsFilterType.ONLY_PSD2_TPPs)
        }
    }
}
