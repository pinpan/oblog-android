package com.applego.oblog.tppwatch.addedittppapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.util.Event
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

    var appId: String? = ""
        set(anId) {field = anId}

    private val _app = MutableLiveData<App>()
    val app: MutableLiveData<App> = _app

    fun start(tppId: String, appId: String?) {
        if (_dataLoading.value == true) {
            return
        }

        this.tppId = tppId
        this.appId = appId

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
        aTpp.appsPortfolio.appsList?.forEach({
            if (appId == it.id ) {
                _app.value = it
            }
        })

        if (app != null) {
            appName.value = app?.value?.name ?: "app name"
            description.value = app?.value?.description ?: "description"
            webAddr.value = app?.value?.webAddr ?: "web address"
            isDataLoaded = true
        }
    }

    fun cancelAddApp() {
        _appUpdatedEvent.value = Event(Unit)
    }

    fun saveApp() {
        val currentAppName = appName.value
        val currentDescription = description.value
        val currentWebAddr = webAddr.value

        if ((currentAppName == null) || currentAppName.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_app_message)
            return
        }

        if ((currentDescription == null) || currentDescription.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_app_desc_message)
            return
        }

        if (app.value != null) {
            app.value?.update(currentAppName, currentDescription, currentWebAddr)
            updateApp(app.value!!)
        } else {
            var newApp = App(currentAppName, currentDescription, currentWebAddr)
            createApp(newApp)
        }
    }

    private fun createApp(app: App) = viewModelScope.launch {
        if (app != null) {
            app.tppId = tpp?.getId() ?: "-1"
            tppsRepository.saveApp(tpp!!, app)
        }

        _appUpdatedEvent.value = Event(Unit)
    }

    private fun updateApp(app: App) = viewModelScope.launch {
        if (app != null) {
            app.tppId = tpp?.getId() ?: "-1"
            tppsRepository.updateApp(tpp!!, app)
        }

        _appUpdatedEvent.value = Event(Unit)
    }

    private fun updateTpp(tpp: Tpp)  = viewModelScope.launch {
        tppsRepository.saveTpp(tpp)

        _appUpdatedEvent.value = Event(Unit)
    }
}
