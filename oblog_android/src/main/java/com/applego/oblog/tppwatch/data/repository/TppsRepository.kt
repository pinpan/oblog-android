package com.applego.oblog.tppwatch.data.repository

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.ListResponse
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse

/**
 * Interface to the data layer.
 */
interface TppsRepository {

    suspend fun fetchTppsPageFromRemoteDatasource(paging: Paging): Result<TppsListResponse>

    suspend fun loadTppsFromLocalDatasource(): Result<List<Tpp>>

    suspend fun loadTppsFromLocalDatasource(orderBy: String, isAsc: Boolean): Result<List<Tpp>>

    suspend fun getAllTpps(forceUpdate: Boolean = false): Result<List<Tpp>>

    suspend fun getTpp(tppId: String, forceUpdate: Boolean = false): Result<Tpp>

    suspend fun getTppBlocking(tppId: String, forceUpdate: Boolean = false): Result<Tpp>

    suspend fun refreshTpp(tpp: Tpp)

    suspend fun saveTpp(tpp: Tpp)

    suspend fun setTppFollowedFlag(tpp: Tpp, followed: Boolean)

    //suspend fun setTppActivateFlag(tpp: Tpp, used: Boolean)

    suspend fun deleteAllTpps()

    suspend fun deleteTpp(tppId: String)

    suspend fun saveApp(tpp: Tpp, app: App)

    suspend fun deleteApp(app: App)

    suspend fun updateApp(tpp: Tpp, app: App)

    suspend fun updateLocalDataSource(tpp: Tpp)

    suspend fun updateLocalDataSource(tpps: List<Tpp>?)
}
