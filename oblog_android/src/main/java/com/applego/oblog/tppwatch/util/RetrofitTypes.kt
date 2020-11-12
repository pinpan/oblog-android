package com.applego.oblog.tppwatch.util

import com.applego.oblog.tppwatch.data.model.EbaEntity
import com.applego.oblog.tppwatch.data.model.NcaEntity
import com.applego.oblog.tppwatch.data.model.Psd2Service
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.source.remote.EbaEntitiesListResponse
import com.applego.oblog.tppwatch.data.source.remote.NcaEntitiesListResponse
import com.applego.oblog.tppwatch.data.source.remote.TppsListResponse
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object RetrofitTypes {
    val ebaEntityType: Type = object : TypeToken<EbaEntity>() {}.type
    val ebaEntityListType: Type = object : TypeToken<List<EbaEntity>>() {}.type
    val ebaEntityListResponseType: Type = object : TypeToken<EbaEntitiesListResponse>() {}.type

    val ncaEntityType: Type = object : TypeToken<NcaEntity>() {}.type
    val ncaEntityListType: Type = object : TypeToken<List<NcaEntity>>() {}.type
    val ncaEntityListResponseType: Type = object : TypeToken<NcaEntitiesListResponse>() {}.type

    val tppType: Type = object : TypeToken<Tpp>() {}.type
    val tppListType: Type = object : TypeToken<MutableList<Tpp>>() {}.type
    val tppsListResponseType: Type = object : TypeToken<TppsListResponse>() {}.type

    val tppServiceType: Type = object : TypeToken<Psd2Service>() {}.type

    //val ebaPassportListType = object : TypeToken<List<EbaPassport>>() {}.type
    //val ebaPassportType: Type = object : TypeToken<EbaPassport>() {}.type
    //val tppServiceListType: Type = object : TypeToken<List<Psd2Service>>() {}.type
}