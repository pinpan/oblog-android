package com.applego.oblog.tppwatch

import androidx.lifecycle.LiveData
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.util.Event
import org.junit.Assert.assertEquals

fun assertLiveDataEventTriggered(
        liveData: LiveData<Event<Tpp>>,
        tpp: Tpp
) {
    val value = LiveDataTestUtil.getValue(liveData)
    assertEquals(value.getContentIfNotHandled(), tpp.ebaEntity.getId())
}

fun assertLiveDataEventTriggered(
        liveData: LiveData<Event<String>>,
        tppId: String
) {
    val value = LiveDataTestUtil.getValue(liveData)
    assertEquals(value.getContentIfNotHandled(), tppId)
}

fun assertSnackbarMessage(snackbarLiveData: LiveData<Event<Int>>, messageId: Int) {
    val value: Event<Int> = LiveDataTestUtil.getValue(snackbarLiveData)
    assertEquals(value.getContentIfNotHandled(), messageId)
}
