package com.applego.oblog.tppwatch.data.source

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import java.util.*

class FakeRemoteDataSource(var tppsListResponse: TppsListResponse = TppsListResponse(mutableListOf())) : RemoteTppDataSource {
    override suspend fun getAllTpps(): Result<TppsListResponse> {
        tppsListResponse?.let { return Success(it) }
        return Error(
            Exception("Tpps not found")
        )
    }

    override suspend fun getTpps(paging: Paging): Result<TppsListResponse> {
        tppsListResponse.paging.page += 1
        tppsListResponse.paging.last  = true

        tppsListResponse?.let { return Success(it) }
        return Error(
                Exception("Tpps not found")
        )
    }

    override suspend fun getTppById(country: String, tppId: String): Result<Tpp> {
        // #TODO: Get from a MAP the Country Specific EntityCodeSchema. For CZ it is stripping the parts of the EBA Entitiy Code which
        tppsListResponse?.tppsList?.firstOrNull { it.getCountry().equals(country) && it.getEntityId().equals(tppId)}?.let { return Success(it) }
        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun getTppByName(country: String, tppName: String): Result<List<Tpp>> {
        tppsListResponse?.tppsList?.firstOrNull { it.getEntityName() == tppName}?.let { return Success(listOf(it)) }

        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun getTppByNameExact(country: String, tppName: String, tppId: String): Result<Tpp> {
        // TODO: Use tppId to filter
        val tpp: Optional<Tpp> = tppsListResponse.tppsList.stream().filter { it.getEntityId().equals(tppId) }.findFirst()
        if (tpp.isPresent) {
            return Result.Success(tpp.get())
        }
        //tppsListResponse.tppsList.stream().findFirst()?.let {return Success(it.get()) }  //filter(item -> item.getEntityId().equals(tppId)).

        return Error(Exception("Tpp not found"))
    }
}
