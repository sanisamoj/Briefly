package com.sanisamoj.api.bot

import com.sanisamoj.data.models.dataclass.LoginRequest
import com.sanisamoj.data.models.dataclass.MessageToSend
import com.sanisamoj.data.models.dataclass.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BotApiService {
    @POST("admin")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): TokenResponse

    @POST("bot/{id}/message")
    suspend fun sendMessage(
        @Path("id") botId: String,
        @Body message: MessageToSend,
        @Header("Authorization") token: String
    )
}