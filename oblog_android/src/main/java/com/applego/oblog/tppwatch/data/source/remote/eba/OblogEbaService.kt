package com.applego.oblog.tppwatch.data.source.remote.eba;

import android.content.Context
import androidx.preference.PreferenceManager
import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.BuildConfig
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.OblogRestClient
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import com.applego.oblog.tppwatch.data.source.remote.serializer.*
import com.applego.oblog.tppwatch.util.ResourcesUtils
import com.applego.oblog.tppwatch.util.RetrofitTypes
import com.google.gson.JsonDeserializer

import retrofit2.Call;
import retrofit2.http.*
import java.lang.reflect.Type


interface  OblogEbaService {
    //var BASE_URL = "http://192.168.0.15:8585/eba-registry/" //api.oblog.org:8443  10.0.2.2
    //var API_KEY = "2e65127e909e178d0af311a81f39948c"

    companion object theEbaService {

        val HTTTP_CONTEXT = "/api/eba-registry/"
        val deserializersMap = HashMap<Type, JsonDeserializer<*>>()
        lateinit var theApiKey : ApiKey

        fun create(context: Context): OblogEbaService {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) //Environment.getDataDirectory()
            var selectedEnvironmentName = sharedPreferences?.getString("RUNTIME_ENV","Prod") ?: "Prod"

            var actualEnvironment = ResourcesUtils.getActualEnvironmentForActivity(context, selectedEnvironmentName)
            if (actualEnvironment.isNullOrEmpty()) {
                selectedEnvironmentName = "Dev"
                sharedPreferences.edit().putString("RUNTIME_ENV", selectedEnvironmentName).commit()
                actualEnvironment = ResourcesUtils.getActualEnvironmentForActivity(context,  selectedEnvironmentName)
            }

            var baseUrl = OblogRestClient.getBaseUrl(actualEnvironment[1])
            theApiKey = getApiKey(actualEnvironment[0]) //"T11NOL41x0L7Cn4OAc1FNQogHAcpWvQA")

            //deserializersMap.put(RetrofitTypes.ebaEntityType, EbaEntityDeserializer())
            //deserializersMap.put(RetrofitTypes.ebaEntityListType, EbaEntitiesListDeserializer())
            //deserializersMap.put(RetrofitTypes.ebaEntityListResponseType, EbaEntitiesListResponseDeserializer())
            deserializersMap.put(RetrofitTypes.tppServiceType, TppServiceDeserializer())

            // TODO: Consider DeserializerFactory per environment to support flowless switching between environments
            deserializersMap.put(RetrofitTypes.tppType, TppDeserializer())
            deserializersMap.put(RetrofitTypes.tppListType, TppListDeserializer())
            deserializersMap.put(RetrofitTypes.tppsListResponseType, TppsListResponseDeserializer())

            val retrofit = OblogRestClient.createRetrofitChecking(baseUrl + HTTTP_CONTEXT, deserializersMap)
            val oblogService = retrofit.create(OblogEbaService::class.java)

            return oblogService
        }

        private fun getApiKey(envName: String): ApiKey {
            BuildConfig::class.java.declaredFields.forEach {
                if (java.lang.reflect.Modifier.isStatic(it.modifiers)) {
                    if (it.name.equals("API_KEY_" + envName.toUpperCase())) {
                        return ApiKey(it.get(null) as String)
                    }
                }
            }
            throw  SecurityException("API-KEY not found for environment: " + envName)
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
