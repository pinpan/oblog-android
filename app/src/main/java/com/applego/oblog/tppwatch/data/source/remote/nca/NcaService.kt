package com.applego.oblog.tppwatch.data.source.remote.nca;

import com.applego.oblog.tppwatch.data.source.local.Tpp;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface NcaService {
    @GET("tpps/{tpp}/repos")
    fun listTpps(@Path("tpp") tpp: String ): Call<List<Tpp>> ;
}
