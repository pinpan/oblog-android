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
import com.applego.oblog.tppwatch.data.model.InstType
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

    private var loadedTpps: List<Tpp> = emptyList()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    /**
     * Controls whether the stats are shown or a "No data" message.
     */
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
    private val barEntriesMap = MutableLiveData<Map<Pair<InstType, TimePeriod>, List<BarEntry>>>()

    private val _currentChartType = MutableLiveData<ChartType>(ChartType.PerCountry)
    val currentChartType: LiveData<ChartType> = _currentChartType

    private val _currentPeriod = MutableLiveData<TimePeriod>(TimePeriod.SinceTheBigBang)
    val currentPeriod : LiveData<TimePeriod> = _currentPeriod

    /*
        private val _perServiceTppsMap = MutableLiveData<Map<String, Int>>()
        val perServiceTppsMap: MutableLiveData<Map<String, Int>> = _perServiceTppsMap
    */

    fun refresh() {
        //updateModel()
        computeStatistics()
    }

    fun updateModel() {
        if (_dataLoading.value == true) {
            return
        }
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
                    computeStatistics()
                    _dataLoading.value = false
                }
            }
        }
    }

    /**
     * Called when new data is ready.
     */
    private fun computeStatistics() {
        _dataLoading.value = true

        var aispCounter = 0
        var pispCounter = 0
        var ciispCounter = 0
        var emiCounter = 0

        var anYearOldTpps = 0
        var aMonthOldTpps = 0
        var aWeekOldTpps = 0

        var thisYearAuthorizedTpps = 0

        var cal = Calendar.getInstance()
        val now = cal.time

        cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val thisYearStart = cal.time

        cal = Calendar.getInstance()
        cal.roll(Calendar.MONTH, -1)
        val aMonthAgo = cal.time

        cal = Calendar.getInstance()
        cal.roll(Calendar.WEEK_OF_YEAR, -1)
        val aWeekAgo = cal.time

        cal = Calendar.getInstance()
        cal.roll(Calendar.YEAR, -1)
        val anYearAgo = cal.time

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        _totalTpps.value = loadedTpps.size
        _empty.value = loadedTpps.isNullOrEmpty()

        val tppsPerCountryArray = Array(allEUCountries.size-1) { 0 }
        val tppsPerInstitutionTypeArray = Array(allEntityTypes.size) { 0 }

        loadedTpps.forEach {
            when (it.ebaEntity.entityType) {
                EbaEntityType.PSD_AISP -> aispCounter++
                EbaEntityType.PSD_PI -> pispCounter++
                EbaEntityType.PSD_EMI -> emiCounter++
            }
            if (!EbaEntityType.NONE.equals(it.ebaEntity.entityType)) {
                tppsPerInstitutionTypeArray[it.ebaEntity.entityType.order-1] = tppsPerInstitutionTypeArray[it.ebaEntity.entityType.order-1]+1
            }

            val euCountry = it.getCountry()
            val authStart = it.ebaEntity.ebaProperties.authorizationStart
            if (!authStart.isNullOrBlank()) {
                cal.time = sdf.parse(authStart)
                if (cal.time.after(anYearAgo) && cal.time.before(now)) {
                    anYearOldTpps++
                }

                if (currentPeriod != TimePeriod.SinceTheBigBang) {
                    if (cal.time.after(aMonthAgo) && cal.time.before(now)) {
                        aMonthOldTpps++
                    }
                }

                if (cal.time.after(aWeekAgo) && cal.time.before(now)) {
                    aWeekOldTpps++
                }

                if (cal.time.after(thisYearStart) && cal.time.before(now)) {
                    thisYearAuthorizedTpps++
                }
            }

            if (euCountry != null) {
                tppsPerCountryArray[EUCountry.valueOf(euCountry).order - 1] = tppsPerCountryArray[EUCountry.valueOf(euCountry).order - 1] + 1
            }
        }
        _lastWeekRegisteredTpps.value = aWeekOldTpps
        _lastMonthRegisteredTpps.value = aMonthOldTpps
        _lastYearRegisteredTpps.value = anYearOldTpps
        _thisYearAuthorizedTpps.value = thisYearAuthorizedTpps

        _totalAISPTpps.value = aispCounter
        _totalPISPTpps.value = pispCounter
        _totalCIISPTpps.value = ciispCounter
        _totalEMITpps.value = emiCounter

        addTppsPerCountryBarEntries(tppsPerCountryArray)
        addTppsPerInstitutionTypeBarEntries(tppsPerInstitutionTypeArray)

        _dataLoading.value = false
    }

    private fun addTppsPerInstitutionTypeBarEntries(tppsPerInstitutionTypeArray: Array<Int>) {
        var num11 = 0
        tppsPerInstitutionTypeSet.value = ArrayList<BarEntry>()
        allEntityTypes.forEach {
            val be1 = BarEntry((num11++).toFloat(), tppsPerInstitutionTypeArray[it.order - 1].toFloat())
            tppsPerInstitutionTypeSet.value?.add(be1)
        }
    }

    private fun addTppsPerCountryBarEntries(tppsPerCountryArray: Array<Int>): Int {
        var num1 = tppsPerCountryArray.size
        tppsPerCountrySet.value = ArrayList<BarEntry>()
        allEUCountries.forEach {
            if (it.order != tppsPerCountryArray.size) {
                tppsPerCountrySet.value?.add(BarEntry((--num1).toFloat(), tppsPerCountryArray[num1].toFloat()))
            }
        }
        return num1
    }

    fun getTppsPerCountryDataSet(): BarDataSet {
        val barDataSet1 = BarDataSet(tppsPerCountrySet.value, "TPPs per EU country")
        barDataSet1.setValueTypeface(Typeface.SANS_SERIF);
        barDataSet1.setValueTextSize(4f)
        barDataSet1.setColors(*ColorTemplate.COLORFUL_COLORS, *ColorTemplate.JOYFUL_COLORS, *ColorTemplate.PASTEL_COLORS, *ColorTemplate.LIBERTY_COLORS, *ColorTemplate.MATERIAL_COLORS)

        return barDataSet1
    }

    fun getTppsPerInstitutionTypeDataSet(): BarDataSet {
        val barDataSet1 = BarDataSet(tppsPerInstitutionTypeSet.value, "TPPs per institution type")
        barDataSet1.setValueTypeface(Typeface.SANS_SERIF);
        barDataSet1.setValueTextSize(4f)
        barDataSet1.setColors(*ColorTemplate.COLORFUL_COLORS, *ColorTemplate.JOYFUL_COLORS, *ColorTemplate.PASTEL_COLORS, *ColorTemplate.LIBERTY_COLORS, *ColorTemplate.MATERIAL_COLORS)

        return barDataSet1
    }

    fun setActualChartType(chartType: ChartType) {
        _currentChartType.value = chartType
        computeStatistics()
    }

    fun setCurrentPeriod(period: TimePeriod) {
        _currentPeriod.value = period
        computeStatistics()
    }

    fun getBarData(): IBarDataSet? {
        if (ChartType.PerInstitutionType.equals(currentChartType.value)) {
             return getTppsPerInstitutionTypeDataSet()
        }
        return getTppsPerCountryDataSet()
    }
}
