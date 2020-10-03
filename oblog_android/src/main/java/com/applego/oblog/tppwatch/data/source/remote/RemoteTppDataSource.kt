package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.Tpp

interface RemoteTppDataSource {

    suspend fun getAllTpps(): Result<TppsListResponse>

    suspend fun getTpps(paging : Paging): Result<TppsListResponse>

    suspend fun getTppByName(country: String, tppName: String): Result<Tpp>

    suspend fun getTppById(country: String, tppId: String): Result<Tpp>
}
