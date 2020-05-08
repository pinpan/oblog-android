package com.applego.oblog.tppwatch.tppdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.applego.oblog.tppwatch.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.TppDetailTabsFragmentBinding
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

    private val appsViewModel by viewModels<AppsViewModel> { getViewModelFactory() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setupFab()
        setupRefresh()
        setupNavigation()

        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)

        //this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

    private fun setupRefresh() {
        activity?.findViewById<Button>(R.id.refresh_tpp)?.let {
            it.setOnClickListener {
                viewModel.refresh()
            }
        }
    }


    private fun setupNavigation() {
        viewModel.tppUpdatedEvent.observe(this, EventObserver {
            val action = TppDetailTabsFragmentDirections
                .actionTppDetailTabsFragmentToTppsFragment()
            findNavController().navigate(action)
        })

        viewModel.editTppEvent.observe(this, EventObserver {
            val action = TppDetailTabsFragmentDirections
                .actionTppDetailTabsFragmentToAddEditTppFragment(
                    args.tppId,
                    resources.getString(R.string.edit_tpp)
                )
            findNavController().navigate(action)
        })

        viewModel.showTppAppsEvent.observe(this, EventObserver {
            val action = TppDetailTabsFragmentDirections
                .actionTppDetailTabsFragmentToAddTppAppFragment(
                    args.tppId
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

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.start(args.tppId)
        }

        CoroutineScope(Dispatchers.Main).launch {
            appsViewModel.start(args.tppId)
        }

        val view = inflater.inflate(R.layout.tpp_detail_tabs_fragment, container, false)

        tppDetailTabsAdapter = TppDetailTabsAdapter(viewModel, appsViewModel, childFragmentManager)
        viewPager = view.findViewById(R.id.detail_tabs_pager)
        viewPager.adapter = tppDetailTabsAdapter

        val tabLayout = view.findViewById(R.id.detail_tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        viewDataBinding = TppDetailTabsFragmentBinding.bind(view).apply {
            viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tppdetail_fragment_menu, menu)
    }
}
