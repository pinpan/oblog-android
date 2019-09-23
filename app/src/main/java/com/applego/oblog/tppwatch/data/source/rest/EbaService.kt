package com.applego.oblog.tppwatch.data.source.rest;

import com.applego.oblog.tppwatch.data.Tpp;
import com.applego.oblog.tppwatch.data.App
import io.reactivex.disposables.Disposable

import retrofit2.Call;
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface  EbaService {

    companion object {

        var BASE_URL = "https://api.oblog.org:8443/eba-registry"
        //var API_KEY = "2e65127e909e178d0af311a81f39948c"

        fun create(): EbaService {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()

            return retrofit.create(EbaService::class.java)
        }
    }

    //var disposable: Disposable? = null

    @GET("eba-registry")
    fun getTpps(): Call<List<Tpp>>

    @GET("tpps")
    fun getTppById(@Query("id") id: Int?): Call<Tpp>

    @GET("tpps")
    fun listTpps(@Query("tppName")tppName: String ): Call<List<Tpp>>;

    @GET("tpps")
    fun listTpps(@Query("country")country: String, @Query("services") services: String): Call<List<Tpp>>;

    @GET("tpps/{tppId}/apps")
    fun listTppApps(@Path("tppId")tppId: String ): Call<List<App>>;

    @PUT("tpps/{id}")
    fun updateTpp(@Body tpp : Tpp) : Call<Tpp>


    abstract fun insertTpp(tpp: Tpp): Unit
    fun updateCompleted(id: String, b: Boolean): Unit

    fun deleteCompletedTpps ()

    fun deleteTpps(): Unit

    fun deleteTppById(tppId: String)
}
