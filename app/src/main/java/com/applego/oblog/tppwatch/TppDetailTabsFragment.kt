package com.applego.oblog.tppwatch

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applego.oblog.tppwatch.tppdetail.TppDetailTabsViewModel


class TppDetailTabsFragment : Fragment() {

    companion object {
        fun newInstance() = TppDetailTabsFragment()
    }

    private lateinit var viewModel: TppDetailTabsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tpp_detail_tabs_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TppDetailTabsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
