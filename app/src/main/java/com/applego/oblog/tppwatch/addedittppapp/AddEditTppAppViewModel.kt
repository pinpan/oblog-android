package com.applego.oblog.tppwatch.addedittppapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add/Edit screen.
 */
class AddEditTppAppViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    // Two-way databinding, exposing MutableLiveData
    val appName = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    var webAddr = MutableLiveData<String>()

    var isDataLoaded = false
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _appUpdatedEvent = MutableLiveData<Event<Unit>>()
    val appUpdatedEvent: LiveData<Event<Unit>> = _appUpdatedEvent

    var tppId: String = ""
        set(anId) {field = anId}

    private var tpp: Tpp? = null

    private var isNewApp: Boolean = true


    fun start(tppId: String) {
        if (_dataLoading.value == true) {
            return
        }

        this.tppId = tppId

        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }

        _dataLoading.value = true

        viewModelScope.launch {
            tppsRepository.getTpp(tppId).let { result ->
                _dataLoading.value = false
                if (result is Success) {
                    tpp = result.data
                    onTppLoaded(result.data)
                }
            }
        }
    }

    private fun onTppLoaded(aTpp: Tpp) {
        tpp =  aTpp
        appName.value = tpp?.getEntityName() ?: "N/A"
        description.value = tpp?.getDescription() ?: "N/A"
        isDataLoaded = true
    }

    /*
    private fun onDataNotAvailable() {
    }

    // Called when clicking on fab.
    fun saveTpp() {
        val currentTitle = entityName.value
        val currentDescription = description.value

        if (currentTitle == null || currentDescription == null) {
            _snackbarText.value = Event(R.string.empty_tpp_message)
            return
        }

        val currentTppId = tppId
    }


*/
    fun cancelAddApp() {
        _appUpdatedEvent.value = Event(Unit)
    }

    fun createApp() {
        //tppsRepository.saveApp(newApp)
        val currentAppName = appName.value
        val currentDescription = description.value
        val currentWebAddr = webAddr.value

        if (currentAppName == null || currentDescription == null) {
            _snackbarText.value = Event(R.string.empty_app_message)
            return
        }

        if (currentAppName.isNullOrBlank() || currentDescription.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_app_message)
            return
        }

        var newApp = App(currentAppName, currentDescription, currentWebAddr)
        //newApp.tppId = tppId
        saveApp(newApp)
    }


    private fun saveApp(app: App) = viewModelScope.launch {

        if (app != null) {
            tppsRepository.saveApp(tpp!!, app)
        }
        _appUpdatedEvent.value = Event(Unit)
    }

    private fun updateTpp(tpp: Tpp) {
        viewModelScope.launch {
            tppsRepository.saveTpp(tpp)
            _appUpdatedEvent.value = Event(Unit)
        }
    }
}
