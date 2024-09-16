package com.sanisamoj.api.log

import com.sanisamoj.utils.analyzers.dotEnv
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LogApi {
    private val BASE_URL_LOG_API: String = dotEnv("LOG_URL")
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL_LOG_API)
        .build()

    val retrofitLogService: LogApiService by lazy {
        retrofit.create(LogApiService::class.java)
    }
}