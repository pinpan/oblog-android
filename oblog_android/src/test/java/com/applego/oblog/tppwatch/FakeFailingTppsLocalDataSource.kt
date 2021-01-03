package com.applego.oblog.tppwatch

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource

object FakeFailingTppsLocalDataSource : LocalTppDataSource {
    override suspend fun saveАpp(аpp: App) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getTpps(orderBy: String, isASc: Boolean): Result<List<Tpp>> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun deleteАpp(аpp: App) {
        TODO("Not yet implemented")
    }

    override suspend fun getTpps(): Result<List<Tpp>> {
        return Result.Error(Exception("Test"))
    }

    override /*suspend*/ fun getTpp(tppId: String): Result<Tpp> {
        return Result.Error(Exception("Test"))
    }

    override suspend fun saveTpp(tpp: Tpp) {
        TODO("not implemented")
    }

    override suspend fun updateFollowing(tpp: Tpp, follow: Boolean) {
        TODO("not implemented")
    }

    /*override suspend fun setTppActivateFlag(tppId: String, used: Boolean) {
        TODO("not implemented")
    }*/

    override suspend fun deleteAllTpps() {
        TODO("not implemented")
    }

    override suspend fun deleteTpp(tppId: String) {
        TODO("not implemented")
    }
}
