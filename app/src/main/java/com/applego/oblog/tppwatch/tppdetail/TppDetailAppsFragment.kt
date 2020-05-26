package com.applego.oblog.tppwatch.tppdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.applego.oblog.tppwatch.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.addedittppapp.AddEditTppAppFragmentDirections
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.databinding.TppdetailAppsFragBinding
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Main UI for the tpp detail screen.
 */
class TppDetailAppsFragment(private val viewModel: AppsViewModel) : Fragment() {
    private lateinit var viewDataBinding: TppdetailAppsFragBinding

    private lateinit var listAdapter: AppsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
        setupAddFab()
        setupEditFab()
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()

        //this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = AppsAdapter(viewModel, context!!, R.layout.app_item)
            viewDataBinding.appsList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupNavigation() {
         viewModel.editAppEvent.observe(this.parentFragment!!, EventObserver {
            val action = TppDetailTabsFragmentDirections
                .actionTppDetailAppsFragmentToAddEditTppAppFragment(
                        viewModel.tpp.value!!.getId(),
                        it,
                        "Edit Tpp application#" + viewModel.getApp(it) ?: "N/A"
                    )
            // TODO#FIX: Throws Action not found, because the current context is the Tabs fragment,
            //  We didn't move to the Apps using action and changing the destination"
            findNavController().navigate(action)
        })
    }

    private fun setupEditFab() {
        activity?.findViewById<FloatingActionButton>(R.id.edit_app_fab)?.let {
            it.setOnClickListener {
                //viewModel.editApp()
                navigateToEditApp()
            }
        }
    }

    private fun setupAddFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_app_fab)?.let {
            it.setOnClickListener {
                //viewModel.addApp()
                navigateToAddNewApp()
            }
        }
    }

    private fun navigateToAddNewApp() {
        val action = TppDetailTabsFragmentDirections
                .actionTppDetailAppsFragmentToAddEditTppAppFragment(
                        viewModel.tpp.value!!.getId(),
                        null,
                        resources.getString(R.string.add_app)
                )
        findNavController().navigate(action)
    }

    private fun navigateToEditApp() {
        val theApp : App

        if ((viewModel.items != null) && (viewModel.items != null) && !viewModel.items.isEmpty()) {
            theApp = viewModel.items.get(0); /// TODO-FixIt: Edits always the first app. Place edit control next to app and seti the app as its context
            val action = TppDetailTabsFragmentDirections
                    .actionTppDetailAppsFragmentToAddEditTppAppFragment(
                        viewModel.tpp.value!!.getId(),
                        theApp.id,
                        resources.getString(R.string.add_app)
                )
            findNavController().navigate(action)
        } else {
            // TODO: Alert uset that no App was selected
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.tppdetail_apps_frag, container, false)

        viewDataBinding = TppdetailAppsFragBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        // Also set in onAActivityCreated: viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        /*CoroutineScope(Dispatchers.Main).launch {
            //tpp
            viewModel.start(args.tppId)
        }*/

        setHasOptionsMenu(true)
        return view
    }
}
