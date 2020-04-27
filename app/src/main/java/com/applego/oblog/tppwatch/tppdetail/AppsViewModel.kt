package com.applego.oblog.tppwatch.tppdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applego.oblog.tppwatch.Event
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.repository.TppsRepository

class AppsViewModel (private val tppsRepository: TppsRepository) : ViewModel() {
    fun addApp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun editApp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val _editAppEvent = MutableLiveData<Event<Unit>>()
    val editAppEvent: LiveData<Event<Unit>> = _editAppEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _items = MutableLiveData<List<App>>().apply { value = emptyList() }
    val items: MutableLiveData<List<App>> = _items
}
