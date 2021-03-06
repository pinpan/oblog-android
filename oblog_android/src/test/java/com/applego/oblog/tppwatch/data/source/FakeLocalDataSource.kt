
package com.applego.oblog.tppwatch.data.source

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.google.common.collect.Lists

class FakeLocalDataSource(var tpps: MutableList<Tpp>? = mutableListOf()) : LocalTppDataSource {

    override suspend fun saveАpp(аpp: App) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getTpps(orderBy: String, isASc: Boolean): Result<List<Tpp>> {
        tpps?.let { return Success(Lists.newArrayList(it)) }
        return Error(
                Exception("Tpps not found")
        )
    }

    override suspend fun deleteАpp(аpp: App) {
        TODO("Not yet implemented")
    }

    override suspend fun getTpps(): Result<List<Tpp>> {
        tpps?.let { return Success(Lists.newArrayList(it)) }
        return Error(
            Exception("Tpps not found")
        )
    }

    override /*suspend */fun getTpp(tppId: String): Result<Tpp> {
        tpps?.firstOrNull { it.getEntityId() == tppId }?.let { return Success(it) }
        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun saveTpp(tpp: Tpp) {
        if (findTpp(tpp) == null) {
            tpps?.add(tpp)
        }
    }

    override suspend fun updateFollowing(tpp: Tpp, follow: Boolean) {
        tpps?.firstOrNull { it.getEntityId() == tpp.getEntityId() }?.let { it.setFollowed(true)}
    }

    /*override suspend fun setTppActivateFlag(tppId: String, used: Boolean) {
        tpps?.firstOrNull { it.getId() == tppId }?.let { it.setUsed(used)}
    }*/

    override suspend fun deleteAllTpps() {
        tpps?.clear()
    }

    override suspend fun deleteTpp(tppId: String) {
        tpps?.removeIf { it.getId() == tppId }
    }

    private fun findTpp(tpp: Tpp) : Tpp? {
        //var tpp: Tpp? = null
        tpps?.forEach {aTpp ->
            if (aTpp.equals(tpp)) {
                return tpp
            }
        }
        return null
    }
}
