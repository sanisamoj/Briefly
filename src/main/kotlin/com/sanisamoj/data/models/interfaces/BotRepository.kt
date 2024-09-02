package com.sanisamoj.data.models.interfaces

import com.sanisamoj.data.models.dataclass.MessageToSend

interface BotRepository {
    suspend fun sendMessage(messageToSend: MessageToSend)
}