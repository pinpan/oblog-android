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

package com.applego.oblog.tppwatch.addedittpp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.Tpp
import com.applego.oblog.tppwatch.data.source.TppsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add/Edit screen.
 */
class AddEditTppViewModel(
    private val tppsRepository: TppsRepository
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
                if (result is Success) {
                    onTppLoaded(result.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    private fun onTppLoaded(tpp: Tpp) {
        title.value = tpp.title
        description.value = tpp.description
        tppFollowed = tpp.isFollowed
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
        if (Tpp(currentTitle, currentDescription).isEmpty) {
            _snackbarText.value = Event(R.string.empty_tpp_message)
            return
        }

        val currentTppId = tppId
        if (isNewTpp || currentTppId == null) {
            createTpp(Tpp(currentTitle, currentDescription))
        } else {
            val tpp = Tpp(currentTitle, currentDescription, tppFollowed, currentTppId)
            updateTpp(tpp)
        }
    }

    private fun createTpp(newTpp: Tpp) = viewModelScope.launch {
        tppsRepository.saveTpp(newTpp)
        _tppUpdatedEvent.value = Event(Unit)
    }

    private fun updateTpp(tpp: Tpp) {
        if (isNewTpp) {
            throw RuntimeException("updateTpp() was called but tpp is new.")
        }
        viewModelScope.launch {
            tppsRepository.saveTpp(tpp)
            _tppUpdatedEvent.value = Event(Unit)
        }
    }
}
