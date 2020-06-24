package com.applego.oblog.tppwatch.addedittpp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.util.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.NcaEntity
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add/Edit screen.
 */
class AddEditTppViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    // Two-way databinding, exposing MutableLiveData
    val entityName = MutableLiveData<String>()

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

    fun start(tppId: String?) {
        if (_dataLoading.value == true) {
            return
        }

        this.tppId = tppId
        if (tppId == null) {
            // No need to populate, it's a new tpp
            isNewTpp = true
            return
        }

        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }

        isNewTpp = false
        _dataLoading.value = true

        viewModelScope.launch {
            tppsRepository.getTpp(tppId).let { result ->
                _dataLoading.value = false
                if (result is Success) {
                    onTppLoaded(result.data)
                }
            }
        }
    }

    private fun onTppLoaded(tpp: Tpp) {
        entityName.value = tpp.getEntityName()
        description.value = tpp.getDescription()
        tppFollowed = tpp.isFollowed()
        isDataLoaded = true
    }

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
        if (isNewTpp || currentTppId == null) {
            createTpp(Tpp(EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = currentTitle, _description = currentDescription, _globalUrn = "", _ebaEntityVersion = "", _country = "cz", entityType = EbaEntityType.NONE), NcaEntity()))
        } else {
            val tpp = Tpp(EbaEntity(_entityId = "28173282", _entityCode = "Entity_CZ28173282", _entityName = currentTitle, _description = currentDescription, _globalUrn = "", _ebaEntityVersion = currentTppId, _country = "cz", entityType = EbaEntityType.NONE), NcaEntity())
            updateTpp(tpp)
        }
    }

    private fun createTpp(newTpp: Tpp) = viewModelScope.launch {
        tppsRepository.saveTpp(newTpp)
        _tppUpdatedEvent.value = Event(Unit)
    }

    private fun updateTpp(tpp: Tpp) {
        if (isNewTpp) {
            throw RuntimeException("updateTppEntity() was called but tpp is new.")
        }
        viewModelScope.launch {
            tppsRepository.saveTpp(tpp)
            _tppUpdatedEvent.value = Event(Unit)
        }
    }
}
