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

    private var fetchedItems = ArrayList<Tpp>()
    private val _items = MutableLiveData<List<Tpp>>().apply { value = emptyList() }
    val items: MutableLiveData<List<Tpp>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private var searchFilter = SearchFilter()
    //private var _currentFilterString: String = ""
    private var _currentFiltering = TppsFilterType.ALL_TPPS
    private val _currentFilteringLabel = MutableLiveData<Int>()
    //val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

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
        //_currentFiltering = requestType
        searchFilter.updateUserInterests(requestType)

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (requestType) {
            TppsFilterType.ALL_TPPS -> {
                setFilterStatusViews(
                    R.string.label_all, R.string.no_tpps_all,
                    R.drawable.logo_no_fill, true
                )
            }
            TppsFilterType.USED_TPPS -> {
                setFilterStatusViews(
                    R.string.label_active, R.string.no_tpps_active,
                    R.drawable.ic_check_circle_96dp, false
                )
            }
            TppsFilterType.FOLLOWED_TPPS -> {
                setFilterStatusViews(
                    R.string.label_followed, R.string.no_tpps_followed,
                    R.drawable.ic_verified_user_96dp, false
                )
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
        tppsRepository.setTppFollowedFlag(tpp, followed)
        showSnackbarMessage(R.string.tpp_marked_followed)

        // Refresh single Tpp
        //loadTpps(false)
    }

    fun activateTpp(tpp: Tpp, active: Boolean) = viewModelScope.launch {
        tppsRepository.setTppActivateFlag(tpp, active)
        showSnackbarMessage(R.string.tpp_marked_active)

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
                    fetchedItems = ArrayList<Tpp>()
                    fetchedItems.addAll(tppsResult.data)

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
        searchFilter.title = searchString

        //wrapEspressoIdlingResource {

            viewModelScope.launch {
                val tppsToShow = getTppsByGlobalFilter()
                _items.value = tppsToShow

                _dataLoading.value = false
            }
        //}
    }

    fun getTppsByGlobalFilter(): List<Tpp> {

        _dataLoading.value = true

        var tppsList: List<Tpp> = fetchedItems
        if (tppsList.isNullOrEmpty()) {
            return tppsList
        }

        var tppsToShow: MutableList<Tpp> = filterTppsByName(tppsList)

        if (!searchFilter.all) {
            tppsList = tppsToShow
            tppsToShow = filterTppsByUserInterest(tppsList, searchFilter.userInterests)
        }

        tppsList = tppsToShow
        tppsToShow = filterTppsByCountry(tppsList, searchFilter.countries)

        tppsList = tppsToShow
        tppsToShow = filterTppsByService(tppsList, searchFilter.services)

        _dataLoading.value = false

        return tppsToShow
    }

    private fun filterTppsByName(inputTpps: List<Tpp>): MutableList<Tpp> {
        val filteredTpps = ArrayList<Tpp>()

        if (searchFilter.title.isNullOrBlank()) {
            filteredTpps.addAll(inputTpps)
        } else {
            for (tpp in inputTpps) {
                if (tpp.title.contains(searchFilter.title, true)) {
                    (filteredTpps as ArrayList<Tpp>).add(tpp)
                }
            }
        }

        return filteredTpps
    }

    private fun filterTppsByUserInterest(inputTpps: List<Tpp>, userInterests: List<TppsFilterType>): MutableList<Tpp> {
        val filteredTpps = ArrayList<Tpp>()

        // userInterests works as discriminator. If empty -> show all
        if (userInterests.isNullOrEmpty() || userInterests.contains(TppsFilterType.ALL_TPPS)) {
            filteredTpps.addAll(inputTpps)
        } else {
            // individual interests are then OR-ed
            inputTpps.forEach { tpp ->
                userInterests.forEach { interest ->
                    when (interest) {
                        //TppsFilterType.ALL_TPPS -> filteredTpps.add(tpp)
                        TppsFilterType.USED_TPPS -> if (tpp.isActive) {
                            filteredTpps.add(tpp)
                        }
                        TppsFilterType.FOLLOWED_TPPS -> if (tpp.isFollowed) {
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
        if (searchFilter.countries.isNullOrBlank() || searchFilter.countries.equals("<ALL>")) {
                filteredTpps.addAll(inputTpps)
        } else {
            searchFilter.countries.forEach {
                inputTpps?.forEach {
                    if (it.country.equals(country)) {
                        filteredTpps.add(it)
                    }
                }
            }
        }

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
        searchFilter.countries = country

        _items.value = getTppsByGlobalFilter()
    }

    fun filterTppsByService(service: String) {
        searchFilter.services = service

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
        //outState.putBoolean("created", viewModel.searchFilter.created)

        outState.putString("title", searchFilter.title)
        outState.putBoolean("searchDescription", searchFilter.searchDescription)
        outState.putString("countries", searchFilter.countries)
        outState.putString("services", searchFilter.services)

        outState.putBoolean("installed", searchFilter.installed)
        outState.putBoolean("psd2Only", searchFilter.psd2Only)
        outState.putBoolean("revokedOnly", searchFilter.revokedOnly)
        outState.putBoolean("showFis", searchFilter.showFis)
        outState.putBoolean("followed", searchFilter.followed)
        outState.putBoolean("active", searchFilter.active)
    }

    fun setupSearchFilter(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            searchFilter.title = savedInstanceState?.getString("", "") ?: ""
            searchFilter.searchDescription = savedInstanceState?.getBoolean("searchDescription", false)
                    ?: false
            searchFilter.countries = savedInstanceState?.getString("countries", "") ?: ""
            searchFilter.services = savedInstanceState?.getString("services", "") ?: ""

            if (savedInstanceState.getBoolean("installed", true)) {
                searchFilter.userInterests.add(TppsFilterType.USED_TPPS)
            }

            // TODO: Duplicates installed or has semantic of: INSTALLED AND USED?
            if (savedInstanceState.getBoolean("active", true)) {
                searchFilter.userInterests.add(TppsFilterType.USED_TPPS) //searchFilter.active
            }

            if (savedInstanceState.getBoolean("followed", true)) {
                searchFilter.userInterests.add(TppsFilterType.FOLLOWED_TPPS) //searchFilter.followed
            }

            if (savedInstanceState?.getBoolean("psd2Only", false)) {
                searchFilter.userInterests.add(TppsFilterType.PSD2_ONLY_TPPS) //searchFilter.psd2Only
            }

            if (savedInstanceState?.getBoolean("onlyPsd2", false)) {
                searchFilter.userInterests.add(TppsFilterType.ONLY_PSD2_TPPS) //searchFilter.psd2Only
            }

            if (savedInstanceState?.getBoolean("showFis", false)) {
                searchFilter.userInterests.add(TppsFilterType.FIS_AS_TPPS) //searchFilter.showFis
            }

            // THIS is excluding all other filteres - means:
            //    Get all whos PSD2 license was revoked regardless if are used, followed, fis ...
            if (savedInstanceState?.getBoolean("revokedOnly", false)) {
                searchFilter.userInterests.add(TppsFilterType.REVOKED_ONLY_TPPS) //searchFilter.revokedOnly
            }
        }
    }
}
