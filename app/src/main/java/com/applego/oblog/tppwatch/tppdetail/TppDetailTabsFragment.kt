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
package com.applego.oblog.tppwatch.tppdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.applego.oblog.tppwatch.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.addedittpp.AddEditTppFragmentDirections
import com.applego.oblog.tppwatch.databinding.TppDetailTabsFragmentBinding
import com.applego.oblog.tppwatch.tpps.ADD_EDIT_RESULT_OK
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main UI for the tpp detail screen.
 */
class TppDetailTabsFragment : Fragment() {

    private lateinit var tppDetailTabsAdapter: TppDetailTabsAdapter
    private lateinit var viewPager: ViewPager

    private lateinit var viewDataBinding: TppDetailTabsFragmentBinding

    private val  args: TppDetailTabsFragmentArgs by navArgs()

    private val viewModel by viewModels<TppDetailViewModel> { getViewModelFactory() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner // ???
        setupFab()

        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()

        //this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

    private fun setupNavigation() {
        viewModel.tppUpdatedEvent.observe(this, EventObserver {
            val action = TppDetailFragmentDirections
                .actionTppDetailFragmentToTppsFragment()
            findNavController().navigate(action)
        })

        viewModel.editTppEvent.observe(this, EventObserver {
            val action = TppDetailFragmentDirections
                .actionTppDetailFragmentToAddEditTppFragment(
                    args.tppId,
                    resources.getString(R.string.edit_tpp)
                )
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        activity?.findViewById<View>(R.id.edit_tpp_fab)?.setOnClickListener {
            viewModel.editTpp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.tpp_detail_tabs_fragment, container, false)

        tppDetailTabsAdapter = TppDetailTabsAdapter(viewModel, childFragmentManager)
        viewPager = view.findViewById(R.id.detail_tabs_pager)
        viewPager.adapter = tppDetailTabsAdapter

        val tabLayout = view.findViewById(R.id.detail_tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        viewDataBinding = TppDetailTabsFragmentBinding.bind(view).apply {
            viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.start(args.tppId)
        }

        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tppdetail_fragment_menu, menu)
    }
}
