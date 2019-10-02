package com.applego.oblog.tppwatch.data.source.rest;

import androidx.core.app.ServiceCompat;

import com.applego.oblog.tppwatch.data.Tpp;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface NcaService {
    @GET("tpps/{tpp}/repos")
    fun listTpps(@Path("tpp") tpp: String ): Call<List<Tpp>> ;
}
