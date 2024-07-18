package com.sanisamoj.config

import com.sanisamoj.data.models.dataclass.ClickerResponse
import com.sanisamoj.data.models.dataclass.Connection
import com.sanisamoj.data.models.dataclass.LinkEntryUpdatedMessage
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

object WebSocketManager {
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    fun addConnection(connection: Connection) {
        connections += connection
    }

    fun removeConnection(connection: Connection) {
        connections -= connection
    }

    suspend fun notifyAboutShortLink(userId: String, shortLink: String, clickerResponse: ClickerResponse) {
        val allConnectionsFromUserId = connections.filter { it.userId == userId }
        val linkEntryUpdatedMessage = LinkEntryUpdatedMessage(userId, shortLink, clickerResponse)
        val linkEntryUpdatedMessageInString: String = Json.encodeToString(linkEntryUpdatedMessage)

        allConnectionsFromUserId.forEach {
            it.session.send(Frame.Text(linkEntryUpdatedMessageInString))
        }
    }
}