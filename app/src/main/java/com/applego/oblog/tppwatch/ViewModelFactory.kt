package com.applego.oblog.tppwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.applego.oblog.tppwatch.about.AboutViewModel
import com.applego.oblog.tppwatch.addedittpp.AddEditTppViewModel
import com.applego.oblog.tppwatch.data.repository.TppsRepository
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
/*
                isAssignableFrom(TppDetailTabsViewModel::class.java) ->
                    TppDetailTabsViewModel(tppsRepository)
*/
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
