package com.applego.oblog.tppwatch.data.repository

import androidx.lifecycle.MutableLiveData
import com.applego.oblog.tppwatch.util.State
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class BaseRepository<T> {

    lateinit var call: Call<T>

    fun makeCall(call:Call<T>): MutableLiveData<State<T>> {
        this.call = call
        val callBackKt = CallBackKt<T>()
        callBackKt.result.value = State.loading(null)
        this.call.clone().enqueue(callBackKt)
        return callBackKt.result
    }

    class CallBackKt<T>: Callback<T> {
        var result: MutableLiveData<State<T>> = MutableLiveData()

        override fun onFailure(call: Call<T>, t: Throwable) {
            result.value = State.error(t.hashCode().toString() + "#" + t.localizedMessage/*ErrorUtils.parseError(t.hashCode(), t.localizedMessage)*/, null)
            t.printStackTrace()
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful)
                result.value = State.success(response.body())
            else {
                result.value = State.error("#" + response.code() + ":"  + response.message()/*ErrorUtils.parseError(response.code(), response.message())*/, response.body())
            }
        }
    }

    fun cancel(){
        if(::call.isInitialized){
            call.cancel()
        }
    }
}