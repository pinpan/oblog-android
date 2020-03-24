
package com.applego.oblog.tppwatch.data.source

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.local.LocalTppDataSource
import com.google.common.collect.Lists

class FakeLocalDataSource(var tpps: MutableList<Tpp>? = mutableListOf()) : LocalTppDataSource {
    override suspend fun getTpps(filter: TppsFilter): Result<List<Tpp>> {
        tpps?.let { return Success(Lists.newArrayList(it)) }
        return Error(
            Exception("Tpps not found")
        )
    }

    override suspend fun getTpp(tppId: String): Result<Tpp> {
        tpps?.firstOrNull { it.getEntityId() == tppId }?.let { return Success(it) }
        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun saveTpp(tpp: Tpp) {
        tpps?.add(tpp)
    }

    override suspend fun udateFollowing(tpp: Tpp, follow: Boolean) {
        tpps?.firstOrNull { it.getEntityId() == tpp.getEntityId() }?.let { it.setFollowed(true)}
    }

    override suspend fun setTppActivateFlag(tppId: String, active: Boolean) {
        tpps?.firstOrNull { it.getId() == tppId }?.let { it.setActive(active)}
    }

    override suspend fun deleteAllTpps() {
        tpps?.clear()
    }

    override suspend fun deleteTpp(tppId: String) {
        tpps?.removeIf { it.getId() == tppId }
    }
}
