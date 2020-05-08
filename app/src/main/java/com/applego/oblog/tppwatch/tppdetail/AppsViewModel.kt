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


    fun addApp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun editApp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun start(tppId : String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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



    private val _editAppEvent = MutableLiveData<Event<Unit>>()
    val editAppEvent: LiveData<Event<Unit>> = _editAppEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _items = MutableLiveData<List<App>>().apply { value = emptyList() }
    val items: MutableLiveData<List<App>> = _items
}
