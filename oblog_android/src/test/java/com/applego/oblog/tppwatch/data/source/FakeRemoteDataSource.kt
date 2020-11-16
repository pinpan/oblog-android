package com.applego.oblog.tppwatch.data.source

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.Result.Error
import com.applego.oblog.tppwatch.data.Result.Success
import com.applego.oblog.tppwatch.data.source.remote.ListResponse
import com.applego.oblog.tppwatch.data.source.remote.Paging
import com.applego.oblog.tppwatch.data.source.remote.RemoteTppDataSource
import java.util.*

class FakeRemoteDataSource<T> () : RemoteTppDataSource<T> {

    constructor(aListResponse: ListResponse<T>) : this() {
        listResponse = aListResponse
    }

    var listResponse = ListResponse<T>(mutableListOf())

    override suspend fun getAllEntities(): Result<ListResponse<T>> {
        listResponse?.let { return Success(it) }
        return Error(
            Exception("Tpps not found")
        )
    }

    override suspend fun getEntitiesPage(paging: Paging): Result<ListResponse<T>> {
        listResponse.paging.page += 1
        listResponse.paging.last  = true

        listResponse?.let { return Success(it) }
        return Error(
                Exception("Tpps not found")
        )
    }

    override suspend fun getEntityById(country: String, tppId: String): Result<T> {
        // #TODO: Get from a MAP the Country Specific EntityCodeSchema. For CZ it is stripping the parts of the EBA Entitiy Code which
        listResponse?.aList?.firstOrNull ().let {return Success(it!!)} // { it.getCountry().equals(country) && it.getEntityId().equals(tppId)}?.let { return Success(it) }
        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun getEntityByName(country: String, tppName: String): Result<List<T>> {
        listResponse?.aList?.let {return Success(it!!)} //{ it.getEntityName() == tppName}?.let { return Success(listOf(it)) }

        return Error(
            Exception("Tpp not found")
        )
    }

    override suspend fun getEntityByNameExact(country: String, tppName: String, tppId: String): Result<T> {
        // TODO: Use tppId to filter
        val tpp: Optional<T> = listResponse.aList.stream()./*filter { it.getEntityId().equals(tppId) }*/findFirst()
        if (tpp.isPresent) {
            return Success(tpp.get())
        }
        //tppsListResponse.tppsList.stream().findFirst()?.let {return Success(it.get()) }  //filter(item -> item.getEntityId().equals(tppId)).

        return Error(Exception("Tpp not found"))
    }
}
