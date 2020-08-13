package com.applego.oblog.tppwatch.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.applego.oblog.tppwatch.about.AboutViewModel
import com.applego.oblog.tppwatch.addedittppapp.AddEditTppAppViewModel
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

    private val models = HashMap<String, ViewModel>()

    fun <T : ViewModel> get(modelClass: Class<T>) =
        with(modelClass) {
            var model = models.get(modelClass.name)
            if (model == null) {
                model = create(modelClass)
            }
            model
        }

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            var model : ViewModel
            when {
                isAssignableFrom(StatisticsViewModel::class.java) -> {
                    model = StatisticsViewModel(tppsRepository)
                    models.put(StatisticsViewModel::class.java.name, model)
                    model
                }
                isAssignableFrom(TppDetailViewModel::class.java) -> {
                    model = TppDetailViewModel(tppsRepository)
                    models.put(TppDetailViewModel::class.java.name, model)
                    model
                }
                isAssignableFrom(AppsViewModel::class.java) -> {
                    model = AppsViewModel(tppsRepository)
                    models.put(AppsViewModel::class.java.name, model)
                    model
                }
                isAssignableFrom(AddEditTppViewModel::class.java) -> {
                    model = AddEditTppViewModel(tppsRepository)
                    models.put(AddEditTppViewModel::class.java.name, model)
                    model
                }
               isAssignableFrom(AddEditTppAppViewModel::class.java) -> {
                   model = AddEditTppAppViewModel(tppsRepository)
                   models.put(AddEditTppAppViewModel::class.java.name, model)
                   model
               }
               isAssignableFrom(TppsViewModel::class.java) -> {
                   model = TppsViewModel(tppsRepository)
                   models.put(TppsViewModel::class.java.name, model)
                   model
               }
               isAssignableFrom(AboutViewModel::class.java) -> {
                   model = AboutViewModel()
                   models.put(AboutViewModel::class.java.name, model)
                   model
               }
               else -> {
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        } as T

    companion object {
        val viewModelFactory = ViewModelFactory(ServiceLocator.tppsRepository!!)
    }
}
