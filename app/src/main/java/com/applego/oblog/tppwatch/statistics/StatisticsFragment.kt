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
package com.applego.oblog.tppwatch.statistics


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.EUCountry.Companion.allEUCountries
import com.applego.oblog.tppwatch.data.model.EbaService.Companion.allEbaServies
import com.applego.oblog.tppwatch.data.model.EbaService.Companion.allEbaServiesMap
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

        val toolbar: Toolbar ?= activity?.findViewById(com.applego.oblog.tppwatch.R.id.toolbar)
        toolbar?.setNavigationIcon(toolbarIcon)

        val chartTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.chart_type_values, R.layout.spinner_item)
        chartTypesSpinner = activity?.findViewById(R.id.spinner_charttype)!!
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
        setUpChart(ChartType.Country)
    }

    private fun setUpChart(ct: ChartType) {
        var chartType = ct
        if (chartType == null) {
            chartType = ChartType.Country
        }

        if (chart != null) {
            chart.setDrawValueAboveBar(true)

            chart.data = BarData(when (chartType) {
                ChartType.Default,
                ChartType.Country -> viewModel.getPerCountryDataSet()
                ChartType.Service -> viewModel.getPerServiceDataSet()
                ChartType.DifferencialPerCountry -> viewModel.getDifferentialPerCountryDataSet()
                ChartType.DifferencialPerService -> viewModel.getDifferentialPerServiceDataSet()
            })

            val desc = Description()
            desc.text = "Tpps per country"
            chart.setDescription(desc)
            //countryChart.xAxis.labelRotationAngle = 45f

            val xAxis: XAxis = chart.getXAxis()
            xAxis.position = XAxisPosition.BOTH_SIDED
            xAxis.axisMinimum = 0f
            xAxis.granularity = 1f
            xAxis.labelCount = allEUCountries.size
            xAxis.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? {
                    return when (chartType) {
                        ChartType.Default,
                        ChartType.DifferencialPerCountry,
                        ChartType.Country -> if (value.toInt() < allEUCountries.size) allEUCountries[value.toInt()].name else allEUCountries[value.toInt()].country
                        ChartType.DifferencialPerService,
                        ChartType.Service -> if (value.toInt() < allEbaServies.size) allEbaServies[value.toInt()]?.psd2Code else allEbaServies[value.toInt()].name
                    }
                }
            })

            val l: Legend = chart.getLegend()
            l.setEnabled(false)

            chart.animateXY(1000, 1000)
            chart.invalidate()
        }
    }
}
