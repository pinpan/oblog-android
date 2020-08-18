package com.applego.oblog.tppwatch.util

import com.applego.oblog.tppwatch.util.Status.ERROR
import com.applego.oblog.tppwatch.util.Status.LOADING
import com.applego.oblog.tppwatch.util.Status.SUCCESS

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class State<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): State<T> {
            return State(SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): State<T> {
            return State(ERROR, data, msg)
        }

        fun <T> loading(data: T?): State<T> {
            return State(LOADING, data, null)
        }
    }
}
