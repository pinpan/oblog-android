package com.applego.oblog.tppwatch.data

import com.applego.oblog.tppwatch.data.Result.Success
import okio.Timeout
import java.util.*

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T> {

    data class Idle(val since: Date) : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Warn(val warning: String, val message: String) : Result<Nothing>()
    data class Loading(val timeout: Timeout) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Warn -> "Warn[warning=$warning]"
            is Loading -> "Loading"
            is Idle -> "Idle"
        }
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Success && data != null
