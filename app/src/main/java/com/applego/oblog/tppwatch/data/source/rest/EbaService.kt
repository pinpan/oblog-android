package com.applego.oblog.tppwatch.data.source.rest;

import com.applego.oblog.tppwatch.data.Tpp;
import com.applego.oblog.tppwatch.data.App

import retrofit2.Call;
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Converter
import java.lang.reflect.Type
import java.text.DateFormat


interface  EbaService {

    private fun createGsonConverter(type: Type, typeAdapter: Any): Converter.Factory {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(type, typeAdapter)
        val gson = gsonBuilder.create()

        return GsonConverterFactory.create(gson)
    }

    companion object {

        var BASE_URL = "http://192.168.0.15:8585/eba-registry/" //api.oblog.org:8443  10.0.2.2
        //var API_KEY = "2e65127e909e178d0af311a81f39948c"
        val tppType: Type = object : TypeToken<Tpp>() {}.type
        val tppListType: Type = object : TypeToken<MutableList<Tpp>>() {}.type //@JvmSuppressWildcards

        var gson = GsonBuilder()
                .registerTypeAdapter(tppType, TppDeserializer())
                .registerTypeAdapter(tppListType, TppListDeserializer())
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create()


        fun create(): EbaService {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create(gson))
                    .baseUrl(BASE_URL)
                    .build()

            return retrofit.create(EbaService::class.java)
        }
    }

    //var disposable: Disposable? = null

    @GET("tpps/")
    fun getTpps(): Call<List<Tpp>>

    @GET("tpps/")
    fun getTppById(@Query("id") id: Int?): Call<Tpp>

    @GET("tpps/")
    fun listTpps(@Query("name")tppName: String ): Call<List<Tpp>>;

    @GET("tpps/")
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
