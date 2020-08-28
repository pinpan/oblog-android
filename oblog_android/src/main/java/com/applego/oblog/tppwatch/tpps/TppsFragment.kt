package com.applego.oblog.tppwatch.tpps

import android.app.SearchManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applego.oblog.tppwatch.util.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.InstType
import com.applego.oblog.tppwatch.databinding.TppsFragBinding
import com.applego.oblog.tppwatch.util.ViewModelFactory.Companion.viewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class TppsFragment : Fragment() {

    private val viewModel = viewModelFactory.get(TppsViewModel::class.java) as TppsViewModel

    private val args: TppsFragmentArgs by navArgs()

    private lateinit var viewDataBinding: TppsFragBinding

    private lateinit var listAdapter: TppsAdapter

    private var searchView: SearchView? = null
    var lastTppsSearchViewQuery = ""

    lateinit var countriesSpinner: Spinner
    lateinit var servicesSpinner: Spinner
    private  var toolbarIcon: Drawable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TppsFragBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)

        viewModel.refreshTpp(arguments?.getString("tppId"))

        return viewDataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.tpps_fragment_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        }

        searchView?.setIconifiedByDefault(true)
        searchView?.setQueryHint("Type text to filter listed TPPs")

        // perform set on query text listener event
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                lastTppsSearchViewQuery = query
                searchBy(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // do something when text changes
                return false
            }
        })
        searchView?.setOnCloseListener (object: SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (!lastTppsSearchViewQuery.isNullOrBlank()) {
                    lastTppsSearchViewQuery = ""
                    //viewModel.loadTpps(false)
                    searchBy("")
                }
                return false
            }
        })

        var searchPlateId = searchView?.context?.resources?.getIdentifier("android:id/search_close_button", null, null)
        if (searchPlateId != null) {
            val closeBtn = searchView?.findViewById<ImageButton>(searchPlateId)
            closeBtn?.setOnClickListener(object: View.OnClickListener {
                        override fun onClick(v: View) {
                            if (!lastTppsSearchViewQuery.isNullOrBlank()) {
                                viewModel.loadTpps(false)
                                lastTppsSearchViewQuery = ""
                            }
                        }
                    })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.about_frag-> {
                openAbout()
                true
            }
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
            R.id.menu_add_tpp -> {
                navigateToAddNewTpp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    override fun onResume() {
        super.onResume()

        val toolbar: Toolbar ?= activity?.findViewById(com.applego.oblog.tppwatch.R.id.toolbar)
        val d: Drawable?= ResourcesCompat.getDrawable(resources, R.drawable.oblog_logo_48x52, null)
        toolbar?.setNavigationIcon(d)

        viewModel.refresh()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSearchFilter(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }

        setupSnackbar()
        setupListAdapter()
        setupNavigation()
        setUpSearchForm();

        toolbarIcon = ResourcesCompat.getDrawable(resources, R.drawable.oblog_logo_48x52, null)
    }

    private fun setupSearchFilter(savedInstanceState: Bundle?) {
        viewModel.setupSearchFilter(savedInstanceState)
    }

    private fun setUpSearchForm() {
        val countryAdapter = ArrayAdapter.createFromResource(getActivity() as Context, R.array.eu_countries, R.layout.spinner_item)

        countriesSpinner = activity?.findViewById(R.id.serarch_country)!!
        countriesSpinner.setAdapter(countryAdapter);
        countriesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                val countryISO = context?.resources?.getStringArray(R.array.eu_countries_iso)!![pos];

                viewModel.filterTppsByCountry(countryISO)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        })

        val psd2RolesAdapter = ArrayAdapter.createFromResource(getActivity() as Context, R.array.eba_services, R.layout.spinner_item)

        servicesSpinner = activity?.findViewById(R.id.search_role)!!
        servicesSpinner.setAdapter(psd2RolesAdapter);
        servicesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                val service = parent.getItemAtPosition(pos).toString()
                val serviceCode = resources.getStringArray(R.array.eba_service_codes)[pos];
                viewModel.filterTppsByService(serviceCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveSearchFilter(outState)

        super.onSaveInstanceState(outState)
    }

    fun searchBy(query: String) {
        viewModel.applyFilterByTitle(query)
    }

    private fun setupNavigation() {
        viewModel.openTppEvent.observe(this, EventObserver {
            openTppDetails(it)
        })
        viewModel.newTppEvent.observe(this, EventObserver {
            navigateToAddNewTpp()
        })
        viewModel.aboutEvent.observe(this, EventObserver {
            openAbout()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter)
        if (view != null) {
            PopupMenu(requireContext(), view).run {
                menuInflater.inflate(R.menu.filter_tpps, menu)

                when (viewModel.searchFilter.instType) {
                    InstType.INST_PI -> menu.findItem(R.id.inst_psd2_pi).isChecked = true
                    InstType.INST_AI -> menu.findItem(R.id.inst_psd2_ai).isChecked = true
                    InstType.INST_PIAI -> menu.findItem(R.id.inst_psd2_piai).isChecked = true
                    InstType.INST_EPI -> menu.findItem(R.id.inst_psd2_epi).isChecked = true
                    InstType.INST_EMI -> menu.findItem(R.id.inst_emi).isChecked = true
                    InstType.INST_EEMI -> menu.findItem(R.id.inst_e_emi).isChecked = true
                    InstType.NON_PSD2_INST -> menu.findItem(R.id.non_psd2_inst).isChecked = true
                    InstType.CIs -> menu.findItem(R.id.credit_inst).isChecked = true
                }

                menu.findItem(R.id.show_branches).isChecked = viewModel.searchFilter.showBranches
                menu.findItem(R.id.show_agents).isChecked = viewModel.searchFilter.showAgents

/*
                menu.findItem(R.id.followed).isChecked = viewModel.searchFilter.showFollowedOnly
                menu.findItem(R.id.used).isChecked = viewModel.searchFilter.showUsedOnly
                menu.findItem(R.id.revoked_only).isChecked = viewModel.searchFilter.showRevokedOnly
                if (menu.findItem(R.id.revoked_only).isChecked) {
                    viewModel.searchFilter.showRevoked = true
                    menu.findItem(R.id.revoked).isChecked = true
                } else {
                    menu.findItem(R.id.revoked).isChecked = viewModel.searchFilter.showRevoked
                }
*/

                setOnMenuItemClickListener {
                    viewModel.setFiltering(
                            when (it.itemId) {
                                R.id.allPSD2 -> TppsFilterType.ALL_INST

                                R.id.inst_psd2_pi-> TppsFilterType.PI_INST
                                R.id.inst_psd2_ai-> TppsFilterType.AI_INST
                                R.id.inst_psd2_piai-> TppsFilterType.PIAI_INST
                                R.id.inst_psd2_epi-> TppsFilterType.E_PI_INST
                                R.id.inst_emi -> TppsFilterType.EMONEY_INST
                                R.id.inst_e_emi -> TppsFilterType.E_EMONEY_INST
                                R.id.non_psd2_inst -> TppsFilterType.NON_PSD2_INST

                                R.id.show_branches -> TppsFilterType.BRANCHES
                                R.id.show_agents -> TppsFilterType.AGENTS
                                R.id.credit_inst -> TppsFilterType.CREDIT_INST

/*
                                R.id.followed -> TppsFilterType.FOLLOWED
                                R.id.used -> TppsFilterType.USED
                                R.id.revoked-> TppsFilterType.REVOKED
                                R.id.revoked_only-> TppsFilterType.REVOKED_ONLY
*/

                                else -> TppsFilterType.ALL_INST
                            }
                    )
                    viewModel.loadTpps(false)
                    true
                }
                show()
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
        arguments?.putString("tppId", tppId)
        val action = TppsFragmentDirections.actionTppsFragmentToTppDetailTabsFragment(tppId)
        findNavController().navigate(action)
    }

    private fun openAbout() {
        val action = TppsFragmentDirections.actionTppsFragmentToAboutFragment()
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TppsAdapter(viewModel, context!!, R.layout.tppitemlayout)

            viewDataBinding.tppsList.adapter = listAdapter

        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}
