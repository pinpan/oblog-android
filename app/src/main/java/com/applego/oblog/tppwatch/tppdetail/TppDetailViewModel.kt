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
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.launch

/**
 * ViewModel for the Details screen.
 */
class TppDetailViewModel(
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

    private val _deleteTppEvent = MutableLiveData<Event<Unit>>()
    val deleteTppEvent: LiveData<Event<Unit>> = _deleteTppEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val tppId: String?
        get() = _tpp.value?.id

    // This LiveData depends on another so we can use a transformation.
    val followed: LiveData<Boolean> = Transformations.map(_tpp) { input: Tpp? ->
        input?.isFollowed ?: false
    }


    fun deleteTpp() = viewModelScope.launch {
        tppId?.let {
            tppsRepository.deleteTpp(it)
            _deleteTppEvent.value = Event(Unit)
        }
    }

    fun editTpp() {
        _editTppEvent.value = Event(Unit)
    }

    fun setFollowed(follow: Boolean) = viewModelScope.launch {
        val tpp = _tpp.value ?: return@launch
        if (follow) {
            tppsRepository.followTpp(tpp)
            showSnackbarMessage(R.string.tpp_marked_followed)
        } else {
            tppsRepository.activateTpp(tpp)
            showSnackbarMessage(R.string.tpp_marked_active)
        }
    }

    fun start(tppId: String?, forceRefresh: Boolean = false) {
        if (_isDataAvailable.value == true && !forceRefresh || _dataLoading.value == true) {
            return
        }

        // Show loading indicator
        _dataLoading.value = true

        wrapEspressoIdlingResource {

            viewModelScope.launch {
                if (tppId != null) {
                    tppsRepository.getTpp(tppId, false).let { result ->
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
        tppId?.let { start(it, true) }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}
