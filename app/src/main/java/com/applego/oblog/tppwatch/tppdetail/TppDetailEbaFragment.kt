package com.applego.oblog.tppwatch.tppdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applego.oblog.tppwatch.util.EventObserver
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.TppdetailEbaFragBinding
import com.applego.oblog.tppwatch.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Main UI for the tpp detail screen.
 */
class TppDetailEbaFragment (private val viewModel: TppDetailViewModel, @Nullable private val tppId : String) : Fragment() {
    private lateinit var viewDataBinding: TppdetailEbaFragBinding

    private val args: TppDetailTabsFragmentArgs by navArgs()

    private lateinit var fragmentAdapter: TppDetailAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setupFab()
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()

        //this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

    private fun setupListAdapter() {
        //val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            fragmentAdapter = TppDetailAdapter(viewModel, context!!, R.layout.tpp_passport)
            viewDataBinding.passportsServicesList.adapter = fragmentAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupNavigation() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.tppdetail_eba_frag, container, false)

        viewDataBinding = TppdetailEbaFragBinding.bind(view).apply {
            viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setHasOptionsMenu(true)
        return view
    }
}
