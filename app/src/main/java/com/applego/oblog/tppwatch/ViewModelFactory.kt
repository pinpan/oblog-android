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
package com.applego.oblog.tppwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.applego.oblog.tppwatch.about.AboutViewModel
import com.applego.oblog.tppwatch.addedittpp.AddEditTppViewModel
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.statistics.StatisticsViewModel
import com.applego.oblog.tppwatch.tppdetail.*
import com.applego.oblog.tppwatch.tpps.TppsViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val tppsRepository: TppsRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(StatisticsViewModel::class.java) ->
                    StatisticsViewModel(tppsRepository)
                isAssignableFrom(TppDetailViewModel::class.java) ->
                    TppDetailViewModel(tppsRepository)
                isAssignableFrom(TppDetailTabsViewModel::class.java) ->
                    TppDetailTabsViewModel(tppsRepository)
                isAssignableFrom(TppDetailEbaViewModel::class.java) ->
                    TppDetailEbaViewModel(tppsRepository)
                isAssignableFrom(TppDetailNcaViewModel::class.java) ->
                    TppDetailNcaViewModel(tppsRepository)
                isAssignableFrom(TppDetailAppsViewModel::class.java) ->
                    TppDetailAppsViewModel(tppsRepository)
                isAssignableFrom(AddEditTppViewModel::class.java) ->
                    AddEditTppViewModel(tppsRepository)
                isAssignableFrom(TppsViewModel::class.java) ->
                    TppsViewModel(tppsRepository)
                isAssignableFrom(AboutViewModel::class.java) ->
                    AboutViewModel()
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
