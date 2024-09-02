package com.sanisamoj.api.bot

import com.sanisamoj.utils.analyzers.dotEnv
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BotApi {
    private val BASE_URL_BOT_API: String = dotEnv("BOT_URL")
    private val retrofitCep = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL_BOT_API)
        .build()

    val retrofitBotService: BotApiService by lazy {
        retrofitCep.create(BotApiService::class.java)
    }
}
