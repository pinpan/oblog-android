package com.applego.oblog.tppwatch.onboarding.ui.main

import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.applego.oblog.tppwatch.R

class OnboardingViewModel : SectionsPagingViewModel() {

    var bgs: IntArray  = intArrayOf(R.drawable.oblog_onboarding_1, R.drawable.oblog_onboarding_2, R.drawable.oblog_onboarding_3, R.drawable.oblog_onboarding_4)
    var lbls: IntArray = intArrayOf(R.string.onboarding_label_1, R.string.onboarding_label_2, R.string.onboarding_label_3, R.string.onboarding_label_4)
    var dscs: IntArray = intArrayOf(R.string.onboarding_description_1, R.string.onboarding_description_2, R.string.onboarding_description_3, R.string.onboarding_description_4)

    val text: LiveData<Int> = Transformations.map(_index) {
        lbls.get(_index?.value ?: R.string.resource_not_found)
    }

    val desc: LiveData<Int> = Transformations.map(_index) {
        dscs.get(_index?.value ?: R.string.resource_not_found)
    }

    val contentIcon = ObservableInt()
    val contentText = ObservableInt()
    val contentDescription = ObservableInt()

    override fun setSectionViewData() {
        contentText.set(lbls.get(index.value ?: 0))
        contentDescription.set(dscs.get(index.value ?: 0))
        contentIcon.set(bgs.get(index.value ?: 0))
    }
}