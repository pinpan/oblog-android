package com.applego.oblog.tppwatch.tpps

import android.app.SearchManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.util.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.EUCountry.Companion.allEUCountries
import com.applego.oblog.tppwatch.data.model.EUCountry.Companion.allEUCountriesWithEU
import com.applego.oblog.tppwatch.data.model.EbaService
import com.applego.oblog.tppwatch.data.model.EbaService.Companion.psd2Servies
import com.applego.oblog.tppwatch.data.model.InstType
import com.applego.oblog.tppwatch.databinding.TppsFragBinding
import com.applego.oblog.tppwatch.util.ViewModelFactory.Companion.viewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.applego.oblog.ui.CountriesSpinnerAdapter
import com.applego.oblog.ui.IconAndTextSpinnerAdapter
import com.applego.oblog.ui.TextSpinnerAdapter
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.ArrayList

class TppsFragment : Fragment() {

    private val viewModel = viewModelFactory.get(TppsViewModel::class.java) as TppsViewModel

    private val args: TppsFragmentArgs by navArgs()

    private lateinit var viewDataBinding: TppsFragBinding

    private lateinit var listAdapter: TppsAdapter

    private var searchView: SearchView? = null
    var lastTppsSearchViewQuery = ""

    lateinit var countriesSpinner: Spinner
    lateinit var servicesSpinner: Spinner

    var progressBar:ProgressBar? = null

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
                                viewModel.loadTpps()
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
                viewModel.loadTpps()
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

        progressBar = activity?.findViewById(R.id.progress_bar)!!

        viewModel.loadProgressStart.observe(this, EventObserver {
            progressBar?.max = it
            progressBar?.visibility = View.VISIBLE
        })
        viewModel.loadProgressEnd.observe(this, EventObserver {
            progressBar?.visibility = View.GONE
        })
        viewModel.loadProgress.observe(this, EventObserver {
            if ((progressBar?.progress ?: 0).compareTo(it.page) < 0) {
                progressBar?.visibility = View.VISIBLE
                progressBar?.max = it.totalPages
            }
            progressBar?.progress = it.page
        })
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
        countriesSpinner = activity?.findViewById(R.id.serarch_country)!!
        val countryAdapter = CountriesSpinnerAdapter(getActivity() as Context, R.layout.custom_spinner_item, countriesSpinner, allEUCountriesWithEU)
        countriesSpinner.setAdapter(countryAdapter);
        countriesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                val countryISO = allEUCountriesWithEU.get(pos).isoCode

                viewModel.filterTppsByCountry(countryISO)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        })

        //val services = context!!.resources.getTextArray(R.array.psd2_service_codes)
        servicesSpinner = activity?.findViewById(R.id.search_by_service)!!
        val servicesAdapter = IconAndTextSpinnerAdapter(getActivity() as Context, R.layout.custom_spinner_item, servicesSpinner, getShortDescriptions(psd2Servies))
        servicesSpinner.setAdapter(servicesAdapter);

        servicesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                val serviceCode = resources.getStringArray(R.array.eba_service_codes)[pos];
                viewModel.filterTppsByService(serviceCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        })

        val recyclerView: RecyclerView = activity?.findViewById(R.id.tpps_list)!!
        val btnFirst:ImageButton = activity?.findViewById(R.id.btn_first)!!
        btnFirst.setOnClickListener{
            recyclerView.scrollToPosition(0);
        }

        val btnLast:ImageButton = activity?.findViewById(R.id.btn_last)!!
        btnLast.setOnClickListener{
            recyclerView.scrollToPosition( (viewModel.displayedItems.value?.size ?:1) -1);
        }
    }

    private fun getShortDescriptions(psd2Servies: ArrayList<EbaService>): List<String> {
        val result = ArrayList<String>()
        psd2Servies.forEach {
            result.add(it.shortDescription)
        }
        return result
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveSearchFilter(outState)
        progressBar?.visibility = View.INVISIBLE
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

                                else -> TppsFilterType.ALL_INST
                            }
                    )
                    viewModel.loadTpps()
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
