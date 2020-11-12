package com.applego.oblog.tppwatch.data.source.remote.eba;

import android.content.Context
import androidx.preference.PreferenceManager
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.EbaEntitiesListResponse
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import com.applego.oblog.tppwatch.data.source.remote.OblogRestClient
import com.applego.oblog.tppwatch.util.ResourcesUtils

import retrofit2.Call;
import retrofit2.http.*


interface  OblogEbaService {
    //var BASE_URL = "http://192.168.0.15:8585/eba-registry/" //api.oblog.org:8443  10.0.2.2
    //var API_KEY = "2e65127e909e178d0af311a81f39948c"

    companion object EbaService {

        val HTTTP_CONTEXT = "/api/eba-registry/"

        fun create(context: Context): OblogEbaService {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) //Environment.getDataDirectory()
            val selectedEnvironmentName = sharedPreferences?.getString("RUNTIME_ENV","TEST") ?: "TEST"

            val actualEnvironment = ResourcesUtils.getActualEnvironmentForActivity(context, selectedEnvironmentName)

            var baseUrl = OblogRestClient.getBaseUrl(actualEnvironment[1])
            val retrofit = OblogRestClient.createRetrofitChecking(baseUrl, HTTTP_CONTEXT)
            val oblogService = retrofit.create(OblogEbaService::class.java)

            return oblogService
        }
    }

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
    fun listEbaEntitiesByName(@Header("X-Api-Key") apiKey: String,
                       @Query("name") tppName: String,
                       @Query("page") page: Int? = null,
                       @Query("size") pageSize: Int? = null,
                       @Query("sort") order: String? = null): Call<EbaEntitiesListResponse>;

    @GET("tpps/local/")
    fun listTppsByName(@Query("country")country: String, @Query("services") services: String): Call<List<Tpp>>;

    @GET("tpps/local/{tppId}/apps")
    fun listTppApps(@Path("tppId")tppId: String ): Call<List<App>>;

    @PUT("tpps/local/{id}")
    fun updateTpp(@Body tpp : Tpp) : Call<Tpp>


    fun insertTpp(tpp: Tpp): Unit

    fun updateUnfollowed(id: String, b: Boolean): Unit
}
