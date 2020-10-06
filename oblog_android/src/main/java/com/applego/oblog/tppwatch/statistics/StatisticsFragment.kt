package com.applego.oblog.tppwatch.statistics

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
import com.applego.oblog.tppwatch.databinding.StatisticsFragBinding
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
        chartTypesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                var chartType = ChartType.valueOf(context?.resources?.getStringArray(R.array.chart_type_values)!![pos]);
                if (chartType == null) {
                    chartType = ChartType.PerCountry
                }
                viewModel.setActualChartType(chartType)
                setUpChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        periodSpinner = activity?.findViewById(R.id.spinner_period)!!
        periodSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                var period = TimePeriod.getByOrdinalValue(pos);
                if (period == null) {
                    period = TimePeriod.SinceTheBigBang
                }
                viewModel.setCurrentPeriod(period)
                setUpChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        viewModel.updateModel()
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

        if (chart != null) {
            chart.setDrawValueAboveBar(true)

            chart.data = BarData( viewModel.getBarData() )
            chart.data.setValueTextSize(11f)

            var chartType = viewModel.currentChartType.value
            val desc = Description()
            desc.text = chartType?.desc
            chart.setDescription(desc)

            val xAxis: XAxis = chart.getXAxis()
            xAxis.position = XAxisPosition.BOTH_SIDED
            xAxis.axisMinimum = 0f
            xAxis.granularity = 1f
            xAxis.labelCount = if (ChartType.PerInstitutionType.equals(chartType)) allEntityTypes.size else allEUCountries.size
            xAxis.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? {
                    if (ChartType.PerInstitutionType.equals(chartType)) {
                        return if (value.toInt() < allEntityTypes.size)
                                   getEntityTypeShortCode(allEntityTypes[value.toInt()]?.code)
                               else "N/A"
                    } else if (ChartType.PerCountry.equals(chartType)) {
                        return if (value.toInt() < allEUCountries.size-1)
                                   allEUCountries[value.toInt() + 1].name
                               else "N/A"
                    }

                    return "N/A"
                }
            })
            //countryChart.xAxis.labelRotationAngle = 45f

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

    /*fun showPsd2Only() {
        if (viewModel.psd2Only?.value ?: false) {
            viewModel.setActualChartType()
        }
    }*/
}
