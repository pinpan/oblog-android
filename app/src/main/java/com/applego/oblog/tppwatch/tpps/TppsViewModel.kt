package com.applego.oblog.tppwatch.tpps

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.util.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.*
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.source.local.*
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import java.util.ArrayList

/**
 * ViewModel for the tpp list screen.
 */
class TppsViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    //private var fetchedItems : List<Tpp> = emptyList()
    private val _allItems: MutableLiveData<List<Tpp>> = MutableLiveData(listOf<Tpp>())
    val allItems: LiveData<List<Tpp>> = _allItems

    private val _items = MutableLiveData<List<Tpp>>().apply { value = allItems.value }
    val items: LiveData<List<Tpp>> = _items

    private val _isFiltered = MutableLiveData<Boolean>(isFiltered()/*_allItems.value?.size == _items.value?.size*/)
    val isFiltered: LiveData<Boolean> = _isFiltered

    private fun isFiltered() : Boolean {
        return (_allItems.value?.size == _items.value?.size)
    }

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

    private val _refreshEvent = MutableLiveData<Event<Unit>>()
    val refreshEvent: LiveData<Event<Unit>> = _refreshEvent

    private val _aboutEvent = MutableLiveData<Event<Unit>>()
    val aboutEvent: LiveData<Event<Unit>> = _aboutEvent

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    // This LiveData depends on another so we can use a transformation.
    val filtered : LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        // Set initial state
        searchFilter.init()

        setFiltering(TppsFilterType.ALL_TPPs)

        refresh()
    }

    fun refresh() {
        loadTpps(false)
    }

    fun refreshTpp(tppId : String?) {
        if (allItems.value.isNullOrEmpty() && !tppId.isNullOrBlank() && !tppId.equals("0")) {
            val tpp = findTppInList(allItems.value!!, tppId)
            runBlocking {
                if (tpp != null) {
                    tppsRepository.refreshTpp(tpp!!)
                }
            }
            loadTpps(false)
        }
    }

    private fun findTppInList(items: List<Tpp>, tppId: String): Tpp? {
        items.forEach() {
            if (it.getId() == tppId) {
                return it
            }
        }
        return null
    }

    /**
     * Sets the current tpp filtering type.
     * Keep an Eye on this method:
     *    When filter criteria changes, fitler status view must be set otherwise tests and app brake
     *    with missing resource exception!
     *
     * @param requestType Can be [TppsFilterType.FOLLOWED_TPPs], or [TppsFilterType.USED_TPPs]
     */
    fun setFiltering(requestType: TppsFilterType) {
        _searchFilter.updateUserSelection(requestType)

        when (requestType) {
            TppsFilterType.ALL_TPPs -> {
                setFilterStatusViews(
                    R.string.label_all, R.string.no_tpps_all,
                    R.drawable.oblog_logo, true
                )
            }
            TppsFilterType.USED_TPPs -> {
                setFilterStatusViews(
                        R.string.label_used, R.string.no_tpps_used,
                        R.drawable.ic_check_circle_96dp, true
                )
            }
            TppsFilterType.FOLLOWED_TPPs -> {
                setFilterStatusViews(
                        R.string.label_followed, R.string.no_tpps_followed,
                        R.drawable.ic_verified_user_96dp, true
                )
            }
            TppsFilterType.ONLY_PSD2_FIs -> {
                setFilterStatusViews(
                        R.string.label_fis, R.string.no_fis_tpps,
                        R.drawable.ic_verified_user_96dp, true
                )
            }
            TppsFilterType.ONLY_PSD2_TPPs -> {
                setFilterStatusViews(
                        R.string.label_psd2_only, R.string.psd2_services_only,
                        R.drawable.ic_verified_user_96dp, true
                )
            }
            TppsFilterType.REVOKED_TPPs -> {
                setFilterStatusViews(
                        R.string.label_revoked, R.string.no_revoked_tpps,
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
        viewModelScope.launch {
            tppsRepository.setTppFollowedFlag(tpp, followed)
            showSnackbarMessage(R.string.tpp_marked_followed)
        }
    }

    fun activateTpp(tpp: Tpp, used: Boolean) = viewModelScope.launch {
        viewModelScope.launch {
            tppsRepository.setTppActivateFlag(tpp, used)
            showSnackbarMessage(R.string.tpp_marked_used)

        }
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
                val tppsResult = tppsRepository.getAllTpps(forceUpdate)

                if (tppsResult is Success) {
                    //fetchedItems = tppsResult.data
                    _allItems.value = tppsResult.data

                    _items.value = getTppsByGlobalFilter()
                    _isFiltered.value = isFiltered()

                    isDataLoadingError.value = false
                } else {
                    isDataLoadingError.value = true
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

        if (allItems.value.isNullOrEmpty()) {
            return listOf<Tpp>() //allItems.value!!
        }

        var tppsToShow = filterTppsByUserInterest(allItems.value, _searchFilter)

        if (!_searchFilter.countries.isNullOrBlank() && !_searchFilter.countries.equals("<ALL>")) {
            tppsToShow = filterTppsByCountry(tppsToShow, _searchFilter.countries)
        }

        if (!_searchFilter.services.isNullOrBlank() && !_searchFilter.services.equals("<ALL>")) {
            tppsToShow = filterTppsByService(tppsToShow, _searchFilter.services)
        }

        tppsToShow = filterTppsByName(tppsToShow)

        _dataLoading.value = false

        return tppsToShow
    }

    private fun filterTppsByName(inputTpps: List<Tpp>): MutableList<Tpp> {
        val filteredTpps = ArrayList<Tpp>()

        if (_searchFilter.title.isNullOrBlank()) {
            filteredTpps.addAll(inputTpps)
        } else {
            for (tpp in inputTpps) {
                if (tpp.getEntityName().contains(_searchFilter.title, true)) {
                    (filteredTpps as ArrayList<Tpp>).add(tpp)
                }
            }
        }

        return filteredTpps
    }

    private fun filterTppsByUserInterest(inputTpps : List<Tpp>?, searchFilter: SearchFilter): MutableList<Tpp> {
        val filteredTpps = ArrayList<Tpp>()

        inputTpps?.forEach { tpp ->
            var addIt : Boolean = when (searchFilter.pspType) {
                PspType.ONLY_PSD2_TPPs -> tpp.isPsd2Tpp()
                PspType.ONLY_ASPSPs -> tpp.isASPSP()
                else -> true
            }

            if (addIt) {
                if (!searchFilter.showRevoked) {
                    addIt = !tpp.ebaEntity.isRevoked()
                }

                if (searchFilter.showRevokedOnly) {
                    addIt = tpp.ebaEntity.isRevoked()
                }

                if (addIt) {
                    if (searchFilter.showUsedOnly) {
                        addIt = tpp.isUsed()
                    } else if (searchFilter.showFollowedOnly) {
                        addIt = tpp.isFollowed() || tpp.isUsed()
                    }

                    if (addIt) {
                        filteredTpps.add(tpp)
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
        _searchFilter.countries.forEach {
            inputTpps?.forEach {
                if (it.getCountry().equals(country)) {
                    filteredTpps.add(it)
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
                if (tpp.getEbaPassport() != null) {
                    tpp.getEbaPassport().serviceMap.entries.forEach() {
                        if (it.key.equals(psdService.code)) {
                            filteredTpps.add(tpp)
                            return@lit;
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

    /*fun isFiltered(): Boolean {
        return _allItems.value?.size != _items.value?.size
    }*/

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
        outState.putString("entityName", _searchFilter.title)
        outState.putBoolean("searchDescription", _searchFilter.searchDescription)
        outState.putString("countries", _searchFilter.countries)
        outState.putString("services", _searchFilter.services)
    }

    fun setupSearchFilter(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            _searchFilter.title = savedInstanceState?.getString("", "") ?: ""
            _searchFilter.searchDescription = savedInstanceState?.getBoolean("searchDescription", false)
            _searchFilter.countries = savedInstanceState.getString("countries", "") ?: ""
            _searchFilter.services = savedInstanceState.getString("services", "") ?: ""
        }
    }
}
