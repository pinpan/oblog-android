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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applego.oblog.tppwatch.util.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.PspType
import com.applego.oblog.tppwatch.databinding.TppsFragBinding
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class TppsFragment : Fragment() {

    private val viewModel by viewModels<TppsViewModel> { getViewModelFactory() }

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

        /*searchView?.setOnQueryTextFocusChangeListener { v, hasFocus ->
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            searchBy("")
        }*/
        // perform set on query text listener event
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //val theID = id
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

        //var searchPlateId = searchView?.context?.resources?.getIdentifier("android:id/search_src_text", null, null)
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
        /*toolbar?.post { */
        val d: Drawable?= ResourcesCompat.getDrawable(resources, R.drawable.oblog_logo_48x52, null)
        toolbar?.setNavigationIcon(d)
        /*}*/

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
        val countryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.eu_countries, R.layout.spinner_item)

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

        val psd2RolesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.psd2_roles, R.layout.spinner_item)

        servicesSpinner = activity?.findViewById(R.id.search_role)!!
        servicesSpinner.setAdapter(psd2RolesAdapter);
        servicesSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                val service = parent.getItemAtPosition(pos).toString()
                viewModel.filterTppsByService(service)
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
        viewModel.filterByTitle(query)
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

                when (viewModel.searchFilter.pspType) {
                    PspType.ALL_PSD2 -> menu.findItem(R.id.allPSD2).isChecked = true
                    PspType.ONLY_PSD2_TPPs -> menu.findItem(R.id.tppOnly).isChecked = true
                    PspType.ONLY_ASPSPs -> menu.findItem(R.id.aspspOnly).isChecked = true
                }
                menu.findItem(R.id.followed).isChecked = viewModel.searchFilter.showFollowedOnly
                menu.findItem(R.id.used).isChecked = viewModel.searchFilter.showUsedOnly
                menu.findItem(R.id.revoked_only).isChecked = viewModel.searchFilter.showRevokedOnly
                if (menu.findItem(R.id.revoked_only).isChecked) {
                    viewModel.searchFilter.showRevoked = true
                    menu.findItem(R.id.revoked).isChecked = true
                } else {
                    menu.findItem(R.id.revoked).isChecked = viewModel.searchFilter.showRevoked
                }

                setOnMenuItemClickListener {
                    //it.isChecked = !it.isChecked
                    viewModel.setFiltering(
                            when (it.itemId) {
                                R.id.allPSD2 -> TppsFilterType.ALL_TPPs
                                R.id.used -> TppsFilterType.USED_TPPs
                                R.id.followed -> TppsFilterType.FOLLOWED_TPPs
                                R.id.aspspOnly -> TppsFilterType.ONLY_PSD2_FIs
                                R.id.tppOnly-> TppsFilterType.ONLY_PSD2_TPPs
                                R.id.revoked-> TppsFilterType.REVOKED_TPPs
                                R.id.revoked_only-> TppsFilterType.REVOKED_ONLY_TPPs
                                else -> TppsFilterType.ONLY_PSD2_TPPs
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
