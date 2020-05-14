package com.applego.oblog.tppwatch.data.source.local

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.model.App

/**
 * Main entry point for accessing tpps data.
 */
interface LocalTppDataSource {

    suspend fun getTpps(filter: TppsFilter): Result<List<Tpp>>

    suspend fun getTpp(tppId: String): Result<Tpp>

    suspend fun saveTpp(tpp: Tpp)

    suspend fun udateFollowing(tpp: Tpp, follow: Boolean)

    suspend fun setTppActivateFlag(tppId: String, used: Boolean)

    suspend fun deleteAllTpps()

    suspend fun deleteTpp(tppId: String)

    suspend fun saveАpp(аpp: App)

}
