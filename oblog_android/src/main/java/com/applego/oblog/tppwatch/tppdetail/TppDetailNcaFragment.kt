package com.applego.oblog.tppwatch.tppdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.databinding.TppdetailNcaFragBinding
import timber.log.Timber

/**
 * Main UI for the tpp detail screen.
 */
class TppDetailNcaFragment(private val viewModel: TppDetailViewModel, @Nullable private val tppId : String) : Fragment() {
    private lateinit var viewDataBinding: TppdetailNcaFragBinding

    private lateinit var listAdapter: TppDetailAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.tppdetail_nca_frag, container, false)
        viewDataBinding = TppdetailNcaFragBinding.bind(view).apply {
            viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setHasOptionsMenu(true)
        return view
    }
}
