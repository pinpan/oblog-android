package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.source.local.Tpp

interface RemoteTppDataSource {
    suspend fun getTpps(): Result<List<Tpp>>

    suspend fun getTpp(tppId: String): Result<Tpp>
}
