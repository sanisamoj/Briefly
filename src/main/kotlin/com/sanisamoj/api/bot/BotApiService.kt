package com.sanisamoj.api.bot

import com.sanisamoj.data.models.dataclass.MessageToSend
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BotApiService {
    @POST("bot/{id}/message")
    suspend fun sendMessage(
        @Path("id") botId: String,
        @Body message: MessageToSend,
        @Header("Authorization") token: String
    )
}