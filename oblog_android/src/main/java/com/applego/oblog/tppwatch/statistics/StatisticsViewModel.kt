package com.applego.oblog.tppwatch.statistics

import android.graphics.Typeface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.EUCountry
import com.applego.oblog.tppwatch.data.model.EUCountry.Companion.allEUCountries
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.EbaEntityType.Companion.allEntityTypes
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    var sinceTheBigBang: Date? = null
    var thisYear: Date? = null
    var previousYear: Date? = null
    var lastYear: Date? = null
    var lastSixMonths: Date? = null
    var lastQuarter: Date? = null
    var lastMonth: Date? = null
    var lastWeek: Date? = null

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    private var loadedTpps: List<Tpp> = emptyList()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean> = _empty

    private val _thisYearAuthorizedTpps = MutableLiveData<Int>()
    val thisYearAuthorizedTpps: LiveData<Int> = _thisYearAuthorizedTpps

    private val _lastYearRegisteredTpps = MutableLiveData<Int>()
    val lastYearRegisteredTpps: LiveData<Int> = _lastYearRegisteredTpps

    private val _lastMonthRegisteredTpps = MutableLiveData<Int>()
    val lastMonthRegisteredTpps: LiveData<Int> = _lastMonthRegisteredTpps

    private val _lastWeekRegisteredTpps = MutableLiveData<Int>()
    val lastWeekRegisteredTpps: LiveData<Int> = _lastWeekRegisteredTpps

    private val _totalTpps = MutableLiveData<Int>()
    val totalTpps: LiveData<Int> = _totalTpps

    private val _totalAISPTpps = MutableLiveData<Int>()
    val totalAISPTpps: MutableLiveData<Int> = _totalAISPTpps

    private val _totalPISPTpps = MutableLiveData<Int>()
    val totalPISPTpps: MutableLiveData<Int> = _totalPISPTpps

    private val _totalCIISPTpps = MutableLiveData<Int>()
    val totalCIISPTpps: MutableLiveData<Int> = _totalCIISPTpps

    private val _totalEMITpps = MutableLiveData<Int>()
    val totalEMITpps: MutableLiveData<Int> = _totalEMITpps

    private val tppsPerCountrySet = MutableLiveData<ArrayList<BarEntry>>()
    private val tppsPerInstitutionTypeSet = MutableLiveData<ArrayList<BarEntry>>()

    private val _currentChartType = MutableLiveData<ChartType>(ChartType.PerCountry)
    val currentChartType: LiveData<ChartType> = _currentChartType

    private val _currentPeriod = MutableLiveData<TimePeriod>(TimePeriod.SinceTheBigBang)
    val currentPeriod : LiveData<TimePeriod> = _currentPeriod

    private val _barEntriesMap = MutableLiveData<HashMap<Pair<ChartType, TimePeriod>, List<BarEntry>>>()
    val barEntriesMap: MutableLiveData<HashMap<Pair<ChartType, TimePeriod>, List<BarEntry>>> = _barEntriesMap

    init {
        var cal = Calendar.getInstance()

        // TimePeriod.SinceTheBigBang - just this millenium
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.YEAR, 2000)
        sinceTheBigBang = cal.time

        // TimePeriod.ThisYear
        cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        thisYear = cal.time

        // TimePeriod.PrevYear
        cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.roll(Calendar.YEAR, -1)
        previousYear = cal.time

        // TimePeriod.LastYear
        cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        lastYear = cal.time

        // TimePeriod.LastSixMonths
        cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -6)
        lastSixMonths = cal.time

        // TimePeriod.LastQuarter
        cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -3)
        lastQuarter = cal.time

        // TimePeriod.LastMonth
        cal = Calendar.getInstance()
        cal.roll(Calendar.MONTH, -1)
        lastMonth = cal.time

        // TimePeriod.LastWeek
        cal = Calendar.getInstance()
        cal.roll(Calendar.WEEK_OF_YEAR, -1)
        lastWeek = cal.time

        _barEntriesMap.value = HashMap<Pair<ChartType, TimePeriod>, List<BarEntry>>()
    }

    fun refresh() {
        computeStatistics(true)
    }

    fun updateModel() {
        if (_dataLoading.value == true) {
            return
        }

        // Loading data is implemented as blocking operation.
        //TODO: Change to create future and wait for it to complete before processing further
        loadDataFromDB()

        computeStatistics(true)
    }

    private fun loadDataFromDB() {
        _dataLoading.value = true
        wrapEspressoIdlingResource {
            runBlocking {
                tppsRepository.getAllTpps().let { result ->
                    if (result is Success) {
                        _error.value = false
                        loadedTpps = result.data
                    } else {
                        _error.value = true
                        loadedTpps = emptyList()
                    }
                    _dataLoading.value = false
                }
            }
        }
    }

    /**
     * Called when new data is ready.
     */
    private fun computeStatistics() {
        computeStatistics(false)
    }

    private fun computeStatistics(refresh: Boolean) {
        if (currentChartType.value == null || currentPeriod.value == null) {
            return
        }

        val key = Pair(currentChartType.value!!, currentPeriod.value!!)
        var barEntries: List<BarEntry>? = barEntriesMap.value?.get(key)
        if (!barEntries.isNullOrEmpty() && !refresh) {
            return
        }

        barEntries = ArrayList<BarEntry>()
        barEntriesMap.value?.put(key, barEntries)

        _totalAISPTpps.value = 0
        _totalPISPTpps.value = 0
        _totalCIISPTpps.value = 0
        _totalEMITpps.value = 0
        _lastYearRegisteredTpps.value = 0
        _lastMonthRegisteredTpps.value = 0
        _lastWeekRegisteredTpps.value = 0
        _thisYearAuthorizedTpps.value = 0

        var authStart = Calendar.getInstance()
        val now = authStart.time

        _totalTpps.value = loadedTpps.size
        _empty.value = loadedTpps.isNullOrEmpty()

        var tppsStatisticsArray: Array<Int>? = null
        when (currentChartType.value) {
            ChartType.PerCountry -> {
                tppsStatisticsArray = Array(allEUCountries.size-1) { 0 }
            }
            ChartType.PerInstitutionType -> {
                tppsStatisticsArray = Array(allEntityTypes.size) { 0 }
            }
        }

        _dataLoading.value = true
        loadedTpps.forEach {
            when (it.ebaEntity.entityType) {
                EbaEntityType.PSD_AISP -> _totalAISPTpps.value = _totalAISPTpps.value!! + 1
                EbaEntityType.PSD_PI -> _totalPISPTpps.value = _totalPISPTpps.value!! + 1
                EbaEntityType.PSD_EMI -> _totalEMITpps.value = _totalEMITpps.value!! + 1
            }

            if (!it.ebaEntity.ebaProperties.authorizationStart.isNullOrBlank()) {
                authStart.time = sdf.parse(it.ebaEntity.ebaProperties.authorizationStart)
                if (authStart.time.after(lastYear) && authStart.time.before(now)) {
                    _lastYearRegisteredTpps.value = _lastYearRegisteredTpps.value!! + 1
                }

                if (currentPeriod != TimePeriod.SinceTheBigBang) {
                    if (authStart.time.after(lastMonth) && authStart.time.before(now)) {
                        _lastMonthRegisteredTpps.value = _lastMonthRegisteredTpps.value!! + 1
                    }
                }

                if (authStart.time.after(lastWeek) && authStart.time.before(now)) {
                    _lastWeekRegisteredTpps.value = _lastWeekRegisteredTpps.value!! + 1
                }

                if (authStart.time.after(thisYear) && authStart.time.before(now)) {
                    _thisYearAuthorizedTpps.value = _thisYearAuthorizedTpps.value!! + 1
                }

                if (isAuthorizationTimeWithinCurrentTimePeriod(authStart.time)) {
                    when (currentChartType.value) {
                        ChartType.PerCountry -> {
                            val euCountry = it.getCountry()
                            if (euCountry != null) {
                                tppsStatisticsArray!![EUCountry.valueOf(euCountry).order - 1] = tppsStatisticsArray[EUCountry.valueOf(euCountry).order - 1] + 1
                            }
                        }

                        ChartType.PerInstitutionType -> {
                            val entityType = it.ebaEntity.entityType
                            if (!EbaEntityType.NONE.equals(entityType)) {
                                tppsStatisticsArray!![entityType.order - 1] = tppsStatisticsArray[entityType.order - 1] + 1
                            }
                        }
                    }
                }
            }
        }

        addBarEntries(barEntries, tppsStatisticsArray)

        _dataLoading.value = false
    }

    private fun isAuthorizationTimeWithinCurrentTimePeriod(authStart: Date) : Boolean {
        val now = Calendar.getInstance().time
        return authStart.before(now) && (when (currentPeriod.value) {
            TimePeriod.SinceTheBigBang -> true
            TimePeriod.ThisYear -> (authStart.after(thisYear))
            TimePeriod.PrevYear -> (authStart.after(previousYear))
            TimePeriod.LastYear -> (authStart.after(lastYear))
            TimePeriod.LastSixMOnths -> (authStart.after(lastSixMonths))
            TimePeriod.LastQuarter -> (authStart.after(lastQuarter))
            TimePeriod.LastMonth -> (authStart.after(lastMonth))
            TimePeriod.LastWeek -> (authStart.after(lastWeek))
            else -> false
        })
    }

    private fun addBarEntries(barEntries: ArrayList<BarEntry>, tppsCountsArray: Array<Int>?) {
        if (!tppsCountsArray.isNullOrEmpty()) {
            //tppsPerInstitutionTypeSet.value = ArrayList<BarEntry>()
            for (n in 0..tppsCountsArray.size-1) {
                val be1 = BarEntry(n.toFloat(), tppsCountsArray[n].toFloat())
                barEntries.add(be1)
            }
        }
    }

    fun setCurrentChartType(chartType: ChartType) {
        _currentChartType.value = chartType
        computeStatistics()
    }

    fun setCurrentPeriod(period: TimePeriod) {
        _currentPeriod.value = period
        computeStatistics()
    }

    fun getBarData(): IBarDataSet? {
        val key = Pair(currentChartType.value!!, currentPeriod.value!!)
        var barEntries: List<BarEntry>? = barEntriesMap.value?.get(key)
        if (barEntries.isNullOrEmpty()) {
            computeStatistics()
            barEntries = barEntriesMap.value?.get(key)
        }

        if (barEntries == null) {
            throw RuntimeException("Can't get bar entries from the map. Check the algorithm")
        }

        val barDataSet1 = BarDataSet(barEntries, "TPPs per institution type")
        barDataSet1.setValueTypeface(Typeface.SANS_SERIF);
        barDataSet1.setValueTextSize(4f)
        barDataSet1.setColors(*ColorTemplate.COLORFUL_COLORS, *ColorTemplate.JOYFUL_COLORS, *ColorTemplate.PASTEL_COLORS, *ColorTemplate.LIBERTY_COLORS, *ColorTemplate.MATERIAL_COLORS)

        return barDataSet1
    }
}
