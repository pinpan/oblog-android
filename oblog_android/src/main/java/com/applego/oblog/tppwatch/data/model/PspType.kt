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
enum class PspType {

    /**
     * No criteria selected
     */
    NONE,

    /**
     * Use all Tpps.
     */
    ALL_PSD2,

    /**
     * Filter licence PSD2 TPPs.
     */
    PSD2_TPPs,

    /**
     * Filter Electroic Money Institutions including exempted EMIs.
     */
    EMIs,


    /**
     * Filter TPPs exempted from PSD2 scope.
     */
    NON_PSD2_TPPs,

    /**
     * Filter CIs.
     */
    CIs;

    companion object {
        //var allFilterTypes: MutableList<TppsFilterType> = ArrayList<TppsFilterType>()
        val allPspTypes = mutableListOf<PspType>()

        init {
            allPspTypes.add(ALL_PSD2)
            allPspTypes.add(PSD2_TPPs)
            allPspTypes.add(CIs)
        }
    }
}
