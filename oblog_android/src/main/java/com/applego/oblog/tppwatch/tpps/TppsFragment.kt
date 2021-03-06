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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.EUCountry
import com.applego.oblog.tppwatch.data.model.EUCountry.Companion.allEUCountriesWithEU
import com.applego.oblog.tppwatch.data.model.EbaService
import com.applego.oblog.tppwatch.data.model.EbaService.Companion.psd2ServiesWithAll_AllOptions
import com.applego.oblog.tppwatch.data.model.InstType
import com.applego.oblog.tppwatch.databinding.TppsFragBinding
import com.applego.oblog.tppwatch.util.EventObserver
import com.applego.oblog.tppwatch.util.ViewModelFactory.Companion.viewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.applego.oblog.ui.CountriesSpinnerAdapter
import com.applego.oblog.ui.IconAndTextSpinnerAdapter
import com.applego.oblog.ui.TextSpinnerAdapter
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

class TppsFragment : Fragment() {

    private val tppsFragViewModel by activityViewModels<TppsViewModel> { viewModelFactory }

    private val args: TppsFragmentArgs by navArgs()

    private lateinit var viewDataBinding: TppsFragBinding

    private lateinit var listAdapter: TppsAdapter

    private var searchView: SearchView? = null

    private var syncMenuItem: MenuItem? = null

    var lastTppsSearchViewQuery = ""

    private var firstVisibleInListview = 0

    lateinit var countriesSpinner: Spinner
    lateinit var servicesSpinner: Spinner

    var progressBar:ProgressBar? = null

    var progressText:TextView? = null

    private  var toolbarIcon: Drawable? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TppsFragBinding.inflate(inflater, container, false).apply {
            viewmodel = tppsFragViewModel
        }
        setHasOptionsMenu(true)

        tppsFragViewModel.refreshTpp(arguments?.getString("tppId"))

        syncMenuItem?.setEnabled(progressBar?.visibility != View.VISIBLE)

