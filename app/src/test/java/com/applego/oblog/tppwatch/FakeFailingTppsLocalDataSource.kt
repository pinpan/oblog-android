package com.applego.oblog.tppwatch

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource

object FakeFailingTppsLocalDataSource : LocalTppDataSource {
    override suspend fun getTpps(filter: TppsFilter): Result<List<Tpp>> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun saveTpp(tpp: Tpp) {
        TODO("not implemented")
    }

    override suspend fun udateFollowing(tpp: Tpp, follow: Boolean) {
        TODO("not implemented")
    }

    override suspend fun setTppActivateFlag(tppId: String, active: Boolean) {
        TODO("not implemented")
    }

    override suspend fun deleteAllTpps() {
        TODO("not implemented")
    }

    override suspend fun deleteTpp(tppId: String) {
        TODO("not implemented")
    }
}
