package com.applego.oblog.tppwatch.statistics

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.EUCountry.Companion.allEUCountries
import com.applego.oblog.tppwatch.data.model.EbaEntityType.Companion.allEntityTypes
import com.applego.oblog.tppwatch.data.model.EbaService.Companion.allEbaServies
import com.applego.oblog.tppwatch.databinding.StatisticsFragBinding
import com.applego.oblog.tppwatch.util.TimePeriod
import com.applego.oblog.ui.TextSpinnerAdapter
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.ValueFormatter


/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment() {
// TODO:
//  1. Consider using https://developers.google.com/chart/interactive/docs/customizing_tooltip_content
//  2. Consider https://github.com/moagrius/TileView or https://github.com/peterLaurence/MapView for ZOOM and PAN of chart details

    private lateinit var viewDataBinding: StatisticsFragBinding

    private val viewModel by viewModels<StatisticsViewModel> { getViewModelFactory() }

    private lateinit var chart: BarChart

    private  var toolbarIcon: Drawable? = null

    private lateinit var chartTypesSpinner: Spinner

    private lateinit var periodSpinner: Spinner

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(
                inflater, R.layout.statistics_frag, container,
                false
        )
        chart = viewDataBinding.chart

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel = viewModel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        val toolbar: Toolbar ?= activity?.findViewById(R.id.toolbar)
        toolbar?.setNavigationIcon(toolbarIcon)

        chartTypesSpinner = activity?.findViewById(R.id.spinner_charttype)!!
        val charTypeTitles = context!!.resources.getTextArray(R.array.chart_type_titles)
        val chartTypesAdapter = TextSpinnerAdapter(getActivity() as Context, R.layout.custom_spinner, chartTypesSpinner, getStringList(charTypeTitles), 12)
        chartTypesSpinner.setAdapter(chartTypesAdapter);
        chartTypesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val chartType = ChartType.valueOf(context?.resources?.getStringArray(R.array.chart_type_values)!![pos]);
                viewModel.setCurrentChartType(chartType)
                setUpChart(chartType, viewModel.currentPeriod.value)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        periodSpinner = activity?.findViewById(R.id.spinner_period)!!
        val timePeriods = context!!.resources.getTextArray(R.array.time_intervals)
        val timePeriodsAdapter = TextSpinnerAdapter(getActivity() as Context, R.layout.custom_spinner, periodSpinner, getStringList(timePeriods), 12)
        periodSpinner.setAdapter(timePeriodsAdapter);
        periodSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val timePeriod = TimePeriod.getByOrdinalValue(pos);
                viewModel.setCurrentPeriod(timePeriod)
                setUpChart(viewModel.currentChartType.value, timePeriod)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        viewModel.updateModel()
    }

    private fun getStringList(arrayItems: Array<CharSequence>): List<String> {
        val result = ArrayList<String>()
        arrayItems.forEach {
            result.add(it.toString())
        }
        return result
    }

    override fun onResume() {
        super.onResume()

        setUpChart()

        val toolbar: Toolbar?= activity?.findViewById(com.applego.oblog.tppwatch.R.id.toolbar)
        val d: Drawable?= ResourcesCompat.getDrawable(resources, R.drawable.oblog_logo_48x52, null)
        toolbar?.setNavigationIcon(d)
    }

    private fun setUpChart() {
        setUpChart(ChartType.PerCountry, viewModel.currentPeriod.value)
    }

    private fun setUpChart(ct: ChartType?, per: TimePeriod?) {
        var chartType = ct
        if (chartType == null) {
            chartType = ChartType.PerCountry
        }
        viewModel.setCurrentChartType(chartType)

        var period = per
        if (period == null) {
            period = TimePeriod.SinceTheBigBang
        }
        viewModel.setCurrentPeriod(period)

        if (chart != null) {
            chart.setDrawValueAboveBar(true)

            chart.data = BarData( viewModel.getBarData() )
            chart.data.setValueTextSize(11f)

            val desc = Description()
            desc.text = chartType.desc
            chart.setDescription(desc)

            val xAxis: XAxis = chart.getXAxis()
            xAxis.position = XAxisPosition.BOTH_SIDED
            xAxis.axisMinimum = 0f
            xAxis.granularity = 1f
            xAxis.labelCount = when (chartType) {
                ChartType.PerCountry -> allEUCountries.size-1
                ChartType.PerInstitutionType -> allEbaServies.size
            }
            xAxis.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? {
                    val intValue = value.toInt()
                    return when (chartType) {
                        ChartType.PerCountry -> allEUCountries[intValue].name
                        ChartType.PerInstitutionType -> {
                            if (value.toInt() < allEntityTypes.size) {
                                getEntityTypeShortCode(allEntityTypes[value.toInt()]?.code)
                            } else {
                                "N/A"
                            }
                        }
                    }
                }
            })

            val l: Legend = chart.getLegend()
            l.setEnabled(false)

            chart.animateXY(1000, 1000)
            chart.invalidate()
        }
    }

    private fun getEntityTypeShortCode(code: String): String {
        return if (code.startsWith("PSD_")) {
            code.substring(4)
        } else {
            code
        }
    }
}
