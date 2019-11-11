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
package com.applego.oblog.tppwatch.tpps

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import java.util.ArrayList

/**
 * ViewModel for the tpp list screen.
 */
class TppsViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<Tpp>>().apply { value = emptyList() }
    val items: LiveData<List<Tpp>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noTppsLabel = MutableLiveData<Int>()
    val noTppsLabel: LiveData<Int> = _noTppsLabel

    private val _noTppIconRes = MutableLiveData<Int>()
    val noTppIconRes: LiveData<Int> = _noTppIconRes

    private val _tppsAddViewVisible = MutableLiveData<Boolean>()
    val tppsAddViewVisible: LiveData<Boolean> = _tppsAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var _currentFiltering = TppsFilterType.ALL_TPPS

    private var _currentFilterString: String = ""

    // Not used at the moment
    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openTppEvent = MutableLiveData<Event<String>>()
    val openTppEvent: LiveData<Event<String>> = _openTppEvent

    private val _newTppEvent = MutableLiveData<Event<Unit>>()
    val newTppEvent: LiveData<Event<Unit>> = _newTppEvent

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        // Set initial state
        setFiltering(TppsFilterType.ALL_TPPS)

        loadTpps(false)
    }

    /**
     * Sets the current tpp filtering type.
     *
     * @param requestType Can be [TppsFilterType.ALL_TPPS],
     * [TppsFilterType.FOLLOWED_TPPS], or
     * [TppsFilterType.ACTIVE_TPPS]
     */
    fun setFiltering(requestType: TppsFilterType) {
        _currentFiltering = requestType

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (requestType) {
            TppsFilterType.ALL_TPPS -> {
                setFilter(
                    R.string.label_all, R.string.no_tpps_all,
                    R.drawable.logo_no_fill, true
                )
            }
            TppsFilterType.ACTIVE_TPPS -> {
                setFilter(
                    R.string.label_active, R.string.no_tpps_active,
                    R.drawable.ic_check_circle_96dp, false
                )
            }
            TppsFilterType.FOLLOWED_TPPS -> {
                setFilter(
                    R.string.label_followed, R.string.no_tpps_followed,
                    R.drawable.ic_verified_user_96dp, false
                )
            }
        }
    }

    private fun setFilter(
            @StringRes filteringLabelString: Int, @StringRes noTppsLabelString: Int,
            @DrawableRes noTppIconDrawable: Int, tppsAddVisible: Boolean
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noTppsLabel.value = noTppsLabelString
        _noTppIconRes.value = noTppIconDrawable
        _tppsAddViewVisible.value = tppsAddVisible
    }

    /*fun clearFollowedTpps() {
        viewModelScope.launch {
            tppsRepository.clearFollowedTpps()
            showSnackbarMessage(R.string.followed_tpps_cleared)
            // Refresh list to show the new state
            loadTpps(false)
        }
    }*/

    fun loadEbaDirectory() {
        viewModelScope.launch {
            //tppsRepository.clearFollowedTpps()
            showSnackbarMessage(R.string.loading)
            // Refresh list to show the new state
            loadTpps(true)
        }
    }

    fun followTpp(tpp: Tpp, followed: Boolean) = viewModelScope.launch {
        if (followed) {
            tppsRepository.followTpp(tpp)
            showSnackbarMessage(R.string.tpp_marked_followed)
        } else {
            tppsRepository.activateTpp(tpp)
            showSnackbarMessage(R.string.tpp_marked_active)
        }
        // Refresh list to show the new state
        //loadTpps(false)
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    fun addNewTpp() {
        _newTppEvent.value = Event(Unit)
    }

    /**
     * Called by Data Binding.
     */
    fun openTpp(tppId: String) {
        _openTppEvent.value = Event(tppId)
    }

    fun showEditResultMessage(result: Int) {
        when (result) {
            EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_saved_tpp_message)
            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_tpp_message)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_tpp_message)
        }
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [LocalTppDataSource]
     */
    fun loadTpps(forceUpdate: Boolean) {
        _dataLoading.value = true

        wrapEspressoIdlingResource {

            viewModelScope.launch {
                val tppsResult = tppsRepository.getTpps(forceUpdate)

                if (tppsResult is Success) {
                    val tpps = tppsResult.data

                    val tppsToShow = ArrayList<Tpp>()
                    // We filter the tpps based on the requestType
                    for (tpp in tpps) {
                        when (_currentFiltering) {
                            TppsFilterType.ALL_TPPS -> tppsToShow.add(tpp)
                            TppsFilterType.ACTIVE_TPPS -> if (tpp.isActive) {
                                tppsToShow.add(tpp)
                            }
                            TppsFilterType.FOLLOWED_TPPS -> if (tpp.isFollowed) {
                                tppsToShow.add(tpp)
                            }
                        }
                    }
                    isDataLoadingError.value = false
                    _items.value = ArrayList(tppsToShow)
                } else {
                    isDataLoadingError.value = false
                    _items.value = emptyList()
                    showSnackbarMessage(R.string.loading_tpps_error)
                }

                _dataLoading.value = false
            }
        }
    }


    fun filterByTitle(searchString: String) {
        _dataLoading.value = true

        _currentFilterString = searchString
        wrapEspressoIdlingResource {

            viewModelScope.launch {
                val tppsResult = tppsRepository.getTpps(false)

                if (tppsResult is Success) {
                    val tpps = tppsResult.data

                    val tppsToShow = ArrayList<Tpp>()
                    // We filter the tpps based on the requestType
                    for (tpp in tpps) {
                        if (tpp.title.contains(_currentFilterString, true)) {
                            tppsToShow.add(tpp)
                        }
                    }
                    isDataLoadingError.value = false
                    _items.value = ArrayList(tppsToShow)
                } else {
                    isDataLoadingError.value = false
                    _items.value = emptyList()
                    showSnackbarMessage(R.string.loading_tpps_error)
                }

                _dataLoading.value = false
            }
        }
    }

    fun refresh() {
        loadTpps(false)
    }

    fun filterTppsByCountry(country: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun filterTppsByService(service: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun showRevokedOnly() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
