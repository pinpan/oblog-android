package com.applego.oblog.tppwatch.data.source.remote.eba;

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.applego.oblog.tppwatch.data.source.local.*

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


interface  OblogEbaService  {

    companion object EbaService : SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key.equals("environment")) {
                var actualEnvironment = sharedPreferences?.getString("environment","")
                if (actualEnvironment == "Test") {
                    actualEnvironment = "Dev"
                }
            }
        }

        //var BASE_URL = "http://192.168.0.15:8585/eba-registry/" //api.oblog.org:8443  10.0.2.2
        //var API_KEY = "2e65127e909e178d0af311a81f39948c"
        val EBA_CONTEXT = "/api/eba-registry/"

        var currentEnv = "Dev"
        val tppType: Type = object : TypeToken<Tpp>() {}.type
        val tppListType: Type = object : TypeToken<MutableList<Tpp>>() {}.type
        val tppsListResponseType: Type = object : TypeToken<TppsListResponse>() {}.type
        val ebaPassportListType = object : TypeToken<List<EbaPassport>>() {}.type
        val ebaPassportType: Type = object : TypeToken<EbaPassport>() {}.type
        val tppServiceListType: Type = object : TypeToken<List<Service>>() {}.type
        val tppServiceType: Type = object : TypeToken<Service>() {}.type

        var gson = GsonBuilder()
                .registerTypeAdapter(tppType, TppDeserializer())
                .registerTypeAdapter(tppListType, TppListDeserializer())
                .registerTypeAdapter(tppsListResponseType, TppsListResponseDeserializer())

                /*
                .registerTypeAdapter(ebaPassportListType, EbaPassportListDeserializer())
                .registerTypeAdapter(ebaPassportType, EbaPassportDeserializer())
                .registerTypeAdapter(tppServiceListType, TppServiceListDeserializer())
                */

                .registerTypeAdapter(tppServiceType, TppServiceDeserializer())
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create()

        fun create(context: Context): OblogEbaService {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)

            currentEnv = sharedPreferences.getString("environment", "Dev")

            var baseUrl = ""
            val envsBaseUrls = context.applicationContext.resources.getStringArray(com.applego.oblog.tppwatch.R.array.env_base_url);
            /* This requires API level 21, while we go with 14

            val envsTypedArray = context.applicationContext.resources.obtainTypedArray(com.applego.oblog.tppwatch.R.array.environments)
            if (envsTypedArray != null) {
                for (iL: Int in 0..envsTypedArray.indexCount) {
                    val type = envsTypedArray.getType(iL)
                    Array<String> = envsTypedArray[i]
                }
            }
            */

            if (envsBaseUrls != null) {
                for (i in envsBaseUrls.indices) {
                    val env = envsBaseUrls[i]

                    if (env.startsWith(currentEnv.toUpperCase())) {
                        baseUrl = env.substring(currentEnv.length+1)
                    }
                }
            }

            val okHttpClient = OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create(gson))
                    .baseUrl(baseUrl + EBA_CONTEXT)
                    .client(okHttpClient)
                    .build()

            return retrofit.create(OblogEbaService::class.java)
        }
    }

    @GET("tpps/local/")
    fun getTppById(@Query("id") id: Int?): Call<Tpp>

    @GET("import/")
    fun listTppsByName(): Call<Unit>;

    @GET("tpps/local/")
    fun listAllTpps(@Query("page") page: Int? = null,
                    @Query("size") pageSize: Int? = null,
                    @Query("sort") order: String? = null): Call<TppsListResponse>;

    @GET("tpps/local/")
    fun listTppsByName(@Header("X-Api-Key") apiKey: String,
                       @Query("name") tppName: String,
                       @Query("page") page: Int? = null,
                       @Query("size") pageSize: Int? = null,
                       @Query("sort") order: String? = null): Call<TppsListResponse>;

    @GET("tpps/local/")
    fun listTppsByName(@Query("country")country: String, @Query("services") services: String): Call<List<Tpp>>;

    @GET("tpps/local/{tppId}/apps")
    fun listTppApps(@Path("tppId")tppId: String ): Call<List<App>>;

    @PUT("tpps/local/{id}")
    fun updateTpp(@Body tpp : Tpp) : Call<Tpp>


    abstract fun insertTpp(tpp: Tpp): Unit

    fun updateUnfollowed(id: String, b: Boolean): Unit

    fun deleteFollowedTpps ()

    fun deleteTpps(): Unit

    fun deleteTppById(tppId: String)
}
