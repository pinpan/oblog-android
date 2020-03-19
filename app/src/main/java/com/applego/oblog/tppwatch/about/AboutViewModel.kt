package com.applego.oblog.tppwatch.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.source.local.Tpp

/**
 * ViewModel for the Add/Edit screen.
 */
class AboutViewModel(
    //private val tppsRepository: TppsRepository
) : ViewModel() {

    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _tppUpdatedEvent = MutableLiveData<Event<Unit>>()
    val tppUpdatedEvent: LiveData<Event<Unit>> = _tppUpdatedEvent

    private var tppId: String? = null

    private var isNewTpp: Boolean = false

    private var isDataLoaded = false

    private var tppFollowed = false

    fun start() {
        if (_dataLoading.value == true) {
            return
        }

        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }
    }

    private fun onTppLoaded(tpp: Tpp) {
        title.value = tpp.getEntityName()
        description.value = tpp.getDescription()
        tppFollowed = tpp.isFollowed()
        _dataLoading.value = false
        isDataLoaded = true
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    // Called when clicking on fab.
    fun saveTpp() {
        val currentTitle = title.value
        val currentDescription = description.value

        if (currentTitle == null || currentDescription == null) {
            _snackbarText.value = Event(R.string.empty_tpp_message)
            return
        }
    }

}
