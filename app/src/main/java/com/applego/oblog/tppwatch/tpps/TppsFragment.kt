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

package com.applego.oblog.tppwatch.tpps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applego.oblog.tppwatch.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.databinding.TppsFragBinding
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.applego.oblog.tppwatch.util.setupRefreshLayout
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber


/**
 * Display a grid of [Tpp]s. User can choose to view all, active or followed tpps.
 */
class TppsFragment : Fragment() {

    private val viewModel by viewModels<TppsViewModel> { getViewModelFactory() }

    private val args: TppsFragmentArgs by navArgs()

    private lateinit var viewDataBinding: TppsFragBinding

    private lateinit var listAdapter: TppsAdapter

    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TppsFragBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.load_directory -> {
                viewModel.loadEbaDirectory()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadTpps(false)
                true
            }
            else -> false
        }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tpps_fragment_menu, menu)

        val searchManager = getActivity()?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(getActivity()?.componentName))
        }

    }*/

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.tppsList)
        setupNavigation()
        setupTextSearch()
        setupFab()

        // Always reloading data for simplicity. Real apps should only do this on first load and
        // when navigating back to this destination. TODO: https://issuetracker.google.com/79672220
        //viewModel.loadTpps(true)
    }

    private fun setupTextSearch() {
        searchView = activity?.findViewById<SearchView>(R.id.textSearchView)
        if (searchView != null) {

        }

        searchView?.setIconifiedByDefault(true)
        searchView?.setQueryHint("Type text to filter listed TPPs")
        searchView?.setOnQueryTextFocusChangeListener { v, hasFocus ->
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        // perform set on query text listener event
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val theID = id
                searchBy(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // do something when text changes
                return false
            }
        })
    }

    fun searchBy(query: String) {
        viewModel.filterByTitle(query)
    }

    private fun setupNavigation() {
        viewModel.openTppEvent.observe(this, EventObserver {
            openTppDetails(it)
        })
        viewModel.newTppEvent.observe(this, EventObserver {
            navigateToAddNewTpp()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(com.applego.oblog.tppwatch.R.menu.filter_tpps, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TppsFilterType.ACTIVE_TPPS
                        R.id.followed -> TppsFilterType.FOLLOWED_TPPS
                        else -> TppsFilterType.ALL_TPPS
                    }
                )
                viewModel.loadTpps(false) // TODO: Use toggle on action bar - default to NO
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_tpp_fab)?.let {
            it.setOnClickListener {
                navigateToAddNewTpp()
            }
        }
    }

    private fun navigateToAddNewTpp() {
        val action = TppsFragmentDirections
            .actionTppsFragmentToAddEditTppFragment(
                null,
                resources.getString(R.string.add_tpp)
            )
        findNavController().navigate(action)
    }

    private fun openTppDetails(tppId: String) {
        val action = TppsFragmentDirections.actionTppsFragmentToTppDetailFragment(tppId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TppsAdapter(viewModel)
            viewDataBinding.tppsList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}
