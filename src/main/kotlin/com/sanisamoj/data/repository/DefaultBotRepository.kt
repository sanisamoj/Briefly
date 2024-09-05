package com.sanisamoj.data.repository

import com.sanisamoj.api.bot.BotApiService
import com.sanisamoj.data.models.dataclass.LoginRequest
import com.sanisamoj.data.models.dataclass.MessageToSend
import com.sanisamoj.data.models.interfaces.BotRepository
import com.sanisamoj.utils.analyzers.dotEnv

class DefaultBotRepository(
    private val botApiService: BotApiService
) : BotRepository {
    private val email: String by lazy { dotEnv("BOT_LOGIN_EMAIL") }
    private val password: String by lazy { dotEnv("BOT_LOGIN_PASSWORD") }
    private lateinit var token: String
    private val botId: String by lazy { dotEnv("BOT_ID") }

    override suspend fun updateToken() {
        val loginRequest = LoginRequest(email, password)
        token = botApiService.login(loginRequest).token
    }

    override suspend fun sendMessage(messageToSend: MessageToSend) {
        botApiService.sendMessage(botId, messageToSend, "Bearer $token")
    }
}