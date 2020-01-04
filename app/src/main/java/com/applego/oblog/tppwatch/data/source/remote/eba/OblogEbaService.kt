package com.applego.oblog.tppwatch.data.source.remote.eba;

import com.applego.oblog.tppwatch.BuildConfig
import com.applego.oblog.tppwatch.data.source.local.Tpp;
import com.applego.oblog.tppwatch.data.source.local.App

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
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


interface  OblogEbaService {

    /*private fun createGsonConverter(type: Type, typeAdapter: Any): Converter.Factory {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(type, typeAdapter)
        val gson = gsonBuilder.create()

        return GsonConverterFactory.create(gson)
    }
*/
    companion object {

        //var BASE_URL = "http://192.168.0.15:8585/eba-registry/" //api.oblog.org:8443  10.0.2.2
        //var API_KEY = "2e65127e909e178d0af311a81f39948c"
        val tppType: Type = object : TypeToken<Tpp>() {}.type
        val tppListType: Type = object : TypeToken<MutableList<Tpp>>() {}.type //@JvmSuppressWildcards
        val tppsListResponseType: Type = object : TypeToken<TppsListResponse>() {}.type //@JvmSuppressWildcards

        var gson = GsonBuilder()
                .registerTypeAdapter(tppType, TppDeserializer())
                .registerTypeAdapter(tppListType, TppListDeserializer())
                .registerTypeAdapter(tppsListResponseType, TppsListResponseDeserializer())
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create()


        fun create(): OblogEbaService {
            val okHttpClient = OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create(gson))
                    .baseUrl(BuildConfig.BASE_URL) //context.getString(R.oblog_api_base_url))
                    .client(okHttpClient)
                    .build()

            return retrofit.create(OblogEbaService::class.java)
        }
    }

    @GET("local/tpps/")
    fun getTppById(@Query("id") id: Int?): Call<Tpp>

    @GET("import/")
    fun listTppsByName(): Call<Unit>;

    @GET("local/tpps/")
    fun listAllTpps(@Query("page") page: Int? = null,
                    @Query("size") pageSize: Int? = null,
                    @Query("sort") order: String? = null): Call<TppsListResponse>;

    @GET("local/tpps/")
    fun listTppsByName(@Query("name") tppName: String,
                       @Query("page") page: Int? = null,
                       @Query("size") pageSize: Int? = null,
                       @Query("sort") order: String? = null): Call<TppsListResponse>;

    @GET("local/tpps/")
    fun listTppsByName(@Query("country")country: String, @Query("services") services: String): Call<List<Tpp>>;

    @GET("tpps/{tppId}/apps")
    fun listTppApps(@Path("tppId")tppId: String ): Call<List<App>>;

    @PUT("tpps/{id}")
    fun updateTpp(@Body tpp : Tpp) : Call<Tpp>


    abstract fun insertTpp(tpp: Tpp): Unit

    fun updateUnfollowed(id: String, b: Boolean): Unit

    fun deleteFollowedTpps ()

    fun deleteTpps(): Unit

    fun deleteTppById(tppId: String)
}
