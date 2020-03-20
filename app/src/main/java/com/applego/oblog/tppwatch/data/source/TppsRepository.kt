package com.applego.oblog.tppwatch.data.source

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp

/**
 * Interface to the data layer.
 */
interface TppsRepository {

    suspend fun getAllTpps(forceUpdate: Boolean = false): Result<List<Tpp>>

    suspend fun filterTpps(filter: TppsFilter, forceUpdate: Boolean = false): Result<List<Tpp>>

    suspend fun getTpp(tppId: String, forceUpdate: Boolean = false): Result<Tpp>

    suspend fun saveTpp(tpp: Tpp)

    suspend fun setTppFollowedFlag(tpp: Tpp, followed: Boolean)

    suspend fun setTppActivateFlag(tpp: Tpp, active: Boolean)

    suspend fun deleteAllTpps()

    suspend fun deleteTpp(tppId: String)
}
