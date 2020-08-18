package com.applego.oblog.tppwatch.data.repository

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp

/**
 * Interface to the data layer.
 */
interface TppsRepository {

    suspend fun fetchTppsFromRemoteDatasourcePaging(): Result<List<Tpp>>

    suspend fun loadTppsFromLocalDatasource(): Result<List<Tpp>>

    suspend fun getAllTpps(forceUpdate: Boolean = false): Result<List<Tpp>>

    suspend fun getTpp(tppId: String, forceUpdate: Boolean = false): Result<Tpp>

    suspend fun refreshTpp(tpp: Tpp)

    suspend fun saveTpp(tpp: Tpp)

    suspend fun setTppFollowedFlag(tpp: Tpp, followed: Boolean)

    suspend fun setTppActivateFlag(tpp: Tpp, used: Boolean)

    suspend fun deleteAllTpps()

    suspend fun deleteTpp(tppId: String)

    suspend fun saveApp(tpp: Tpp, app: App)

    suspend fun deleteApp(tpp: Tpp, app: App)

    suspend fun updateApp(tpp: Tpp, app: App)

}
