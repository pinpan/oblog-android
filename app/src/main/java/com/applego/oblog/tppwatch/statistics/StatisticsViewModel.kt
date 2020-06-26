package com.applego.oblog.tppwatch.statistics

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.EUCountry
import com.applego.oblog.tppwatch.data.model.EbaEntityType
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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

/*
    private val _perCountryTppsMap = MutableLiveData<Map<String, Int>>()
    val perCountryTppsMap: MutableLiveData<Map<String, Int>> = _perCountryTppsMap
*/

    private val _perServiceTppsMap = MutableLiveData<Map<String, Int>>()
    val perServiceTppsMap: MutableLiveData<Map<String, Int>> = _perServiceTppsMap

    private val _totalTpps = MutableLiveData<Int>()
    val totalTpps: LiveData<Int> = _totalTpps

    private val _totalAISPTpps = MutableLiveData<Int>()
    val totalAISPTpps: MutableLiveData<Int> = _totalAISPTpps

    private val _totalPISPTpps = MutableLiveData<Int>()
    val totalPISPTpps: MutableLiveData<Int> = _totalPISPTpps

    private val _totalEMITpps = MutableLiveData<Int>()
    val totalEMITpps: MutableLiveData<Int> = _totalEMITpps

    private val _usedTppsPercent = MutableLiveData<Float>()
    val usedTppsPercent: LiveData<Float> = _usedTppsPercent

    private val _followedTppsPercent = MutableLiveData<Float>()
    val followedTppsPercent: LiveData<Float> = _followedTppsPercent

    private val _countriesTppsSet = MutableLiveData<ArrayList<BarEntry>>()
    private val countriesTppsSet: LiveData<ArrayList<BarEntry>> = _countriesTppsSet

    private var usedTpps = 0

    private var followedTpps = 0

    init {
        start()
    }

    fun start() {
        if (_dataLoading.value == true) {
            return
        }
        _dataLoading.value = true

        wrapEspressoIdlingResource {
            runBlocking {
                tppsRepository.getAllTpps().let { result ->
                    if (result is Success) {
                        _error.value = false
                        computeStats(result.data)
                    } else {
                        _error.value = true
                        usedTpps = 0
                        followedTpps = 0
                        computeStats(null)
                    }
                }
            }
        }
    }

    fun refresh() {
        start()
    }

    /**
     * Called when new data is ready.
     */
    fun computeStats(tpps: List<Tpp>?) {
        _totalTpps.value = tpps?.size ?: 0
        calculateEntityTypeStatistics(tpps)

         getUsedAndFollowedStats(tpps).let {
            _usedTppsPercent.value = it.usedTppsPercent
            _followedTppsPercent.value = it.followedTppsPercent
        }
        _empty.value = tpps.isNullOrEmpty()
        _dataLoading.value = false
    }

    private fun calculateEntityTypeStatistics(tpps: List<Tpp>?) {
        val tppsPerCountryCount = HashMap<String, Int>()
        EUCountry.allEUCountries.forEach {
            var c: Int = 0
            tppsPerCountryCount.put(it.name, c)
        }

        var aispCounter = 0
        var pispCounter = 0
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

        tpps?.forEach {
            when (it.ebaEntity.entityType) {
                EbaEntityType.PSD_AISP -> aispCounter++
                EbaEntityType.PSD_PI -> pispCounter++
                EbaEntityType.PSD_EMI -> emiCounter++
            }
            val authStart = it.ebaEntity.ebaProperties.authorizationStart
            if (!authStart.isNullOrBlank()) {
                cal.time = sdf.parse(authStart)
                if (cal.time.after(anYearAgo) && cal.time.before(now)) {
                    anYearOldTpps++
                }
                if (cal.time.after(aMonthAgo) && cal.time.before(now)) {
                    aMonthOldTpps++
                }
                if (cal.time.after(aWeekAgo) && cal.time.before(now)) {
                    aWeekOldTpps++
                }
                if (cal.time.after(thisYearStart) && cal.time.before(now)) {
                    thisYearAuthorizedTpps++
                }
            }

            var counter: Int = tppsPerCountryCount.get(it.getCountry()) as Int
            tppsPerCountryCount.put(it.getCountry(), ++counter)
        }
        _lastWeekRegisteredTpps.value = aWeekOldTpps
        _lastMonthRegisteredTpps.value = aMonthOldTpps
        _lastYearRegisteredTpps.value = anYearOldTpps
        _thisYearAuthorizedTpps.value = thisYearAuthorizedTpps

        _totalAISPTpps.value = aispCounter
        _totalPISPTpps.value = pispCounter
        _totalEMITpps.value = emiCounter

        _countriesTppsSet.value = ArrayList<BarEntry>()
        var num = 0
        tppsPerCountryCount.forEach {
            val be = BarEntry(it.value.toFloat(), (num++).toFloat())
            countriesTppsSet.value?.add(be)
        }
    }

