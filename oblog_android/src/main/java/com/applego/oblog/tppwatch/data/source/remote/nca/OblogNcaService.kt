package com.applego.oblog.tppwatch.data.source.remote.nca;

import android.content.Context
import androidx.preference.PreferenceManager
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

    companion object NcaService {
        val HTTP_CONTEXT = "/api/nca-registry/"

        val deserializersMap = HashMap<Type, JsonDeserializer<*>>()

        fun create(context: Context): OblogNcaService {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) //Environment.getDataDirectory()
            val selectedEnvironmentName = sharedPreferences?.getString("RUNTIME_ENV","TEST") ?: "TEST"
            val actualEnvironment = ResourcesUtils.getActualEnvironmentForActivity(context, selectedEnvironmentName)

            var baseUrl = OblogRestClient.getBaseUrl(actualEnvironment[1])

            deserializersMap.put(RetrofitTypes.ncaEntityType, NcaEntityDeserializer())
            deserializersMap.put(RetrofitTypes.ncaEntityListType, NcaEntitiesListDeserializer())
            deserializersMap.put(RetrofitTypes.ncaEntityListResponseType, NcaEntitiesListResponseDeserializer())

            val retrofit = OblogRestClient.createRetrofitChecking(baseUrl + HTTP_CONTEXT, deserializersMap)
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
