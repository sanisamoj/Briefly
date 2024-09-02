package com.sanisamoj.data.repository

import com.sanisamoj.api.bot.BotApiService
import com.sanisamoj.data.models.dataclass.MessageToSend
import com.sanisamoj.data.models.interfaces.BotRepository
import com.sanisamoj.utils.analyzers.dotEnv

class DefaultBotRepository(
    private val botApiService: BotApiService
) : BotRepository {
    private val token: String by lazy { dotEnv("BOT_TOKEN") }
    private val botId: String by lazy { dotEnv("BOT_ID") }

    override suspend fun sendMessage(messageToSend: MessageToSend) {
        botApiService.sendMessage(botId, messageToSend, "Bearer $token")
    }
}