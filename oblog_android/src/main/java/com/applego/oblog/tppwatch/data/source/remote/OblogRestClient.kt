package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.source.remote.serializer.TppDeserializer
import com.applego.oblog.tppwatch.data.source.remote.serializer.TppListDeserializer
import com.applego.oblog.tppwatch.data.source.remote.serializer.TppServiceDeserializer
import com.applego.oblog.tppwatch.data.source.remote.serializer.TppsListResponseDeserializer
import com.applego.oblog.tppwatch.util.RetrofitTypes
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.util.concurrent.TimeUnit

object OblogRestClient {

    private val okHttpClient by lazy { OkHttpClient() }

    fun getBaseUrl(baseUrlFromConfig: String) : String {
        val result = baseUrlFromConfig.removePrefix("\"").removeSuffix("\"")
        return result
    }

    fun createRetrofit() : Gson {
        return GsonBuilder()
                .registerTypeAdapter(RetrofitTypes.tppType, TppDeserializer())
                .registerTypeAdapter(RetrofitTypes.tppListType, TppListDeserializer())
                .registerTypeAdapter(RetrofitTypes.tppsListResponseType, TppsListResponseDeserializer())
                .registerTypeAdapter(RetrofitTypes.tppServiceType, TppServiceDeserializer())
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create()
    }

    fun createRetrofitChecking(baseUrl: String, restContext : String) : Retrofit {

        val okHttpClient = OblogRestClient.okHttpClient.newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

        return Retrofit.Builder()
                .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                        GsonConverterFactory.create(createRetrofit()))
                .baseUrl(baseUrl + restContext)
                .client(okHttpClient)
                .build()

    }
}