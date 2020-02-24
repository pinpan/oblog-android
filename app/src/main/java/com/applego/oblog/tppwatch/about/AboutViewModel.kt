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

package com.applego.oblog.tppwatch.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.TppsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add/Edit screen.
 */
class AboutViewModel(
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
        if (Tpp("Entity_CZ28173282", currentTitle, currentDescription).isEmpty) {
            _snackbarText.value = Event(R.string.empty_tpp_message)
            return
        }

        val currentTppId = tppId
        if (isNewTpp || currentTppId == null) {
            createTpp(Tpp("Entity_CZ28173282", currentTitle, currentDescription))
        } else {
            val tpp = Tpp("Entity_CZ28173282", currentTitle, currentDescription, "", currentTppId)
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
