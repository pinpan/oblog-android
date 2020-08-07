package com.applego.oblog.tppwatch.onboarding.ui.main

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.R

private val TAB_TITLES = arrayOf(
        R.string.label_onbording_introduction,
        R.string.label_onboarding_features,
        R.string.label_onboarding_features,
        R.string.label_onboarding_disclaimer
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(val viewModel: ViewModel, val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return OnboardingFragment.newInstance(position)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return 4
    }

    override fun instantiateItem(container: View, position: Int): Any {
        return super.instantiateItem(container, position)
    }

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
    }
}