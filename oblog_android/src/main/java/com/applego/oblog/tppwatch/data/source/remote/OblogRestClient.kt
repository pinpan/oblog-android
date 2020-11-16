package com.applego.oblog.tppwatch.data.source.remote

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.DateFormat
import java.util.concurrent.TimeUnit


class OblogRestClient {

    companion object {
        fun getBaseUrl(baseUrlFromConfig: String): String {
            val result = baseUrlFromConfig.removePrefix("\"").removeSuffix("\"")
            return result
        }

        fun createRetrofit(convertorsMap: Map<Type, JsonDeserializer<*>>) : Gson {
            val builder = GsonBuilder()

            //convertorsMap.entries.stream().filter{e -> (e.value is JsonDeserializer<OblogEntity>::class.java)}.map { e -> Map.Entry(e.key, e.value)}
            convertorsMap.forEach() {
                //if (it.value is JsonDeserializer<*>) {
                    builder.registerTypeAdapter(it.key, it.value)
                //}
            }

            builder.enableComplexMapKeySerialization()
                   .serializeNulls()
                   .setDateFormat(DateFormat.LONG)
                   .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                   .setPrettyPrinting()
                   .setVersion(1.0)

            return builder.create()
        }


        fun createRetrofitChecking(baseUrl: String, convertorsMap: Map<Type, JsonDeserializer<*>>) : Retrofit {

            val logging = HttpLoggingInterceptor()
// set your desired log level
// set your desired log level
            logging.level = HttpLoggingInterceptor.Level.BODY

            //val httpClient = OkHttpClient.Builder()
// add your other interceptors …
// add logging as last interceptor
// add your other interceptors …
// add logging as last interceptor
            //httpClient.addInterceptor(logging) // <-- this is the important line!

            val okHttpClient = /*OblogRestClient.*/OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build()

            val retrofit: Retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create(createRetrofit(convertorsMap)))
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .build()

            return retrofit
        }

/*

        return
                .registerTypeAdapter(RetrofitTypes.ebaEntityType, EbaEntityDeserializer())
                .registerTypeAdapter(RetrofitTypes.ebaEntityListType, EbaEntitiesListDeserializer())
                .registerTypeAdapter(RetrofitTypes.ebaEntityListResponseType, EbaEntitiesListResponseDeserializer())

                .registerTypeAdapter(RetrofitTypes.ncaEntityType, NcaEntityDeserializer())
                .registerTypeAdapter(RetrofitTypes.ncaEntityListType, NcaEntitiesListDeserializer())
                .registerTypeAdapter(RetrofitTypes.ncaEntityListResponseType, NcaEntitiesListResponseDeserializer())
                // TODO: Remove TppDeserializers and use EbaDeserializers. Separate serializers registration for EBA and NCA
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
*/
    }
}