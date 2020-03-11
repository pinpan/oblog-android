/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            fragment = TppDetailEbaFragment(viewModel, (viewModel.tpp.value?.id) ?: "")
        } else if (position == 1) {
            fragment = TppDetailNcaFragment(viewModel, (viewModel.tpp.value?.id) ?: "")
        } else { //if (position == 2) {
            fragment = TppDetailAppsFragment(viewModel, (viewModel.tpp.value?.id) ?: "")
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
