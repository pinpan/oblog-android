package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.Result

interface RemoteTppDataSource<T> {

    suspend fun getAllEntities(): Result<ListResponse<T>>

    suspend fun getEntitiesPage(paging : Paging): Result<ListResponse<T>>

    suspend fun getEntityByName(country: String, tppName: String): Result<List<T>>

    suspend fun getEntityByNameExact(country: String, tppName: String, tppId: String): Result<T>

    suspend fun getEntityById(country: String, tppId: String): Result<T>
}
