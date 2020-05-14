package com.applego.oblog.tppwatch.tppdetail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * ViewModel for the Details screen.
 */
open class TppDetailViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    private val _tpp = MutableLiveData<Tpp>()
    val tpp: LiveData<Tpp> = _tpp

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editTppEvent = MutableLiveData<Event<Unit>>()
    val editTppEvent: LiveData<Event<Unit>> = _editTppEvent

    private val _tppUpdatedEvent = MutableLiveData<Event<Unit>>()
    val tppUpdatedEvent: LiveData<Event<Unit>> = _tppUpdatedEvent

    private val _addTppAppEvent = MutableLiveData<Event<Unit>>()
    val addTppAppEvent: LiveData<Event<Unit>> = _addTppAppEvent


    private val _editTppAppEvent = MutableLiveData<Event<String>>()
    val editTppAppEvent: LiveData<Event<String>> = _editTppAppEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val tppId: String?
        get() = _tpp.value?.getId()

    public val description: String?
        get() {
            return tpp.value?.getDescription() ?: ""
        }

    // This LiveData depends on another so we can use a transformation.
    val followed: LiveData<Boolean> = Transformations.map(_tpp) { input: Tpp? ->
        input?.isFollowed() ?: false
    }

    // This LiveData depends on another so we can use a transformation.
    val used: LiveData<Boolean> = Transformations.map(_tpp) { input: Tpp? ->
        input?.isUsed() ?: false
    }

    fun editTpp() {
        _editTppEvent.value = Event(Unit)
    }

    fun setFollowed(follow: Boolean) = viewModelScope.launch {
        _tpp.value?.setFollowed(follow)
        val tpp = _tpp.value ?: return@launch
        tppsRepository.setTppFollowedFlag(tpp, follow)

        showSnackbarMessage(if (follow) R.string.tpp_marked_followed else R.string.tpp_marked_followed)
    }

    fun setUsed(activate: Boolean) = viewModelScope.launch {
        val tpp = _tpp.value ?: return@launch
        tppsRepository.setTppActivateFlag(tpp, activate)

        showSnackbarMessage(if (activate) R.string.tpp_marked_used else R.string.tpp_marked_inused)
    }

    fun start(tppId: String?, forceRefresh: Boolean = false) {
        if (_isDataAvailable.value == true && !forceRefresh || _dataLoading.value == true) {
            return
        }

        // Show loading indicator
        _dataLoading.value = true

        wrapEspressoIdlingResource {
            runBlocking {
                if (tppId != null) {
                    tppsRepository.getTpp(tppId, forceRefresh).let { result ->
                        if (result is Success) {
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

    fun refresh() {
        CoroutineScope(Dispatchers.Main).launch {
            tppId?.let {
                start(it, true)
            }
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}
