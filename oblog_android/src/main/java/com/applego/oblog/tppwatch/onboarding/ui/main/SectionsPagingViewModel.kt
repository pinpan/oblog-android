package com.applego.oblog.tppwatch.onboarding.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.util.Event

open abstract class SectionsPagingViewModel : ViewModel() {

    protected var pageCount: Int = 4 // TODO: find out how to pass this to the model
    protected val _index = MutableLiveData<Int>()
    var index: LiveData<Int> = _index

    private val _onboardingFinishEvent = MutableLiveData<Event<Boolean>>()
    var onboardingFinishEvent: LiveData<Event<Boolean>> = _onboardingFinishEvent

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
            if (_index.value!! < pageCount-1) {
                _index.value = _index.value!!.inc()
            } else if (_index.value!! == pageCount-1) {
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

    fun setIndex(index: Int) {
        _index.value = index

        setSectionViewData()
    }

    abstract fun setSectionViewData()
}