        return viewDataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.tpps_fragment_menu, menu)

        syncMenuItem = menu.findItem(R.id.sync_directory)

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
        searchView?.setOnCloseListener(object : SearchView.OnCloseListener {
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
            closeBtn?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (!lastTppsSearchViewQuery.isNullOrBlank()) {
                        tppsFragViewModel.loadTpps()
                        lastTppsSearchViewQuery = ""
                    }
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.about_frag -> {
                openAbout()
                true
            }
            R.id.sync_directory -> {
                tppsFragViewModel.syncEbaDirectory()
                syncMenuItem?.setEnabled(false)
                // TODO: Sync all NCAs
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                tppsFragViewModel.loadTpps()
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
        progressText = activity?.findViewById(R.id.progress_text)!!

        syncMenuItem?.setEnabled(progressBar?.visibility != View.VISIBLE)

        tppsFragViewModel.loadProgressStart.observe(this, EventObserver {
            progressBar?.max = it.totalPages
            progressBar?.visibility = View.VISIBLE
            progressText?.visibility = View.VISIBLE
            syncMenuItem?.setEnabled(false)
        })
        tppsFragViewModel.loadProgressEnd.observe(this, EventObserver {
            progressBar?.visibility = View.GONE
            progressText?.visibility = View.GONE
            syncMenuItem?.setEnabled(true)
        })
        tppsFragViewModel.loadProgress.observe(this, EventObserver {
            if (it.totalPages.compareTo(it.page + 1) > 0) {
                progressBar?.visibility = View.VISIBLE
                progressText?.visibility = View.VISIBLE
                progressBar?.max = it.totalPages
            }
            syncMenuItem?.setEnabled(false)
            progressBar?.progress = it.page
        })
        tppsFragViewModel.loadTpps()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSearchFilter(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        arguments?.let {
            tppsFragViewModel.showEditResultMessage(args.userMessage)
        }

        setupSnackbar()
        setupListAdapter()
        setupNavigation()
        setUpSearchForm();

        toolbarIcon = ResourcesCompat.getDrawable(resources, R.drawable.oblog_logo_48x52, null)
    }

    private fun setupSearchFilter(savedInstanceState: Bundle?) {
        tppsFragViewModel.setupSearchFilter(savedInstanceState)
    }

    private fun setUpSearchForm() {
        countriesSpinner = activity?.findViewById(R.id.serarch_country)!!
        val countryAdapter = CountriesSpinnerAdapter(getActivity() as Context, R.layout.custom_spinner_item, countriesSpinner, allEUCountriesWithEU, 12)
        countriesSpinner.setAdapter(countryAdapter);
        countriesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                tppsFragViewModel.filterTppsByCountry(countryAdapter.getItem(pos)?.isoCode ?: EUCountry.EU.isoCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        })

        servicesSpinner = activity?.findViewById(R.id.search_by_service)!!
        val servicesAdapter = IconAndTextSpinnerAdapter(getActivity() as Context, R.layout.custom_spinner_item, servicesSpinner, getShortDescriptions(psd2ServiesWithAll_AllOptions), 12)
        servicesSpinner.setAdapter(servicesAdapter);
        servicesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                tppsFragViewModel.filterTppsByService(servicesAdapter.getItem(pos))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        })
        servicesSpinner.setSelection(2)

        val recyclerView: RecyclerView = activity?.findViewById(R.id.tpps_list)!!
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                firstVisibleInListview = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition();
                var aTpp = tppsFragViewModel.displayedItems.value?.get(firstVisibleInListview)
                // TODO: Find the Tpp item position after sorting
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                firstVisibleInListview = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            }
        })

        val orderByDirectionButton:ImageButton = activity?.findViewById(R.id.order_direction)!!
        orderByDirectionButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                orderByDirectionButton.setImageResource(if (tppsFragViewModel.orderByDirection?.value
                                ?: false)
                                   R.drawable.sort_ascending_bars else R.drawable.sort_descending_bars)
                tppsFragViewModel.reverseOrderBy()
                listAdapter.notifyDataSetChanged()
                recyclerView.invalidate()
            }
        })

        val orderBySpinner:Spinner = activity?.findViewById(R.id.order_by)!!
        val orderByAdapter = TextSpinnerAdapter(getActivity() as Context, R.layout.custom_spinner, orderBySpinner, getOrderByFieldNames(), 12)
        orderBySpinner.setAdapter(orderByAdapter)
        orderBySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val orderByField = resources.getStringArray(R.array.orderby_field_values)[pos];
                tppsFragViewModel.orderTppsBy(orderByField)
                tppsFragViewModel.orderTpps()
                listAdapter.notifyDataSetChanged()
                recyclerView.invalidate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        })

        val btnFirst:ImageButton = activity?.findViewById(R.id.btn_first)!!
        btnFirst.setOnClickListener{
            recyclerView.scrollToPosition(0);
        }

        val btnLast:ImageButton = activity?.findViewById(R.id.btn_last)!!
        btnLast.setOnClickListener{
            recyclerView.scrollToPosition((tppsFragViewModel.displayedItems.value?.size ?: 1) - 1);
        }
    }

    private fun getOrderByFieldNames() : List<String> {
        val resourcesArray =  resources.getStringArray(R.array.orderby_field_names)
        return resourcesArray.toList()
    }

    private fun getShortDescriptions(psd2Servies: ArrayList<EbaService>): List<String> {
        val result = ArrayList<String>()
        psd2Servies.forEach {
            result.add(it.shortDescription)
        }
        return result
    }

    override fun onSaveInstanceState(outState: Bundle) {
        tppsFragViewModel.saveSearchFilter(outState)
        progressBar?.visibility = View.INVISIBLE
        progressText?.visibility = View.INVISIBLE

        super.onSaveInstanceState(outState)
    }

    fun searchBy(query: String) {
        tppsFragViewModel.applyFilterByTitle(query)
    }

    private fun setupNavigation() {
        tppsFragViewModel.openTppEvent.observe(viewLifecycleOwner, EventObserver {
            openTppDetails(it)
        })
        tppsFragViewModel.newTppEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddNewTpp()
        })
        tppsFragViewModel.aboutEvent.observe(viewLifecycleOwner, EventObserver {
            openAbout()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, tppsFragViewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            tppsFragViewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter)
        if (view != null) {
            PopupMenu(requireContext(), view).run {
                menuInflater.inflate(R.menu.filter_tpps, menu)

                when (tppsFragViewModel.searchFilter.instType) {
                    InstType.INST_PI -> menu.findItem(R.id.inst_psd2_pi).isChecked = true
                    InstType.INST_AI -> menu.findItem(R.id.inst_psd2_ai).isChecked = true
                    InstType.INST_PIAI -> menu.findItem(R.id.inst_psd2_piai).isChecked = true
                    InstType.INST_EPI -> menu.findItem(R.id.inst_psd2_epi).isChecked = true
                    InstType.INST_EMI -> menu.findItem(R.id.inst_emi).isChecked = true
                    InstType.INST_EEMI -> menu.findItem(R.id.inst_e_emi).isChecked = true
                    InstType.NON_PSD2_INST -> menu.findItem(R.id.non_psd2_inst).isChecked = true
                    InstType.CIs -> menu.findItem(R.id.credit_inst).isChecked = true
                }
                menu.findItem(R.id.show_branches).isChecked = tppsFragViewModel.searchFilter.showBranches
                menu.findItem(R.id.show_agents).isChecked = tppsFragViewModel.searchFilter.showAgents

                setOnMenuItemClickListener {
                    tppsFragViewModel.setFiltering(
                            when (it.itemId) {
                                R.id.allPSD2 -> TppsFilterType.ALL_INST

                                R.id.inst_psd2_pi -> TppsFilterType.PI_INST
                                R.id.inst_psd2_ai -> TppsFilterType.AI_INST
                                R.id.inst_psd2_piai -> TppsFilterType.PIAI_INST
                                R.id.inst_psd2_epi -> TppsFilterType.E_PI_INST
                                R.id.inst_emi -> TppsFilterType.EMONEY_INST
                                R.id.inst_e_emi -> TppsFilterType.E_EMONEY_INST
                                R.id.non_psd2_inst -> TppsFilterType.NON_PSD2_INST

                                R.id.show_branches -> TppsFilterType.BRANCHES
                                R.id.show_agents -> TppsFilterType.AGENTS
                                R.id.credit_inst -> TppsFilterType.CREDIT_INST

                                else -> TppsFilterType.ALL_INST
                            }
                    )
                    tppsFragViewModel.loadTpps()
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
            listAdapter = TppsAdapter(viewModel)

            viewDataBinding.tppsList.adapter = listAdapter

        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}
