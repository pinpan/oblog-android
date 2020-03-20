package com.applego.oblog.tppwatch.tppdetail

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * Adapter for the tpp details. Has a reference to the [TppDetailViewModel] to send actions back to it.
 */
class TppDetailTabsAdapter(private val viewModel: TppDetailViewModel, @NonNull fm : FragmentManager) : FragmentPagerAdapter (fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = TppDetailEbaFragment(viewModel, (viewModel.tpp.value?.getId()) ?: "")
        } else if (position == 1) {
            fragment = TppDetailNcaFragment(viewModel, (viewModel.tpp.value?.getId()) ?: "")
        } else { //if (position == 2) {
            fragment = TppDetailAppsFragment(viewModel, (viewModel.tpp.value?.getId()) ?: "")
        }
        return fragment
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = "EBA"
        } else if (position == 1) {
            title = "NCA()"
        } else if (position == 2) {
            title = "TPP's apps"
        }
        return title
    }
}
