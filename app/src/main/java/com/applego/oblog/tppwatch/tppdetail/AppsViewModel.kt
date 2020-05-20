package com.applego.oblog.tppwatch.tppdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.Event
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.runBlocking

class AppsViewModel (private val tppsRepository: TppsRepository) : ViewModel() {

    private val _tpp = MutableLiveData<Tpp>()
    val tpp: LiveData<Tpp> = _tpp

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    //private val _items = MutableLiveData<List<App>>().apply { value = emptyList() }
    private val _openAppEvent = MutableLiveData<Event<String>>()
    val openAppEvent: LiveData<Event<String>> = _openAppEvent

    fun start(tppId : String?) {
        _dataLoading.value = true

        wrapEspressoIdlingResource {
            runBlocking {
                if (tppId != null) {
                    tppsRepository.getTpp(tppId, false).let { result ->
                        if (result is Result.Success) {
                            onTppLoaded(result.data)
                        } else {
                            onDataNotAvailable(result)
                        }
                    }
                }
                _dataLoading.value = false
            }
        }
    }

    private fun setTpp(tpp: Tpp?) {
        this._tpp.value = tpp
        _isDataAvailable.value = tpp != null
    }

    private fun onTppLoaded(tpp: Tpp) {
        setTpp(tpp)
    }

    private fun onDataNotAvailable(result: Result<Tpp>) {
        _tpp.value = null
        _isDataAvailable.value = false
    }

    fun openApp(appId: String) {
        _editAppEvent.value = Event(appId)
    }

    fun getApp(appId: String) : App? {
        return tpp.value?.appsPortfolio?.getApp(appId)
    }

    val items: List<App> // = _items
        get() = tpp.value?.appsPortfolio?.appsList ?: emptyList()


    private val _editAppEvent = MutableLiveData<Event<String>>()
    val editAppEvent: LiveData<Event<String>> = _editAppEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText
}
