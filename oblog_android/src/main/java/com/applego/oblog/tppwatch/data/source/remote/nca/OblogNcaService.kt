package com.applego.oblog.tppwatch.data.source.remote.nca;

import android.content.Context
import androidx.preference.PreferenceManager
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.OblogRestClient
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import com.applego.oblog.tppwatch.util.ResourcesUtils

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header
import retrofit2.http.Path;
import retrofit2.http.Query

interface OblogNcaService {

    // #TODO@PZA: Refactor to make it common for all Services
    companion object EbaService {
        val HTTP_CONTEXT = "/api/nca-registry/"

        fun create(context: Context): OblogNcaService {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) //Environment.getDataDirectory()
            val selectedEnvironmentName = sharedPreferences?.getString("RUNTIME_ENV","TEST") ?: "TEST"
            val actualEnvironment = ResourcesUtils.getActualEnvironmentForActivity(context, selectedEnvironmentName)

            var baseUrl = OblogRestClient.getBaseUrl(actualEnvironment[1])
            val retrofit = OblogRestClient.createRetrofitChecking(baseUrl, OblogNcaService.HTTP_CONTEXT)
            val oblogService = retrofit.create(OblogNcaService::class.java)

            return oblogService
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
