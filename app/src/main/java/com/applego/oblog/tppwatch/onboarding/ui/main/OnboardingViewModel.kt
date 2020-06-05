package com.applego.oblog.tppwatch.onboarding.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.util.Event

class OnboardingViewModel : ViewModel() {

    private val _onboardingFinishEvent = MutableLiveData<Event<Unit>>()
    val onboardingFinishEvent: LiveData<Event<Unit>> = _onboardingFinishEvent

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }

    //android:onClick="@{() -> viewmodel.openTpp(tpp.ebaEntity.id)}"
    /**
     * Called by Data Binding.
     */
    fun finishOnboarding() {
        _onboardingFinishEvent.value = Event(Unit)
    }

}