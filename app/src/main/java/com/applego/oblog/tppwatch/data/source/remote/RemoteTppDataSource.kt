package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.data.source.remote.eba.TppsListResponse

interface RemoteTppDataSource {

    suspend fun getAllTpps(): Result<TppsListResponse>

    suspend fun filterTpps(filter: TppFilter): Result<TppsListResponse>

    suspend fun getTpp(tppId: String): Result<Tpp>
}
