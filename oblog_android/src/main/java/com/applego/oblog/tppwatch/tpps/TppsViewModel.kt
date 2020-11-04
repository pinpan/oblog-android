package com.applego.oblog.tppwatch.tpps

import android.os.Bundle
import androidx.lifecycle.*
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.EbaService
import com.applego.oblog.tppwatch.data.model.InstType
import com.applego.oblog.tppwatch.data.model.SearchFilter
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.util.Event
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * ViewModel for the tpp list screen.
 */
class TppsViewModel(
        private val tppsRepository: TppsRepository
) : ViewModel() {

    private val _allItems: MutableLiveData<List<Tpp>> = MutableLiveData(listOf<Tpp>())
    val allItems: LiveData<List<Tpp>> = _allItems

    private val _displayedItems = MutableLiveData<List<Tpp>>().apply { value = allItems.value }
    val displayedItems: LiveData<List<Tpp>> = _displayedItems

    private val _dataLoadingLocalDB = MutableLiveData<Boolean>()
    val dataLoadingLocalDB: LiveData<Boolean> = _dataLoadingLocalDB

    private val _dataLoadingRemote = MutableLiveData<Boolean>()
    val dataLoadingRemote: LiveData<Boolean> = _dataLoadingRemote

    private var _searchFilter = SearchFilter()
    val  searchFilter = _searchFilter

    private val _orderByField = MutableLiveData<String>("entityName")
    val orderByField: LiveData<String> = _orderByField

    private val _orderByDirection = MutableLiveData<Boolean>(true)
    val orderByDirection: LiveData<Boolean> = _orderByDirection

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _openTppEvent = MutableLiveData<Event<String>>()
    val openTppEvent: LiveData<Event<String>> = _openTppEvent

    private val _newTppEvent = MutableLiveData<Event<Unit>>()
    val newTppEvent: LiveData<Event<Unit>> = _newTppEvent

    private val _aboutEvent = MutableLiveData<Event<Unit>>()
    val aboutEvent: LiveData<Event<Unit>> = _aboutEvent

    private val _loadProgress = MutableLiveData<Event<Paging>>()
    val loadProgress: LiveData<Event<Paging>> = _loadProgress

    private val _loadProgressStart = MutableLiveData<Event<Int>>()
    val loadProgressStart: LiveData<Event<Int>> = _loadProgressStart

    private val _loadProgressEnd = MutableLiveData<Event<Boolean>>()
    val loadProgressEnd: LiveData<Event<Boolean>> = _loadProgressEnd

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_displayedItems) {
        it.isEmpty()
    }

    val showRevoked = MutableLiveData<Boolean>()
    val showRevokedOnly = MutableLiveData<Boolean>()

    val dataLoading = MediatorLiveData<Boolean>();

    init {
        searchFilter.init()

         setFiltering(TppsFilterType.ALL_INST)

        dataLoading.addSource(_dataLoadingRemote, { value ->
            dataLoading.setValue((_dataLoadingLocalDB.value
                    ?: false) || value)
        });
        dataLoading.addSource(dataLoadingLocalDB, { value ->
            dataLoading.setValue((_dataLoadingRemote.value
                    ?: false) || value)
        });
    }

    fun isFiltered() : Boolean {
        return (_allItems.value?.size != _displayedItems.value?.size)
    }

    fun refreshTpp(tppId: String?) {
        if (!allItems.value.isNullOrEmpty() && !tppId.isNullOrBlank() && !tppId.equals("0")) {
            val tpp = findTppInList(allItems.value!!, tppId)
            runBlocking {
                if (tpp != null) {
                    tppsRepository.refreshTpp(tpp)
                }
            }
            // TODO: Check if this is needed: loadTpps()
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
     * @param requestType Can be one of the [TppsFilterType], or [TppsFilterType.USED]
     */
    fun setFiltering(requestType: TppsFilterType) {
        _searchFilter.updateUserSelection(requestType)

        when (requestType) {
            TppsFilterType.REVOKED -> {
                _searchFilter.showRevoked = !_searchFilter.showRevoked
                if (!_searchFilter.showRevoked) {
                    _searchFilter.showRevokedOnly = false
                }
                showRevoked.value = _searchFilter.showRevoked
                showRevokedOnly.value = _searchFilter.showRevokedOnly
            }
            TppsFilterType.REVOKED_ONLY -> {
                _searchFilter.showRevokedOnly = !_searchFilter.showRevokedOnly
                if (_searchFilter.showRevokedOnly) {
                    _searchFilter.showRevoked = true
                }
                showRevokedOnly.value = _searchFilter.showRevokedOnly
                showRevoked.value = _searchFilter.showRevoked
            }

            TppsFilterType.USED -> {
                _searchFilter.showUsedOnly = !_searchFilter.showUsedOnly
            }
            TppsFilterType.FOLLOWED -> {
                _searchFilter.showFollowedOnly = !_searchFilter.showFollowedOnly
            }
        }
    }

    /**
      *  (RE)Loads EBA directory/repository from OBLOG API
      */
    // TODO: Set warning message than "Data is old" to be displayed,
    //  until refresh succeeds next time. May be for Remote updates only?
    fun loadEbaDirectory() {
        if (!(_dataLoadingRemote.value?.equals(true) ?: false)) {
            _dataLoadingRemote.value = true
            _displayedItems.value = emptyList()

            showSnackbarMessage(R.string.loading)
            wrapEspressoIdlingResource {
                viewModelScope.launch {
                    var paging = Paging(100, 1, 0, true)
                    while (!paging.last) {

                        val tppsResult = tppsRepository.fetchTppsPageFromRemoteDatasource(paging)
                        if (tppsResult is Success) {
                            if (paging.page == 1) {
                                _loadProgressStart.value = Event(tppsResult.data.paging.totalPages)
                            }

                            _loadProgress.value = Event(paging)

                            paging = tppsResult.data.paging

                            loadTpps()
                        } else {
                            showSnackbarMessage(R.string.loading_tpps_error)
                        }
                    }
                    if (paging.page == paging.totalPages) {
                        showSnackbarMessage(R.string.loading_finished)
                    }
                    _loadProgressEnd.value = Event(paging.last)
                    // TODO#MoveToToast: _statusLine.value = "Successfully loaded #" + paging.totalPages + "pages of TPPs."
                    _dataLoadingRemote.value = false
                }
            }
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
      */
    fun loadTpps() {
        _dataLoadingLocalDB.value = true
        viewModelScope.launch {
            val tppsResult = tppsRepository.loadTppsFromLocalDatasource(orderByField.value!!, orderByDirection.value!!)
            if (tppsResult is Success) {
                    _allItems.value = tppsResult.data
                    _displayedItems.value = applyAllTppFilters()
                } else {
                    //is Result.Idle -> TODO()
                    //is Result.Error -> TODO()
                    //is Result.Warn -> TODO()
                    //is Result.Loading -> TODO()
                }
           _dataLoadingLocalDB.value = false
        }
    }

    fun applyFilterByTitle(searchString: String) {
        _searchFilter.title = searchString

        wrapEspressoIdlingResource {

            viewModelScope.launch {
                val tppsToShow = applyAllTppFilters()
                _displayedItems.value = tppsToShow

                _dataLoadingLocalDB.value = false
            }
        }
    }

    fun applyAllTppFilters(): List<Tpp> {
        _dataLoadingLocalDB.value = true

        if (allItems.value.isNullOrEmpty()) {
            return listOf<Tpp>()
        }

        var tppsToShow = filterFollowedAndUsedOnly(allItems.value)

        tppsToShow = filterTppsByUserInterest(tppsToShow, _searchFilter)

        if (!_searchFilter.countries.isNullOrBlank() && !_searchFilter.countries.equals("EU")) {
            tppsToShow = filterTppsByCountry(tppsToShow, _searchFilter.countries)
        }

        if (!_searchFilter.services.isNullOrBlank() && !_searchFilter.services.equals("<All services>")) {
            tppsToShow = filterTppsByService(tppsToShow, _searchFilter.services)
        }

        tppsToShow = filterTppsByName(tppsToShow)

        if (orderByDirection.value ?: true) {
            tppsToShow.sortBy { it.getEntityName() }
        } else {
            tppsToShow.sortedByDescending { it.getEntityName() }
        }

        _dataLoadingLocalDB.value = false

        return tppsToShow
    }

    fun orderTppsBy(fieldName: String)/*: List<Tpp>*/ {
        _orderByField.value = fieldName
    }

    fun orderTpps() {
        val fieldName = _orderByField.value
        val asc = orderByDirection.value ?: true

        if (!_displayedItems.value.isNullOrEmpty()) {
            (_displayedItems.value as MutableList).sortBy {
                when (fieldName) {
                    "authorizationDate" -> it.ebaEntity.getAuthorizationDate()
                    "country" -> it.ebaEntity.getCountry()
                    "type" -> it.ebaEntity.getEntityType().code
                    "followed" -> it.isFollowed().toString()
                    else -> it.getEntityName()
                }
            }

            if (!asc) {
                (_displayedItems.value as MutableList).reverse()
            }
        }
    }

    fun reverseOrderBy() {
        if (!_displayedItems.value.isNullOrEmpty()) {
            _orderByDirection.value = !(_orderByDirection?.value ?: false)
            (_displayedItems.value as MutableList).reverse()
        }
    }

    private fun filterFollowedAndUsedOnly(inTpps: List<Tpp>?): List<Tpp>? {
        if (!(searchFilter.showFollowedOnly || searchFilter.showUsedOnly)) {
            return inTpps
        }

        val filteredTpps = ArrayList<Tpp>()
        inTpps?.forEach { aTpp ->
            if (searchFilter.showUsedOnly && aTpp.isUsed()) {
                filteredTpps.add(aTpp)
            } else {
                if (searchFilter.showFollowedOnly && (aTpp.isFollowed() || aTpp.isUsed())) {
                    filteredTpps.add(aTpp)
                }
            }
        }

        return filteredTpps
    }

    private fun filterTppsByName(inputTpps: List<Tpp>): MutableList<Tpp> {
        val filteredTpps = ArrayList<Tpp>()

        if (_searchFilter.title.isNullOrBlank()) {
            filteredTpps.addAll(inputTpps)
        } else {
            for (tpp in inputTpps) {
                if (tpp.getEntityName().contains(_searchFilter.title, true)) {
                    filteredTpps.add(tpp)
                }
            }
        }

        return filteredTpps
    }

    private fun filterTppsByUserInterest(inputTpps: List<Tpp>?, searchFilter: SearchFilter): MutableList<Tpp> {
        val filteredTpps = ArrayList<Tpp>()

        inputTpps?.forEach { tpp ->
            var addIt : Boolean = when (searchFilter.instType) {
                InstType.ALL -> tpp.isPSD2()
                InstType.INST_PI -> tpp.isPI()
                InstType.INST_AI -> tpp.isAI()
                InstType.INST_PIAI -> tpp.isPIAI()
                InstType.INST_EPI -> tpp.isEPI()
                InstType.INST_EMI -> tpp.isEMI()
                InstType.INST_EEMI -> tpp.isE_EMI()
                InstType.NON_PSD2_INST -> tpp.isNonPsd2Sp()
                InstType.CIs -> tpp.isASPSP()
                else -> true
            }

            if (addIt && !searchFilter.showBranches) {
                addIt = !tpp.ebaEntity.isBranch()
            }

            if (addIt && !searchFilter.showAgents) {
                addIt = !tpp.ebaEntity.isAgent()
            }

            if (addIt) {
                if (!searchFilter.showRevoked) {
                    addIt = !tpp.ebaEntity.isRevoked()
                }
            }
            if (addIt) {
                if (searchFilter.showRevokedOnly) {
                    addIt = tpp.ebaEntity.isRevoked()
                }
            }

            if (addIt) {
                filteredTpps.add(tpp)
            }
        }

        return filteredTpps
    }

    fun filterTppsByCountry(inputTpps: List<Tpp>?, country: String) : MutableList<Tpp> {
        if (inputTpps.isNullOrEmpty()) {
            return ArrayList<Tpp>()
        }

        var filteredTpps = ArrayList<Tpp>()
        if (country.isNullOrBlank() || country.equals("<All EU>")) {
            filteredTpps.addAll(inputTpps)
        } else {
            inputTpps.forEach {
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
        if (service.isNullOrBlank() || service.equals("ALL")) {
            filteredTpps.addAll(inputTpps)
        } else {
            val psdService = EbaService.findPsd2Service(service)
            inputTpps.forEach lit@{ tpp ->
                tpp.getEbaPassport().serviceMap.entries.forEach() {
                    if (it.key.equals(psdService.code)) {
                        filteredTpps.add(tpp)
                        return@lit;
                    }
                }
            }
        }

        return filteredTpps
    }

    fun filterTppsByCountry(country: String) {
        _searchFilter.countries = country

        _displayedItems.value = applyAllTppFilters()
    }

    fun filterFollowed(only: Boolean) {
        setFiltering(TppsFilterType.FOLLOWED)

        _displayedItems.value = applyAllTppFilters()
    }

    fun filterUsed(only: Boolean) {
        setFiltering(TppsFilterType.USED)

        _displayedItems.value = applyAllTppFilters()
    }

    fun filterRevoked(dofilter: Boolean) {
        setFiltering(TppsFilterType.REVOKED)

        _displayedItems.value = applyAllTppFilters()
    }

    fun filterRevokedOnly(only: Boolean) {
        setFiltering(TppsFilterType.REVOKED_ONLY)

        _displayedItems.value = applyAllTppFilters()
    }

    fun filterTppsByService(service: String) {
        _searchFilter.services = service

        _displayedItems.value = applyAllTppFilters()
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
        outState.putString("entityName", _searchFilter.title)
        outState.putBoolean("searchDescription", _searchFilter.searchDescription)
        outState.putString("countries", _searchFilter.countries)
        outState.putString("services", _searchFilter.services)
    }

    fun setupSearchFilter(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            _searchFilter.title = savedInstanceState.getString("", "") ?: ""
            _searchFilter.searchDescription = savedInstanceState.getBoolean("searchDescription", false)
            _searchFilter.countries = savedInstanceState.getString("countries", "") ?: ""
            _searchFilter.services = savedInstanceState.getString("services", "") ?: ""
        }
    }
}
