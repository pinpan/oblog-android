package com.applego.oblog.tppwatch.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import com.applego.oblog.tppwatch.util.wrapEspressoIdlingResource
import kotlinx.coroutines.launch

/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(
    private val tppsRepository: TppsRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    /**
     * Controls whether the stats are shown or a "No data" message.
     */
    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean> = _empty

    private val _usedTppsPercent = MutableLiveData<Float>()
    val usedTppsPercent: LiveData<Float> = _usedTppsPercent

    private val _followedTppsPercent = MutableLiveData<Float>()
    val followedTppsPercent: LiveData<Float> = _followedTppsPercent

    private var usedTpps = 0

    private var followedTpps = 0

    init {
        start()
    }

    fun start() {
        if (_dataLoading.value == true) {
            return
        }
        _dataLoading.value = true

        wrapEspressoIdlingResource {
            viewModelScope.launch {
                tppsRepository.getAllTpps().let { result ->
                    if (result is Success) {
                        _error.value = false
                        computeStats(result.data)
                    } else {
                        _error.value = true
                        usedTpps = 0
                        followedTpps = 0
                        computeStats(null)
                    }
                }
            }
        }
    }

    fun refresh() {
        start()
    }

    /**
     * Called when new data is ready.
     */
    private fun computeStats(tpps: List<Tpp>?) {
        getUsedAndFollowedStats(tpps).let {
            _usedTppsPercent.value = it.usedTppsPercent
            _followedTppsPercent.value = it.followedTppsPercent
        }
        _empty.value = tpps.isNullOrEmpty()
        _dataLoading.value = false
    }
}
