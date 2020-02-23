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

import android.os.Bundle
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
import com.applego.oblog.tppwatch.data.source.TppsRepository
import com.applego.oblog.tppwatch.data.source.local.*
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import java.util.ArrayList

/**
 * ViewModel for the tpp list screen.
 */
class TppsViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    private var fetchedItems : List<Tpp> = emptyList()
    private val _items = MutableLiveData<List<Tpp>>().apply { value = emptyList() }
    val items: MutableLiveData<List<Tpp>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private var _searchFilter = SearchFilter()
    val  searchFilter = _searchFilter

    private val _currentFilteringLabel = MutableLiveData<Int>()

    private val _noTppsLabel = MutableLiveData<Int>()
    val noTppsLabel: LiveData<Int> = _noTppsLabel

    private val _noTppIconRes = MutableLiveData<Int>()
    val noTppIconRes: LiveData<Int> = _noTppIconRes

    private val _tppsAddViewVisible = MutableLiveData<Boolean>()
    val tppsAddViewVisible: LiveData<Boolean> = _tppsAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

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

        refresh()
    }

    fun refresh() {
        loadTpps(false)
    }

    /**
     * Sets the current tpp filtering type.
     *
     * @param requestType Can be [TppsFilterType.ALL_TPPS],
     * [TppsFilterType.FOLLOWED_TPPS], or
     * [TppsFilterType.USED_TPPS]
     */
    fun setFiltering(requestType: TppsFilterType) {
        _searchFilter.updateUserSelection(requestType)

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        if (_searchFilter.all) {
            setFilterStatusViews(
                R.string.label_all, R.string.no_tpps_all,
                R.drawable.logo_no_fill, true
            )
        } else {
            when (requestType) {
                TppsFilterType.USED_TPPS -> {
                    setFilterStatusViews(
                            R.string.label_active, R.string.no_tpps_active,
                            R.drawable.ic_check_circle_96dp, true
                    )
                }
                TppsFilterType.FOLLOWED_TPPS -> {
                    setFilterStatusViews(
                            R.string.label_followed, R.string.no_tpps_followed,
                            R.drawable.ic_verified_user_96dp, true
                    )
                }
                TppsFilterType.FIS_AS_TPPS -> {
                    setFilterStatusViews(
                            R.string.label_fis, R.string.no_fis_tpps,
                            R.drawable.ic_verified_user_96dp, true
                    )
                }
                TppsFilterType.PSD2_TPPS -> {
                    setFilterStatusViews(
                            R.string.label_psd2_only, R.string.no_psd2_only,
                            R.drawable.ic_verified_user_96dp, true
                    )
                }
                /* This is handled by the slider
                TppsFilterType.ONLY_PSD2_TPPS -> {
                    setFilterStatusViews(
                            R.string.label_followed, R.string.no_tpps_followed,
                            R.drawable.ic_verified_user_96dp, false
                    )
                }*/
                TppsFilterType.REVOKED_TPPS -> {
                    setFilterStatusViews(
                            R.string.label_revoked, R.string.no_revoked_tpps,
                            R.drawable.ic_verified_user_96dp, false
                    )
                }
            }
        }
    }

    private fun setFilterStatusViews(
            @StringRes filteringLabelString: Int, @StringRes noTppsLabelString: Int,
            @DrawableRes noTppIconDrawable: Int, tppsAddVisible: Boolean
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noTppsLabel.value = noTppsLabelString
        _noTppIconRes.value = noTppIconDrawable
        _tppsAddViewVisible.value = tppsAddVisible
    }

    fun loadEbaDirectory() {
        viewModelScope.launch {
            showSnackbarMessage(R.string.loading)
            // Load from Eba
            loadTpps(true)
        }
    }

    fun followTpp(tpp: Tpp, followed: Boolean) = viewModelScope.launch {

        viewModelScope.launch {
            tppsRepository.setTppFollowedFlag(tpp, followed)
            showSnackbarMessage(R.string.tpp_marked_followed)
        }

        // Refresh single Tpp
        //loadTpps(false)
    }

    fun activateTpp(tpp: Tpp, active: Boolean) = viewModelScope.launch {
        viewModelScope.launch {
            tppsRepository.setTppActivateFlag(tpp, active)
            showSnackbarMessage(R.string.tpp_marked_active)

        }
            // Refresh single Tpp
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

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [LocalTppDataSource]
     */
    fun loadTpps(forceUpdate: Boolean) {
        _dataLoading.value = true

        wrapEspressoIdlingResource {

            viewModelScope.launch {
                val tppsResult = tppsRepository.getTpps(forceUpdate)

                if (tppsResult is Success) {
                    fetchedItems = tppsResult.data

                    _items.value = getTppsByGlobalFilter()

                    isDataLoadingError.value = false
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
        _searchFilter.title = searchString

        wrapEspressoIdlingResource {

            viewModelScope.launch {
                val tppsToShow = getTppsByGlobalFilter()
                _items.value = tppsToShow

                _dataLoading.value = false
            }
        }
    }

    fun getTppsByGlobalFilter(): List<Tpp> {

        _dataLoading.value = true

        var tppsList: List<Tpp> = fetchedItems
        if (tppsList.isNullOrEmpty()) {
            return tppsList
        }

        var tppsToShow: MutableList<Tpp> = filterTppsByName(tppsList)

        if (!_searchFilter.all) {
            tppsList = tppsToShow
            tppsToShow = filterTppsByUserInterest(tppsList, _searchFilter.userSelectedFilterTypes)
        }

        if (!_searchFilter.countries.isNullOrBlank() && !_searchFilter.countries.equals("<ALL>")) {
            tppsList = tppsToShow
            tppsToShow = filterTppsByCountry(tppsList, _searchFilter.countries)
        }

        tppsList = tppsToShow
        tppsToShow = filterTppsByService(tppsList, _searchFilter.services)

        _dataLoading.value = false

        return tppsToShow
    }

    private fun filterTppsByName(inputTpps: List<Tpp>): MutableList<Tpp> {
        val filteredTpps = ArrayList<Tpp>()

        if (_searchFilter.title.isNullOrBlank()) {
            filteredTpps.addAll(inputTpps)
        } else {
            for (tpp in inputTpps) {
                if (tpp.title.contains(_searchFilter.title, true)) {
                    (filteredTpps as ArrayList<Tpp>).add(tpp)
                }
            }
        }

        return filteredTpps
    }

    private fun filterTppsByUserInterest(inputTpps: List<Tpp>, userInterests: Map<TppsFilterType, Boolean>): MutableList<Tpp> {
        val filteredTpps = ArrayList<Tpp>()

        // userSelectedFilterTypes works as discriminator. If empty -> show all
        if ((userInterests.get(TppsFilterType.ALL_TPPS) ?: false) || _searchFilter.all) {
            filteredTpps.addAll(inputTpps)
        } else {
            // individual interests are then OR-ed
            inputTpps.forEach { tpp ->
                userInterests.forEach {
                    when (it.key) {
                        //TppsFilterType.ALL_TPPS -> filteredTpps.add(tpp)
                        TppsFilterType.USED_TPPS -> if (it.value && tpp.isActive) {
                            filteredTpps.add(tpp)
                        }
                        TppsFilterType.FOLLOWED_TPPS -> if (it.value && tpp.isFollowed) {
                            filteredTpps.add(tpp)
                        }
                        TppsFilterType.PSD2_TPPS -> if (it.value && tpp.isPsd2) {
                            filteredTpps.add(tpp)
                        }
                        TppsFilterType.PSD2_TPPS -> if (it.value && tpp.isFis) {
                            filteredTpps.add(tpp)
                        }
                    }
                }
            }
        }

        return filteredTpps
    }

    fun filterTppsByCountry(inputTpps : List<Tpp>?, country: String) : MutableList<Tpp> {

        if (inputTpps.isNullOrEmpty()) {
            return ArrayList<Tpp>()
        }

        var filteredTpps = ArrayList<Tpp>()
        /*if (_searchFilter.countries.isNullOrBlank() || _searchFilter.countries.equals("<ALL>")) {
                filteredTpps.addAll(inputTpps)
        } else {*/
            _searchFilter.countries.forEach {
                inputTpps?.forEach {
                    if (it.country.equals(country)) {
                        filteredTpps.add(it)
                    }
                }
            }
        //}

        return filteredTpps
    }

    fun filterTppsByService(inputTpps: List<Tpp>, service: String) : MutableList<Tpp> {

        if (inputTpps.isNullOrEmpty()) {
            return ArrayList<Tpp>()
        }

        val filteredTpps = ArrayList<Tpp>()
        if (service.isNullOrBlank() || service.equals("<ALL>")) {
            filteredTpps.addAll(inputTpps)
        } else {
            val psdService = EbaService.findPsd2Service(service)
            inputTpps?.forEach lit@{ tpp ->
                if (tpp.ebaPassport != null) {
                    tpp.ebaPassport.countryMap.entries.forEach() {
                        if (it.value != null) {
                            val services = it.value as List<Service>
                            services.forEach {serv ->
                                if (serv.title.equals(psdService.code)) {
                                    filteredTpps.add(tpp)
                                    return@lit;
                                }
                            }
                        }
                    }
                }
            }
        }

        return filteredTpps
    }

    fun filterTppsByCountry(country: String) {
        _searchFilter.countries = country

        _items.value = getTppsByGlobalFilter()
    }

    fun filterTppsByService(service: String) {
        _searchFilter.services = service

        _items.value = getTppsByGlobalFilter()
    }

    fun showRevokedOnly() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    fun saveSearchFilter(outState: Bundle) {
        //outState.putBoolean("created", viewModel._searchFilter.created)

        outState.putString("title", _searchFilter.title)
        outState.putBoolean("searchDescription", _searchFilter.searchDescription)
        outState.putString("countries", _searchFilter.countries)
        outState.putString("services", _searchFilter.services)

        outState.putBoolean("installed", _searchFilter.installed)
        outState.putBoolean("psd2Only", _searchFilter.psd2Only)
        outState.putBoolean("revokedOnly", _searchFilter.revokedOnly)
        outState.putBoolean("showFis", _searchFilter.showFis)
        outState.putBoolean("followed", _searchFilter.followed)
        outState.putBoolean("active", _searchFilter.active)
    }

    fun setupSearchFilter(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            _searchFilter.title = savedInstanceState?.getString("", "") ?: ""
            _searchFilter.searchDescription = savedInstanceState?.getBoolean("searchDescription", false)
                    ?: false
            _searchFilter.countries = savedInstanceState?.getString("countries", "") ?: ""
            _searchFilter.services = savedInstanceState?.getString("services", "") ?: ""

            if (savedInstanceState.getBoolean("installed", true)) {
                _searchFilter.userSelectedFilterTypes.put(TppsFilterType.USED_TPPS, true)
            }

            // TODO: Duplicates installed or has semantic of: INSTALLED AND USED?
            if (savedInstanceState.getBoolean("active", true)) {
                _searchFilter.userSelectedFilterTypes.put(TppsFilterType.USED_TPPS, true) //_searchFilter.active
            }

            if (savedInstanceState.getBoolean("followed", true)) {
                _searchFilter.userSelectedFilterTypes.put(TppsFilterType.FOLLOWED_TPPS, true) //_searchFilter.followed
            }

            if (savedInstanceState?.getBoolean("psd2Only", false)) {
                _searchFilter.userSelectedFilterTypes.put(TppsFilterType.PSD2_TPPS, true) //_searchFilter.psd2Only
            }

            if (savedInstanceState?.getBoolean("onlyPsd2", false)) {
                _searchFilter.userSelectedFilterTypes.put(TppsFilterType.ONLY_PSD2_TPPS, true) //_searchFilter.psd2Only
            }

            if (savedInstanceState?.getBoolean("showFis", false)) {
                _searchFilter.userSelectedFilterTypes.put(TppsFilterType.FIS_AS_TPPS, true) //_searchFilter.showFis
            }

            // THIS is excluding all other filteres - means:
            //    Get all whos PSD2 license was revoked regardless if are used, followed, fis ...
            if (savedInstanceState?.getBoolean("revokedOnly", false)) {
                _searchFilter.userSelectedFilterTypes.put(TppsFilterType.REVOKED_TPPS, true) //_searchFilter.revokedOnly
            }
        }
    }
}
