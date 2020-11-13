package com.applego.oblog.tppwatch

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.ListResponse
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource

class FakeFailingTppsRemoteDataSource<T> () : RemoteTppDataSource<T> {

    override suspend fun getAllEntities(): Result<ListResponse<T>> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getEntitiesPage(paging: Paging): Result<ListResponse<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun getEntityById(country: String, tppId: String): Result<T> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getEntityByName(country: String, tppName: String): Result<List<T>> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getEntityByNameExact(country: String, tppName: String, tppId: String): Result<T> {
        TODO("Not yet implemented")
    }
}
