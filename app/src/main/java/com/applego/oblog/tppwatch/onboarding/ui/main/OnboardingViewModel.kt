package com.applego.oblog.tppwatch.onboarding.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.util.Event

class OnboardingViewModel : ViewModel() {

    private val _onboardingPrevage = MutableLiveData<Event<Unit>>()
    var onboardingPrevPage: LiveData<Event<Unit>> = _onboardingPrevage

    private val _onboardingNextPage = MutableLiveData<Event<Unit>>()
    var onboardingNextPage: LiveData<Event<Unit>> = _onboardingNextPage

    private val _onboardingFinishEvent = MutableLiveData<Event<Unit>>()
    var onboardingFinishEvent: LiveData<Event<Unit>> = _onboardingFinishEvent

    private val _index = MutableLiveData<Int>()
    val index : LiveData<Int> = _index

    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }

    /**
     * Called by Data Binding.
     */
    fun finishOnboarding() {
        _onboardingFinishEvent.value = Event(Unit)
    }

    /**
     * Called by Data Binding.
     */
    fun prevPage() {
        _onboardingPrevage.value = Event(Unit)
    }

    /**
     * Called by Data Binding.
     */
    fun nextPage() {
        _onboardingNextPage.value = Event(Unit)
    }

}