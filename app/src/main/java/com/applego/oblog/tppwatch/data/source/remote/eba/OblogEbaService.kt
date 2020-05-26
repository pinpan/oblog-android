package com.applego.oblog.tppwatch.data.source.remote.eba;

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import com.applego.oblog.tppwatch.data.source.remote.OblogRestClient

import retrofit2.Call;
import retrofit2.http.*


interface  OblogEbaService {

    companion object EbaService : SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key.equals("environment")) {
                var actualEnvironment = sharedPreferences?.getString("environment","")
                if (actualEnvironment == "Test") {
                    actualEnvironment = "Dev"
                }
            }

            else if (key.equals("psd2")) {

            }
        }
        //var BASE_URL = "http://192.168.0.15:8585/eba-registry/" //api.oblog.org:8443  10.0.2.2
        //var API_KEY = "2e65127e909e178d0af311a81f39948c"

        val HTTTP_CONTEXT = "/api/eba-registry/"

        fun create(context: Context): OblogEbaService {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) //Environment.getDataDirectory()
            val sharedPreferences2 = context.getSharedPreferences("root_preferences", Context.MODE_PRIVATE) //PreferenceManager.getDefaultSharedPreferences(context)

            val currentEnv = sharedPreferences.getString("environment", "TEST")
            val testEnvDefaults =  context.applicationContext.resources.getStringArray(com.applego.oblog.tppwatch.R.array.env_TEST);
            val currentEnv2 = sharedPreferences.all //StringSet("environment", testEnvDefaults.toMutableSet())

            val envsBaseUrls : Array<String> = context.applicationContext.resources.getStringArray(com.applego.oblog.tppwatch.R.array.env_base_url);
            var baseUrl = OblogRestClient.getBaseUrl(currentEnv, envsBaseUrls)

            val retrofit = OblogRestClient.createRetrofitChecking(baseUrl, HTTTP_CONTEXT)
            val oblogEbaService = retrofit.create(OblogEbaService::class.java)

            sharedPreferences.registerOnSharedPreferenceChangeListener(this)

            return oblogEbaService
        }
    }


/*
    @GET("tpps/local/")
    fun getTppEntityByDbId(@Query("id") id: Int?): Call<Tpp>
*/
    @GET("tpps/local/{entityId}")
    fun findById(@Header("X-Api-Key") apiKey: String,
                 @Path("entityId") entityId: String,
                 @Query("page") page: Int? = null,
                 @Query("size") pageSize: Int? = null,
                 @Query("sort") order: String? = null): Call<List<Tpp>>;


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


    fun insertTpp(tpp: Tpp): Unit

    fun updateUnfollowed(id: String, b: Boolean): Unit
}
