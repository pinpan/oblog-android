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
package com.example.android.architecture.blueprints.todoapp.addedittpp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.AddtppFragBinding
import com.example.android.architecture.blueprints.todoapp.tpps.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.util.getViewModelFactory
import com.example.android.architecture.blueprints.todoapp.util.setupRefreshLayout
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

/**
 * Main UI for the add tpp screen. Users can enter a tpp title and description.
 */
class AddEditTppFragment : Fragment() {

    private lateinit var viewDataBinding: AddtppFragBinding

    private val args: AddEditTppFragmentArgs by navArgs()

    private val viewModel by viewModels<AddEditTppViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.addtpp_frag, container, false)
        viewDataBinding = AddtppFragBinding.bind(root).apply {
            this.viewmodel = viewModel
        }
        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSnackbar()
        setupNavigation()
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
        viewModel.start(args.tppId)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.tppUpdatedEvent.observe(this, EventObserver {
            val action = AddEditTppFragmentDirections
                .actionAddEditTppFragmentToTppsFragment(ADD_EDIT_RESULT_OK)
            findNavController().navigate(action)
        })
    }
}
