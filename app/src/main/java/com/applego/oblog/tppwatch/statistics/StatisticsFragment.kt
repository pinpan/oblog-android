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


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.StatisticsFragBinding
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import timber.log.Timber


/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment() {

    private lateinit var viewDataBinding: StatisticsFragBinding

    private val viewModel by viewModels<StatisticsViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(
            inflater, R.layout.statistics_frag, container,
            false
        )

        val chart: BarChart? = viewDataBinding.chart
        if (chart != null) {
            Timber.d("chart= " + chart)

            val data = BarData(getDataSet())
            chart.data = data
            val desc = Description()
            desc.text = "My Chart"
            chart.setDescription(desc)
            chart.animateXY(2000, 2000)
            chart.invalidate()
        }

        return viewDataBinding.root
    }

    private fun getDataSet(): BarDataSet {

        val valueSet1 = ArrayList<BarEntry>()
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
        valueSet1.add(v1e6)
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
        val barDataSet1 = BarDataSet(valueSet1, "Brand 1")
        barDataSet1.color = Color.rgb(0, 155, 0)
        //val barDataSet2 = BarDataSet(valueSet2, "Brand 2")
        //barDataSet2.setColors(*ColorTemplate.COLORFUL_COLORS)

        /*var dataSet = BarDataSet()
        dataSet.add(barDataSet1)
        dataSet.add(barDataSet2)*/

        return barDataSet1
    }

    fun getXAxisValues(): ArrayList<String> {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel = viewModel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        //this.setupRefreshLayout(viewDataBinding.refreshLayout)
        viewModel.start()
    }
}
