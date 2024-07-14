package com.sanisamoj.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object IpInfoApi {
    private const val BASE_URL_IP_INFO_API = "https://ipinfo.io/"
    private val retrofitCep = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL_IP_INFO_API)
        .build()

    val retrofitIpService: IpInfoApiService by lazy {
        retrofitCep.create(IpInfoApiService::class.java)
    }
}