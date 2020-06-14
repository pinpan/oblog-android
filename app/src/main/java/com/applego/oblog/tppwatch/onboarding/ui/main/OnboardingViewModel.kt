package com.applego.oblog.tppwatch.onboarding.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    var index: LiveData<Int> = _index

    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }

    fun prevPage() {
        if (_index.value == null) {
            _index.value = 0
        } else {
            if (_index.value!! > 0 ) {
                _index.value = _index.value!!.dec()
            }
        }
    }

    fun nextPage() {
        if (_index.value == null) {
            _index.value = 0
        } else {
            if (_index.value!! <2) {
                _index.value = _index.value!!.inc()
            }
        }
    }

    /**
     * Called by Data Binding.
     */
    /*fun finishOnboarding() {
        _onboardingFinishEvent.value = Event(Unit)
    }*/
}