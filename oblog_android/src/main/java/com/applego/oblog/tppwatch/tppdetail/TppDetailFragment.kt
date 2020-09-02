package com.applego.oblog.tppwatch.tppdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applego.oblog.tppwatch.util.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.TppdetailFragBinding
import com.applego.oblog.tppwatch.tpps.DELETE_RESULT_OK
import com.applego.oblog.tppwatch.util.getViewModelFactory
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Main UI for the tpp detail screen.
 */
class TppDetailFragment : Fragment() {
    private lateinit var viewDataBinding: TppdetailFragBinding

    private val args: TppDetailTabsFragmentArgs by navArgs()

    private lateinit var listAdapter: TppDetailAdapter

    //private lateinit var expandableListAdapter: SimpleExpandableListAdapter

    private val viewModel by viewModels<TppDetailViewModel> { getViewModelFactory() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
        setupFab()
        //setupRefresh()
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()

    }

/*
    private fun setupRefresh() {
        activity?.findViewById<Button>(R.id.refresh_tpp)?.let {
            it.setOnClickListener {
                refreshTppFromServer()
            }
        }
    }
*/

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_refresh_tpp -> {
                refreshTppFromServer()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun refreshTppFromServer() {
        viewModel.refresh()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TppDetailAdapter(viewModel, context!!, R.layout.tpp_passport)
            viewDataBinding.passportsList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupNavigation() {
       viewModel.deleteTppEvent.observe(this, EventObserver {
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

        val view = inflater.inflate(R.layout.tppdetail_frag, container, false)

        viewDataBinding = TppdetailFragBinding.bind(view).apply {
            viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.start(args.tppId)
        }

        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.menu_refresh_tpp).setOnMenuItemClickListener {
            viewModel.refresh()
            true
        }

        inflater.inflate(R.menu.tppdetail_fragment_menu, menu)
    }
}