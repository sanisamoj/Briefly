package com.sanisamoj.api.ipinfo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object IpInfoApi {
    private const val BASE_URL_IP_INFO_API = "https://ipinfo.io/"
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL_IP_INFO_API)
        .build()

    val retrofitIpService: IpInfoApiService by lazy {
        retrofit.create(IpInfoApiService::class.java)
    }
}