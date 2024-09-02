package com.sanisamoj.api.bot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BotApi {
    private const val BASE_URL_BOT_API = "https://localhost:8585/"
    private val retrofitCep = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL_BOT_API)
        .build()

    val retrofitBotService: BotApiService by lazy {
        retrofitCep.create(BotApiService::class.java)
    }
}
