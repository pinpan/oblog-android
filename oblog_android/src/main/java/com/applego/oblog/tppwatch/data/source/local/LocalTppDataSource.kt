package com.applego.oblog.tppwatch.data.source.local

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.model.App

/**
 * Main entry point for accessing tpps data.
 */
interface LocalTppDataSource {

    suspend fun getTpps(orderBy: String, isASc: Boolean): Result<List<Tpp>>

    suspend fun getTpps(): Result<List<Tpp>>

    /*suspend */fun getTpp(tppId: String): Result<Tpp>

    suspend fun saveTpp(tpp: Tpp)

    suspend fun updateFollowing(tpp: Tpp, follow: Boolean)

    //suspend fun setTppActivateFlag(tppId: String, used: Boolean)

    suspend fun deleteAllTpps()

    suspend fun deleteTpp(tppId: String)

    suspend fun saveАpp(аpp: App)

    suspend fun deleteАpp(аpp: App)
}
