package com.applego.oblog.tppwatch.tpps

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applego.oblog.tppwatch.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.TppsFragBinding
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TppsFragBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)

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
        searchView?.setOnQueryTextFocusChangeListener { v, hasFocus ->
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
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
                    viewModel.loadTpps(false)
                    lastTppsSearchViewQuery = ""
                }
                return true
            }
        })

        var searchPlateId = searchView?.context?.resources?.getIdentifier("android:id/search_src_text", null, null)
        searchPlateId = searchView?.context?.resources?.getIdentifier("android:id/search_close_button", null, null)
        if (searchPlateId != null) {
            val closeBtn = searchView?.findViewById<ImageButton>(searchPlateId) /*as ImageView*/
            //searchView?.findViewById<ImageButton>(R.id.search_close_btn)
            closeBtn?.setOnClickListener(object: View.OnClickListener {
                        override fun onClick(v: View) {
                            if (!lastTppsSearchViewQuery.isNullOrBlank()) {
                                viewModel.loadTpps(false)
                                lastTppsSearchViewQuery = ""
                            }
                        }
                    })
        }


/*
        val searchPlateId = searchView?.context?.resources?.getIdentifier("android:id/search_src_text", null, null)
        if (searchPlateId != null) {
            val searchPlate = searchView?.findViewById(searchPlateId) as EditText ;
            searchPlate.setOnEditorActionListener(object: TextView.OnEditorActionListener {
                override fun onEditorAction(v : TextView , actionId : Int, event : KeyEvent ) : Boolean {

                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        //Do something
                    }
                    return true;

                }
            })
        }
*/



        val item = menu!!.findItem(R.id.revokedSwitchForActionBar)
        item.setActionView(R.layout.switch_item)

        val mySwitch = item.actionView.findViewById(R.id.mySwitch) as Switch
        mySwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                // do what you want with isChecked
                viewModel.showRevokedOnly()
            }
        })
    }

/*
    fun setOnQueryTextListener(listener: SearchView.OnQueryTextListener?) {
        super.setOnQueryTextListener(listener)
        this.listener = listener
        mSearchSrcTextView = this.findViewById(android.support.v7.appcompat.R.id.search_src_text)
        mSearchSrcTextView.setOnEditorActionListener({ textView, i, keyEvent ->
            if (listener != null) {
                listener!!.onQueryTextSubmit(getQuery().toString())
            }
            true
        })
    }
*/

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
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
            else -> false
        }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSearchFilter(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        //setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.tppsList)
        setupNavigation()
        setupTextSearch()
        setUpSearchForm();
        setupFab()
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
                //val country : String = (item is String) ? item : ""
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

    private fun setupTextSearch() {
        searchView = activity?.findViewById<SearchView>(R.id.search/*textSearchView app_bar_search*/)
        if (searchView != null) {

        }

        searchView?.setIconifiedByDefault(true)
        searchView?.setQueryHint("Type text to filter listed TPPs")
        searchView?.setOnQueryTextFocusChangeListener { v, hasFocus ->
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
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
                    viewModel.loadTpps(false)
                    lastTppsSearchViewQuery = ""
                }
                return true
            }
        })

        searchView?.findViewById<ImageButton>(R.id.search_close_btn)
                ?.setOnClickListener(object: View.OnClickListener {
                    override fun onClick(v: View) {
                        if (!lastTppsSearchViewQuery.isNullOrBlank()) {
                            viewModel.loadTpps(false)
                            lastTppsSearchViewQuery = ""
                        }
                    }
                })
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
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(com.applego.oblog.tppwatch.R.menu.filter_tpps, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TppsFilterType.USED_TPPS
                        R.id.followed -> TppsFilterType.FOLLOWED_TPPS
                        else -> TppsFilterType.ALL_TPPS
                    }
                )
                viewModel.loadTpps(false) // TODO: Use toggle on action bar - default to NO
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_tpp_fab)?.let {
            it.setOnClickListener {
                navigateToAddNewTpp()
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
        val action = TppsFragmentDirections.actionTppsFragmentToTppDetailFragment(tppId)
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
