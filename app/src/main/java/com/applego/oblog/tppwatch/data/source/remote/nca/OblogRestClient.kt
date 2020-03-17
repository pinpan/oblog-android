package com.applego.oblog.tppwatch.data.source.remote.nca

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

    val okHttpClient = OkHttpClient()

    fun getBaseUrl(currentEnv: String, envsBaseUrls: Array<String>) : String {

        // HACK-HACK-HACK - because the statement above returns the preference ID instead if the value ... some times
        var theEnv: String = if (currentEnv.startsWith("@")) currentEnv else "TEST"


        /* This requires API level 21, while we go with 14

            val envsTypedArray = context.applicationContext.resources.obtainTypedArray(com.applego.oblog.tppwatch.R.array.environments)
            if (envsTypedArray != null) {
                for (iL: Int in 0..envsTypedArray.indexCount) {
                    val type = envsTypedArray.getType(iL)
                    Array<String> = envsTypedArray[i]
                }
            }
        */

        var baseUrl = ""
        if (envsBaseUrls != null) {
            for (i in envsBaseUrls.indices) {
                if (envsBaseUrls[i].startsWith(theEnv.toUpperCase())) {
                    baseUrl = envsBaseUrls[i].substring(theEnv.length+1)
                }
            }
        }

        return baseUrl
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

    fun createRetrofit(baseUrl: String, restContext : String) : Retrofit {

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