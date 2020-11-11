package com.applego.oblog.tppwatch.tppdetail

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * Adapter for the tpp details. Has a reference to the [TppDetailViewModel] to send actions back to it.
 */
class TppDetailTabsAdapter(private val viewModel: TppDetailViewModel, private val appsViewModel: AppsViewModel, @NonNull fm : FragmentManager) : FragmentPagerAdapter (fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val integratedRegisters =  listOf("EU", "CZ")

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null

        if (position == 0) {
            fragment = TppDetailEbaFragment(viewModel, (viewModel.tpp.value?.getId()) ?: "")
        } else if (position == 1) {
            if (viewModel.tpp.value?.getCountry() in integratedRegisters) {
                fragment = TppDetailNcaFragment(viewModel, (viewModel.tpp.value?.getId()) ?: "")
            } else {
                fragment = TppDetailAppsFragment(appsViewModel)
            }
        } else { //if (position == 2) {
            fragment = TppDetailAppsFragment(appsViewModel)
        }

        return fragment
    }

    override fun getCount(): Int {
        if (viewModel.tpp.value?.getCountry() in integratedRegisters) {
            return integratedRegisters.size + 1
        } else {
            return integratedRegisters.size
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String?

        if (position == 0) {
            title = "EBA"
        } else if (position == 1) {
            if (viewModel.tpp.value?.getCountry() in integratedRegisters) {
                title = "NCA (" + (viewModel.tpp.value?.getCountry() ?: "N/A") + ")"
            } else {
                title = "APPs"
            }
        } else /*if (position == 2)*/ {
            title = "APPs"
        }

        return title
    }
}
