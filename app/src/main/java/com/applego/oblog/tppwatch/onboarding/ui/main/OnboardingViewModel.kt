package com.applego.oblog.tppwatch.onboarding.ui.main

import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.util.Event

class OnboardingViewModel : ViewModel() {

    var pageCount: Int = 4 // TODO: find out how to pass this to the model
    var bgs: IntArray = intArrayOf(R.drawable.oblog_onboarding_1, R.drawable.oblog_onboarding_2, R.drawable.oblog_onboarding_3, R.drawable.oblog_onboarding_4)
    var lbls: IntArray = intArrayOf(R.string.onboarding_label_1, R.string.onboarding_label_2, R.string.onboarding_label_3, R.string.onboarding_label_4)
    var dscs: IntArray = intArrayOf(R.string.onboarding_description_1, R.string.onboarding_description_2, R.string.onboarding_description_3, R.string.onboarding_description_4)

    val contentIcon = ObservableInt()
    val contentText = ObservableInt()
    val contentDescription = ObservableInt()

    private val _index = MutableLiveData<Int>()
    var index: LiveData<Int> = _index

    private val _onboardingFinishEvent = MutableLiveData<Event<Boolean>>()
    var onboardingFinishEvent: LiveData<Event<Boolean>> = _onboardingFinishEvent

    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
        //lbls.get(_index?.value ?: R.string.resource_not_found))
    }

    val desc: LiveData<String> = Transformations.map(_index) {
        "Hello world from section = more descriptive: $it"
        //dscs.get(_index?.value ?: R.string.resource_not_found)
    }

    fun setIndex(index: Int) {
        _index.value = index

        contentText.set(lbls.get(index))
        contentDescription.set(dscs.get(index))
        contentIcon.set(bgs.get(index))
    }

    fun prevPage(): Int {
        if (_index.value == null) {
            _index.value = 0
        } else {
            if (_index.value!! > 0 ) {
                _index.value = _index.value!!.dec()
            }
        }
        return index.value!!
    }

    fun nextPage(): Int {
        if (_index.value == null) {
            _index.value = 0
        } else {
            if (_index.value!! < pageCount) {
                _index.value = _index.value!!.inc()
            } else if (_index.value!! == pageCount) {
                finishOnboarding(true)
            }
        }
        return index.value!!
    }

    fun skip(): Int {
        finishOnboarding(false)
        return index.value!!
    }

    /**
     * Called by Data Binding.
     */
    fun finishOnboarding(skipped: Boolean) {
        _onboardingFinishEvent.value = Event(skipped)
    }
}