package com.applego.oblog.tppwatch.addedittppapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applego.oblog.tppwatch.util.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.AddTtpappFragBinding
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

/**
 * Main UI for the add tpp screen. Users can enter a tpp entityName and description.
 */
class AddEditTppAppFragment : Fragment() {

    private lateinit var viewDataBinding: AddTtpappFragBinding

    private val args: AddEditTppAppFragmentArgs by navArgs()

    private val viewModel by viewModels<AddEditTppAppViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_ttpapp_frag, container, false)
        viewDataBinding = AddTtpappFragBinding.bind(root).apply {
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

        viewModel.start(args.tppId, args.appId)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.appUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            val action = AddEditTppAppFragmentDirections
                    .actionAddEditTppAppFragmentToTppDetailTabsFragment(args.tppId)
            findNavController().navigate(action)
        })
    }
}
