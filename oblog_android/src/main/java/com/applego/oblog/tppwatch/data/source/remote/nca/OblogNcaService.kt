package com.applego.oblog.tppwatch.data.source.remote.nca;

import android.content.Context
import androidx.preference.PreferenceManager
import com.applego.oblog.apikey.ApiKey
import com.applego.oblog.tppwatch.BuildConfig
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.source.remote.NcaEntitiesListResponse
import com.applego.oblog.tppwatch.data.source.remote.OblogRestClient
import com.applego.oblog.tppwatch.data.source.remote.serializer.NcaEntitiesListDeserializer
import com.applego.oblog.tppwatch.data.source.remote.serializer.NcaEntitiesListResponseDeserializer
import com.applego.oblog.tppwatch.data.source.remote.serializer.NcaEntityDeserializer
import com.applego.oblog.tppwatch.util.ResourcesUtils
import com.applego.oblog.tppwatch.util.RetrofitTypes
import com.google.gson.JsonDeserializer

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header
import retrofit2.http.Path;
import retrofit2.http.Query
import java.lang.reflect.Type

interface OblogNcaService {

    companion object theNcaService {
        val HTTP_CONTEXT = "/api/nca-registry/"

        val deserializersMap = HashMap<Type, JsonDeserializer<*>>()

        lateinit var theApiKey : ApiKey

        fun create(context: Context): OblogNcaService {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) //Environment.getDataDirectory()
            var selectedEnvironmentName = sharedPreferences?.getString("RUNTIME_ENV","Prod") ?: "Prod"
            var actualEnvironment = ResourcesUtils.getActualEnvironmentForActivity(context, selectedEnvironmentName)
            if (actualEnvironment.isNullOrEmpty()) {
                selectedEnvironmentName = "Prod"
                sharedPreferences.edit().putString("RUNTIME_ENV", selectedEnvironmentName).commit()
                actualEnvironment = ResourcesUtils.getActualEnvironmentForActivity(context, selectedEnvironmentName)
            }

            var baseUrl = OblogRestClient.getBaseUrl(actualEnvironment[1])
            theApiKey = OblogNcaService.getApiKey(actualEnvironment[0]) //"T11NOL41x0L7Cn4OAc1FNQogHAcpWvQA")

            deserializersMap.put(RetrofitTypes.ncaEntityType, NcaEntityDeserializer())
            deserializersMap.put(RetrofitTypes.ncaEntityListType, NcaEntitiesListDeserializer())
            deserializersMap.put(RetrofitTypes.ncaEntityListResponseType, NcaEntitiesListResponseDeserializer())

            val retrofit = OblogRestClient.createRetrofitChecking(baseUrl + HTTP_CONTEXT, deserializersMap)
            val oblogService = retrofit.create(OblogNcaService::class.java)

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

    @GET("{country}/{entityId}")
    fun findById(@Header("X-Api-Key") apiKey: String,
                 @Path("country") country: String,
                 @Path("entityId") entityId: String,
                 @Query("page") page: Int? = null,
                 @Query("size") pageSize: Int? = null,
                 @Query("sort") order: String? = null): Call<NcaEntitiesListResponse>;

    @GET("{country}")
    fun findByName( @Header("X-Api-Key") apiKey: String,
                    @Path("country") country: String,
                    @Query("name") entityName: String,
                    @Query("page") page: Int? = null,
                    @Query("size") pageSize: Int? = null,
                    @Query("sort") order: String? = null): Call<List<NcaEntity>>;

    @GET("{country}")
    fun listTpps(
                 @Header("X-Api-Key") apiKey: String,
                 @Path("country") country: String,
                 @Query("name") tppName: String,
                 @Query("page") page: Int? = null,
                 @Query("size") pageSize: Int? = null,
                 @Query("sort") order: String? = null): Call<NcaEntitiesListResponse>;

    // #TODO-EKO: Not implemented on back-end
    @GET("{country}")
    fun filterTpps(
                 @Header("X-Api-Key") apiKey: String,
                 @Path("country") country: String,
                 @Query("filter") tppName: String,
                 @Query("page") page: Int? = null,
                 @Query("size") pageSize: Int? = null,
                 @Query("sort") order: String? = null): Call<NcaEntitiesListResponse>;
}
