package com.applego.oblog.tppwatch.statistics

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
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
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*


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

        val aChartTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.chart_type_titles, R.layout.spinner_chart_types)

        val strings = context!!.resources.getTextArray(R.array.chart_type_titles)
        val chartTypeAdapter/*:ArrayAdapter<String> */= /*object: ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                R.array.chart_type_titles
        )*/ //object: ArrayAdapter<CharSequence>/*.createFromResource*/(getActivity(), R.array.chart_type_titles, R.layout.spinner_item) {
            /*val theChartTypeAdapter = */
            object: ArrayAdapter<CharSequence>(context, R.layout.spinner_chart_types, 0, strings) {
            override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                        position,
                        convertView,
                        parent
                ) as TextView
                // set item text size
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP,12F)

                // set selected item style
                if (position == chartTypesSpinner.selectedItemPosition){
                    view.background = ColorDrawable(Color.parseColor("#FFF600"))
                    view.setTextColor(Color.parseColor("#2E2D88"))
                }

                return view
            }
        }

        chartTypeAdapter.setDropDownViewResource(R.layout.spinner_chart_types)
        chartTypesSpinner.setAdapter(chartTypeAdapter);
        chartTypesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val chartType = ChartType.valueOf(context?.resources?.getStringArray(R.array.chart_type_values)!![pos]);
                setUpChart(chartType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        viewModel.start()
    }

    override fun onResume() {
        super.onResume()

        setUpChart()

        val toolbar: Toolbar?= activity?.findViewById(com.applego.oblog.tppwatch.R.id.toolbar)
        /*toolbar?.post { */
        val d: Drawable?= ResourcesCompat.getDrawable(resources, R.drawable.oblog_logo_48x52, null)
        toolbar?.setNavigationIcon(d)
        /*}*/
    }

    private fun setUpChart() {
        setUpChart(ChartType.PerCountry)
    }

    private fun setUpChart(ct: ChartType) {
        var chartType = ct
        if (chartType == null) {
            chartType = ChartType.PerCountry
        }

        viewModel.setActualChartType(chartType)

        if (chart != null) {
            chart.setDrawValueAboveBar(true)

            chart.data = BarData(when (chartType) {
                ChartType.PerCountry -> viewModel.getTppsPerCountryDataSet()
                ChartType.PerInstitutionType -> viewModel.getTppsPerInstitutionTypeDataSet()
                ChartType.PerCountryChange -> viewModel.getTppsPerCountryChangeDataSet()
                ChartType.PerInstitutionTypeChange -> viewModel.getTppsPerInstitutionTypeChangeDataSet()
            })
            chart.data.setValueTextSize(10f)

            val desc = Description()
            desc.text = chartType.desc
            chart.setDescription(desc)

            val xAxis: XAxis = chart.getXAxis()
            xAxis.position = XAxisPosition.BOTH_SIDED
            xAxis.axisMinimum = 0f
            xAxis.granularity = 1f
            xAxis.labelCount = when (chartType) {
                ChartType.PerCountryChange,
                ChartType.PerCountry -> allEUCountries.size
                ChartType.PerInstitutionTypeChange,
                ChartType.PerInstitutionType -> allEbaServies.size
            }
            xAxis.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? {
                    return when (chartType) {
                        ChartType.PerCountryChange,
                        ChartType.PerCountry -> if (value.toInt() < allEUCountries.size) allEUCountries[value.toInt()].name else "N/A"
                        ChartType.PerInstitutionTypeChange,
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
