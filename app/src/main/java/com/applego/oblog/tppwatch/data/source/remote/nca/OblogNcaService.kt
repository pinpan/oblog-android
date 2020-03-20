package com.applego.oblog.tppwatch.data.source.remote.nca;

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header
import retrofit2.http.Path;
import retrofit2.http.Query

interface OblogNcaService {

    // #TODO@PZA: Refactor to make it common for all Services
    companion object EbaService : SharedPreferences.OnSharedPreferenceChangeListener {
        val HTTP_CONTEXT = "/api/nca-registry/"
        var currentEnv = "TEST"

        fun create(context: Context): OblogNcaService {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

            val currentEnv = sharedPreferences.getString("environment", "TEST")
            val envsBaseUrls : Array<String> = context.applicationContext.resources.getStringArray(com.applego.oblog.tppwatch.R.array.env_base_url);
            var baseUrl = OblogRestClient.getBaseUrl(currentEnv, envsBaseUrls)

            val retrofit = OblogRestClient.createRetrofit(baseUrl, HTTP_CONTEXT)
            val oblogService = retrofit.create(OblogNcaService::class.java)

            sharedPreferences.registerOnSharedPreferenceChangeListener(this)

            return oblogService
        }

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

    }

    @GET("{country}/{entityId}")
    fun findById(@Header("X-Api-Key") apiKey: String,
                 @Path("country") country: String,
                 @Path("entityId") entityId: String,
                 @Query("page") page: Int? = null,
                 @Query("size") pageSize: Int? = null,
                 @Query("sort") order: String? = null): Call<List<Tpp>>;

    @GET("{country}")
    fun findByName( @Header("X-Api-Key") apiKey: String,
                    @Path("country") country: String,
                    @Query("name") entityName: String,
                    @Query("page") page: Int? = null,
                    @Query("size") pageSize: Int? = null,
                    @Query("sort") order: String? = null): Call<List<Tpp>>;

    @GET("{country}")
    fun listTpps(
                 @Header("X-Api-Key") apiKey: String,
                 @Path("country") country: String,
                 @Query("name") tppName: String,
                 @Query("page") page: Int? = null,
                 @Query("size") pageSize: Int? = null,
                 @Query("sort") order: String? = null): Call<TppsListResponse>;

    // #TODO-EKO: Not implemented on back-end
    @GET("{country}")
    fun filterTpps(
                 @Header("X-Api-Key") apiKey: String,
                 @Path("country") country: String,
                 @Query("filter") tppName: String,
                 @Query("page") page: Int? = null,
                 @Query("size") pageSize: Int? = null,
                 @Query("sort") order: String? = null): Call<TppsListResponse>;

}
