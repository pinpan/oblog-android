package com.applego.oblog.tppwatch.tpps

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.applego.oblog.tppwatch.util.Event
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.*
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.data.source.local.*
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import java.util.*

/**
 * ViewModel for the tpp list screen.
 */
class TppsViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    private val _allItems: MutableLiveData<List<Tpp>> = MutableLiveData(listOf<Tpp>())
    val allItems: LiveData<List<Tpp>> = _allItems

    // TODO: rename to displayedItems
    private val _displayedItems = MutableLiveData<List<Tpp>>().apply { value = allItems.value }
    val displayedItems: LiveData<List<Tpp>> = _displayedItems

    private val _statusLine = MutableLiveData<String>("")
    val statusLine: LiveData<String> = _statusLine

    private val _dataLoadingLocalDB = MutableLiveData<Boolean>()
    val dataLoadingLocalDB: LiveData<Boolean> = _dataLoadingLocalDB

    private val _dataLoadingRemoteEBA = MutableLiveData<Boolean>()
    val dataLoadingRemoteEBA: LiveData<Boolean> = _dataLoadingRemoteEBA

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

    private val _openTppEvent = MutableLiveData<Event<String>>()
    val openTppEvent: LiveData<Event<String>> = _openTppEvent

    private val _newTppEvent = MutableLiveData<Event<Unit>>()
    val newTppEvent: LiveData<Event<Unit>> = _newTppEvent

    private val _aboutEvent = MutableLiveData<Event<Unit>>()
    val aboutEvent: LiveData<Event<Unit>> = _aboutEvent

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_displayedItems) {
        it.isEmpty()
    }

    val dataLoading = MediatorLiveData<Boolean>();

    init {
        searchFilter.init()

        setFiltering(TppsFilterType.ALL_INST)

        dataLoading.addSource(_dataLoadingLocalDB, {value -> dataLoading.setValue(value)});
        dataLoading.addSource(dataLoadingRemoteEBA, {value -> dataLoading.setValue(value)});
    }

    fun refresh() {
        loadTpps(false)
    }

    fun isFiltered() : Boolean {
        return (_allItems.value?.size != _displayedItems.value?.size)
    }

    fun refreshTpp(tppId : String?) {
        if (!allItems.value.isNullOrEmpty() && !tppId.isNullOrBlank() && !tppId.equals("0")) {
            val tpp = findTppInList(allItems.value!!, tppId)
            runBlocking {
                if (tpp != null) {
                    tppsRepository.refreshTpp(tpp)
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
     * @param requestType Can be [TppsFilterType.FOLLOWED], or [TppsFilterType.USED]
     */
    fun setFiltering(requestType: TppsFilterType) {
        _searchFilter.updateUserSelection(requestType)

        when (requestType) {
            TppsFilterType.ALL_INST -> {
                setFilterStatusViews(
                    R.string.label_all, R.string.no_tpps_all,
                    R.drawable.oblog_logo, true
                )
            }
            TppsFilterType.PI_INST -> {
                setFilterStatusViews(
                        R.string.label_psd2_only, R.string.inst_type_psd2_pis,
                        R.drawable.oblog_logo, true
                )
            }
            TppsFilterType.AI_INST -> {
                setFilterStatusViews(
                        R.string.label_psd2_only, R.string.inst_type_psd2_ais,
                        R.drawable.oblog_logo, true
                )
            }
            TppsFilterType.PIAI_INST -> {
                setFilterStatusViews(
                        R.string.label_psd2_only, R.string.inst_type_psd2_piai,
                        R.drawable.oblog_logo, true
                )
            }
            TppsFilterType.E_PI_INST -> {
                setFilterStatusViews(
                        R.string.label_psd2_only, R.string.inst_type_psd2_epis,
                        R.drawable.oblog_logo, true
                )
            }

            TppsFilterType.EMONEY_INST -> {
                setFilterStatusViews( // TODO: Define label for epi inst
                        R.string.label_psd2_only, R.string.inst_type_emi,
                        R.drawable.oblog_logo, true
                )
            }
            TppsFilterType.E_EMONEY_INST -> {
                setFilterStatusViews(
                        R.string.label_psd2_only, R.string.inst_type_eemi,
                        R.drawable.oblog_logo, true
                )
            }

            TppsFilterType.NON_PSD2_INST -> {
                setFilterStatusViews( // TODO: Define label for e_emoney inst
                        R.string.label_psd2_only, R.string.inst_type_non_psd2_tpps,
                        R.drawable.oblog_logo, true
                )
            }

            TppsFilterType.BRANCHES -> {
                setFilterStatusViews(
                        R.string.show_branches, R.string.no_data,
                        R.drawable.oblog_logo, true
                )
            }
            TppsFilterType.AGENTS -> {
                setFilterStatusViews(
                        R.string.show_agents, R.string.no_data,
                        R.drawable.oblog_logo, true
                )
            }

            TppsFilterType.REVOKED -> {
                setFilterStatusViews(
                        R.string.label_revoked, R.string.no_revoked_tpps,
                        R.drawable.oblog_logo, false
                )
            }
            TppsFilterType.REVOKED_ONLY -> {
                setFilterStatusViews(
                        R.string.label_revoked_only, R.string.no_revoked_tpps,
                        R.drawable.oblog_logo, false
                )
            }
            TppsFilterType.USED -> {
                setFilterStatusViews(
                        R.string.label_used, R.string.no_tpps_used,
                        R.drawable.oblog_logo, true
                )
            }
            TppsFilterType.FOLLOWED -> {
                setFilterStatusViews(
                        R.string.label_followed, R.string.no_tpps_followed,
                        R.drawable.oblog_logo, true
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
        // Load from OBLOG API
        loadTpps(true)
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
        if (forceUpdate) {
            if (!(_dataLoadingRemoteEBA.value?.equals(true) ?: false)) {
                _dataLoadingRemoteEBA.value = true
                _displayedItems.value = emptyList()

                showSnackbarMessage(R.string.loading)
                wrapEspressoIdlingResource {
                    viewModelScope.launch {
                        val tppsResult = tppsRepository.fetchTppsFromRemoteDatasourcePaging()
                        if (tppsResult is Success) {
                            _allItems.value = tppsResult.data
                            _displayedItems.value = getTppsByGlobalFilter()

                            // TODO: Get it from fetched EBA / OBLOG data
                            _statusLine.value = "Last EBA version: " + Random().nextLong()
                        } else {
                            // TODO: Set warning message than "Data is old" to be displayed,
                            //  until refresh succeeds next time. May be for Remote updates only?
                            _displayedItems.value = getTppsByGlobalFilter()
                            showSnackbarMessage(R.string.loading_tpps_error)
                        }
                        _dataLoadingRemoteEBA.value = false
                    }
                }
            }
        } else {
            _dataLoadingLocalDB.value = true
            viewModelScope.launch {
                val tppsResult = tppsRepository.loadTppsFromLocalDatasource()
                if (tppsResult is Success) {
                        _allItems.value = tppsResult.data
                        _displayedItems.value = getTppsByGlobalFilter()
                    } else {
                        //is Result.Idle -> TODO()
                        //is Result.Error -> TODO()
                        //is Result.Warn -> TODO()
                        //is Result.Loading -> TODO()
                    }
                _dataLoadingLocalDB.value = false
            }
        }
    }

    fun applyFilterByTitle(searchString: String) {
        _searchFilter.title = searchString

        wrapEspressoIdlingResource {

            viewModelScope.launch {
                val tppsToShow = getTppsByGlobalFilter()
                _displayedItems.value = tppsToShow

                _dataLoadingLocalDB.value = false
            }
        }
    }

    fun getTppsByGlobalFilter(): List<Tpp> {
        _dataLoadingLocalDB.value = true

        if (allItems.value.isNullOrEmpty()) {
            return listOf<Tpp>()
        }

        var tppsToShow = filterTppsByUserInterest(allItems.value, _searchFilter)

        if (!_searchFilter.countries.isNullOrBlank() && !_searchFilter.countries.equals("<All EU countries>")) {
            tppsToShow = filterTppsByCountry(tppsToShow, _searchFilter.countries)
        }

        if (!_searchFilter.services.isNullOrBlank() && !_searchFilter.services.equals("<All services>")) {
            tppsToShow = filterTppsByService(tppsToShow, _searchFilter.services)
        }

        tppsToShow = filterTppsByName(tppsToShow)

        _dataLoadingLocalDB.value = false

        return tppsToShow
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

    private fun filterTppsByUserInterest(inputTpps : List<Tpp>?, searchFilter: SearchFilter): MutableList<Tpp> {
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

            if (addIt) {
                if (!searchFilter.showBranches) {
                    addIt = !tpp.ebaEntity.isBranch()
                }
                if (!searchFilter.showAgents) {
                    addIt = !tpp.ebaEntity.isAgent()
                }

                if (!searchFilter.showRevoked) {
                    addIt = !tpp.ebaEntity.isRevoked()
                }

                if (searchFilter.showRevokedOnly) {
                    addIt = tpp.ebaEntity.isRevoked()
                }
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

        return filteredTpps
    }

    fun filterTppsByCountry(inputTpps : List<Tpp>?, country: String) : MutableList<Tpp> {
        if (inputTpps.isNullOrEmpty()) {
            return ArrayList<Tpp>()
        }

        var filteredTpps = ArrayList<Tpp>()
        if (country.isNullOrBlank() || country.equals("<All EU countries>")) {
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
        if (service.isNullOrBlank() || service.equals("<All services>")) {
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

        _displayedItems.value = getTppsByGlobalFilter()
    }

    fun filterTppsByService(service: String) {
        _searchFilter.services = service

        _displayedItems.value = getTppsByGlobalFilter()
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
