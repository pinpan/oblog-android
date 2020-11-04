package com.applego.oblog.tppwatch.onboarding

import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.applego.oblog.tppwatch.R

class OnboardingViewModel : SectionsPagingViewModel() {

    val bgs: IntArray = intArrayOf(R.drawable.oblog_onboarding_1, R.drawable.oblog_onboarding_2_1, R.drawable.oblog_statistics_light, R.drawable.ic_empty)
    val lbls: IntArray = intArrayOf(R.string.onboarding_label_1, R.string.onboarding_label_2, R.string.onboarding_label_3, R.string.onboarding_label_4)
    val dscs: IntArray = intArrayOf(R.string.onboarding_description_1, R.string.onboarding_description_2, R.string.onboarding_description_3, R.string.onboarding_description_4)

    protected val _showDisclamer = MutableLiveData<Boolean>(false)
    val showDisclamer: LiveData<Boolean> = _showDisclamer

    fun isLastPage(): Boolean {
        return ((pageCount-1).equals(index.value))
    }
            //get() = _showDisclamer //.value ?: false
    //val showDisclamer: LiveData<Boolean> = _showDisclamer
            // This LiveData depends on another so we can use a transformation.

    val text: LiveData<Int> = Transformations.map(_index) {
        //"Hello world from section: $it"
        lbls.get(_index?.value ?: R.string.resource_not_found)
    }

    val desc: LiveData<Int> = Transformations.map(_index) {
        //"Hello world from section = more descriptive: $it"
        dscs.get(_index?.value ?: R.string.resource_not_found)
    }

    val image: LiveData<Int> = Transformations.map(_index) {
        bgs.get(_index?.value ?: R.drawable.ic_empty)
    }

    val contentIcon = ObservableInt()
    val contentText = ObservableInt()
    val contentDescription = ObservableInt()

    override fun setSectionViewData() {
        contentText.set(lbls.get(index.value ?: 0))
        contentDescription.set(dscs.get(index.value ?: 0))
        contentIcon.set(bgs.get(index.value ?: 0))
        _showDisclamer.value =  (pageCount-1).equals(index.value)
    }
}