/*
    private fun countLastPeriodAdditions(tpps: List<Tpp>?, period: Int): Int? {
        val before = Calendar.getInstance()
        val after = Calendar.getInstance()
        after.add(period, -1)

        return countAdditionsForTimeInterval(tpps, before.time, after.time)
    }

    private fun countThisYearAdditions(tpps: List<Tpp>?): Int? {
        val before = Calendar.getInstance()
        val after = Calendar.getInstance()
        after.set(Calendar.MONTH, Calendar.JANUARY)
        after.set(Calendar.DAY_OF_MONTH, 1)

        return countAdditionsForTimeInterval(tpps, before.time, after.time)
    }

    private fun countAdditionsForTimeInterval(tpps: List<Tpp>?, before: Date, after: Date): Int? {
        var counter = 0
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        tpps?.forEach {
            val authStart = it.ebaEntity.ebaProperties.authorizationStart
            if (!authStart.isNullOrBlank()) {
                cal.time = sdf.parse(authStart)
                if (cal.time.after(after) && cal.time.before(before)) {
                    counter++
                }
            }
        }

        return counter
    }
*/

/*
    private fun countLastYearAdditions(tpps: List<Tpp>?): Int? {
        return countLastPeriodAdditions(tpps, Calendar.YEAR)
    }

    private fun countLastMonthAdditions(tpps: List<Tpp>?): Int? {
        return countLastPeriodAdditions(tpps, Calendar.MONTH)
    }

    private fun countLastWeekAdditions(tpps: List<Tpp>?): Int? {
        return countLastPeriodAdditions(tpps, Calendar.WEEK_OF_YEAR)
    }
*/

    fun getPerCountryDataSet(): BarDataSet {
        /*val valueSet1 = ArrayList<BarEntry>()

        val v1e1 = BarEntry(110.000f, 0f) // Jan
        valueSet1.add(v1e1)
        val v1e2 = BarEntry(40.000f, 1f) // Feb
        valueSet1.add(v1e2)
        val v1e3 = BarEntry(60.000f, 2f) // Mar
        valueSet1.add(v1e3)
        val v1e4 = BarEntry(30.000f, 3f) // Apr
        valueSet1.add(v1e4)
        val v1e5 = BarEntry(90.000f, 4f) // May
        valueSet1.add(v1e5)
        val v1e6 = BarEntry(100.000f, 5f) // Jun
        valueSet1.add(v1e6)*/
        /*val valueSet2 = ArrayList<BarEntry>()
        val v2e1 = BarEntry(150.000f, 0f) // Jan
        valueSet2.add(v2e1)
        val v2e2 = BarEntry(90.000f, 1f) // Feb
        valueSet2.add(v2e2)
        val v2e3 = BarEntry(120.000f, 2f) // Mar
        valueSet2.add(v2e3)
        val v2e4 = BarEntry(60.000f, 3f) // Apr
        valueSet2.add(v2e4)
        val v2e5 = BarEntry(20.000f, 4f) // May
        valueSet2.add(v2e5)
        val v2e6 = BarEntry(80.000f, 5f) // Jun
        valueSet2.add(v2e6)*/
        //val barDataSet2 = BarDataSet(valueSet2, "Brand 2")

        val barDataSet1 = BarDataSet(countriesTppsSet.value, "Brand 1")
        barDataSet1.color = Color.rgb(0, 155, 0)
        barDataSet1.setColors(*ColorTemplate.COLORFUL_COLORS)

        /*var dataSet = BarDataSet()
        dataSet.add(barDataSet1)
        dataSet.add(barDataSet2)*/

        return barDataSet1
    }

/*
    fun getXAxisValues(): ArrayList<String> {
        val valueSet1 = ArrayList<BarEntry>()
        val v1e1 = BarEntry(1.0f, 0f) // Jan

        val xAxis = ArrayList<String>()
        xAxis.add("AT")
        xAxis.add("BG")
        xAxis.add("CZ")
        xAxis.add("DE")
        xAxis.add("DK")
        xAxis.add("ES")
        xAxis.add("FR")
        xAxis.add("HR")
        xAxis.add("HU")
        xAxis.add("IT")
        xAxis.add("PL")
        xAxis.add("RO")
        xAxis.add("SE")
        xAxis.add("SK")
        xAxis.add("SL")
        xAxis.add("UK")
        return xAxis
    }
*/


/*
    private fun countPSD2Tpps(tpps: List<Tpp>?): Int? {
        var counter = 0
        tpps?.forEach {
            if (!it.ebaEntity.entityType.equals(EbaEntityType.NONE)
                    && !it.ebaEntity.entityType.equals(EbaEntityType.CREDIT_INSTITUTION)
                ) {
                counter++
            }
        }

        return counter
    }
*/
}
