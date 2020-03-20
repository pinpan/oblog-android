package com.applego.oblog.tppwatch.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.applego.oblog.tppwatch.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.AboutFragBinding
import com.applego.oblog.tppwatch.tpps.ADD_EDIT_RESULT_OK
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

/**
 * Main UI for the add tpp screen. Users can enter a tpp entityName and description.
 */
class AboutFragment : Fragment() {

    private lateinit var viewDataBinding: AboutFragBinding

    private val viewModel by viewModels<AboutViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.about_frag, container, false)
        viewDataBinding = AboutFragBinding.bind(root).apply {
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
        //this.setupRefreshLayout(viewDataBinding.refreshLayout)
        viewModel.start()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.tppUpdatedEvent.observe(this, EventObserver {
            val action = AboutFragmentDirections
                .actionAboutFragmentToTppsFragment(ADD_EDIT_RESULT_OK)
            findNavController().navigate(action)
        })
    }
}
