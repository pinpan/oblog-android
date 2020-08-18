package com.applego.oblog.tppwatch.data.source.remote;

import com.applego.oblog.tppwatch.BuildConfig
import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.apikey.ApiKeyDeserializer

import retrofit2.Call;
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.DateFormat
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


interface  OblogApiKeyService {
    companion object {

        val apiKeyType: Type = object : TypeToken<ApiKey>() {}.type

        var gson = GsonBuilder()
                .registerTypeAdapter(apiKeyType, ApiKeyDeserializer())
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create()


        fun create(): OblogApiKeyService {
            val okHttpClient = OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create(gson))
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(okHttpClient)
                    .build()

            return retrofit.create(OblogApiKeyService::class.java)
        }
    }

    @POST("/api-key")
    fun createApiKey() : Call<ApiKey>

    @DELETE("/api-key")
    fun revokeApiKey() : Call<ApiKey>

    @PUT("/api-key")
    fun updateApiKey() : Call<ApiKey>

}
