package com.applego.oblog.tppwatch

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse

object FakeFailingTppsRemoteDataSource : RemoteTppDataSource {
    override suspend fun getAllTpps(): Result<TppsListResponse> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getTpps(paging: Paging): Result<TppsListResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getTppById(country: String, tppId: String): Result<Tpp> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getTppByName(country: String, tppName: String): Result<List<Tpp>> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getTppByNameExact(country: String, tppName: String, tppId: String): Result<Tpp> {
        TODO("Not yet implemented")
    }
}
