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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleExpandableListAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applego.oblog.tppwatch.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.TppdetailFragBinding
import com.applego.oblog.tppwatch.tpps.DELETE_RESULT_OK
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Main UI for the tpp detail screen.
 */
class TppDetailFragment : Fragment() {
    private lateinit var viewDataBinding: TppdetailFragBinding

    private val args: TppDetailFragmentArgs by navArgs()

    private lateinit var listAdapter: TppDetailAdapter

    private lateinit var expandableListAdapter: SimpleExpandableListAdapter

    private val viewModel by viewModels<TppDetailViewModel> { getViewModelFactory() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
        setupFab()
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()

        //this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TppDetailAdapter(viewModel, context!!, R.layout.tpp_passport)
            viewDataBinding.passportsList.adapter = listAdapter

            /* TODO:
            expandableListAdapter = SimpleExpandableListAdapter(
                    context!!
                    , viewModel.tpp.value?.ebaPassport?.serviceMaps
                    , R.layout.tpp_passport
                    , R.layout.tpp_passport
                    , R.layout.tpp_passport
                    ,
            )
            viewDataBinding.passportsExpandableList.setAdapter(expandableListAdapter)
            */
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupNavigation() {
        viewModel.deleteTppEvent.observe(this, EventObserver {
            val action = TppDetailFragmentDirections
                .actionTppDetailFragmentToTppsFragment(DELETE_RESULT_OK)
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

        val view = inflater.inflate(R.layout.tppdetail_frag, container, false)

        viewDataBinding = TppdetailFragBinding.bind(view).apply {
            viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.start(args.tppId)
        }

        setHasOptionsMenu(true)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteTpp()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tppdetail_fragment_menu, menu)
    }
}
