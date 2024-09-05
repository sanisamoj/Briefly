package com.sanisamoj.data

import com.sanisamoj.data.models.dataclass.MessageToSend
import com.sanisamoj.data.models.interfaces.BotRepository

class BotRepositoryTest: BotRepository {
    override suspend fun updateToken() {
        println("Token updated!")
    }

    override suspend fun sendMessage(messageToSend: MessageToSend) {
        println(messageToSend)
    }
}