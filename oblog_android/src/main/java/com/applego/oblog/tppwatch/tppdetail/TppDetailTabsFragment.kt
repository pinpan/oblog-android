package com.applego.oblog.tppwatch.tppdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.addedittppapp.AddEditTppAppFragmentDirections
import com.applego.oblog.tppwatch.databinding.TppDetailTabsFragmentBinding
import com.applego.oblog.tppwatch.util.EventObserver
import com.applego.oblog.tppwatch.util.ViewModelFactory.Companion.viewModelFactory
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.runBlocking


/**
 * Main UI for the tpp detail screen.
 */
class TppDetailTabsFragment : Fragment() {

    private lateinit var tppDetailTabsAdapter: TppDetailTabsAdapter

    private lateinit var viewPager: ViewPager

    private lateinit var viewDataBinding: TppDetailTabsFragmentBinding

    private val  args: TppDetailTabsFragmentArgs by navArgs()

    private val viewModel by activityViewModels<TppDetailViewModel> { viewModelFactory }

    private val appsViewModel by activityViewModels<AppsViewModel> { viewModelFactory }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.tppUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TppDetailTabsFragmentDirections
                    .actionTppDetailTabsFragmentToTppsFragment(args.tppId)
            findNavController().navigate(action)
        })

        viewModel.editTppEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TppDetailTabsFragmentDirections
                    .actionTppDetailTabsFragmentToAddEditTppFragment(
                            args.tppId,
                            resources.getString(R.string.edit_tpp)
                    )
            findNavController().navigate(action)
        })

        viewModel.addTppAppEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TppDetailTabsFragmentDirections
                    .actionTppDetailAppsFragmentToAddEditTppAppFragment(
                            args.tppId,
                            null,
                            resources.getString(R.string.add_app)
                    )
            findNavController().navigate(action)
        })

        viewModel.editTppAppEvent.observe(viewLifecycleOwner, EventObserver {
            val action = AddEditTppAppFragmentDirections
                    .actionAddEditTppAppFragmentToTppDetailTabsFragment(
                            args.tppId
                    )
            findNavController().navigate(action)
        })

        viewModel.backToTppsListEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TppDetailTabsFragmentDirections
                    .actionTppDetailTabsFragmentToTppsFragment(
                            args.tppId
                    )
            findNavController().navigate(action)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.start(args.tppId)

        runBlocking {
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
        inflater.inflate(R.menu.tpp_detail_tabs_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.menu_refresh_tpp -> {
                    viewModel.refresh()
                    true
                }
                R.id.about_frag-> {
                    openAbout()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun openAbout() {
        val action = TppDetailTabsFragmentDirections.actionTppDetailTabsFragmentToAboutFragment()

        findNavController().navigate(action)
    }
}
