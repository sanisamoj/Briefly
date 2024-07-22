package com.sanisamoj.config

import com.sanisamoj.data.models.dataclass.ClickerResponse
import com.sanisamoj.data.models.dataclass.Connection
import com.sanisamoj.data.models.dataclass.LinkEntryUpdatedMessage
import com.sanisamoj.data.models.dataclass.SystemClicksCountResponse
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
        val allConnectionsFromUserId: List<Connection> = connections.filter { it.userId == userId }
        val linkEntryUpdatedMessage = LinkEntryUpdatedMessage(userId, shortLink, clickerResponse)
        val linkEntryUpdatedMessageInString: String = Json.encodeToString(linkEntryUpdatedMessage)

        allConnectionsFromUserId.forEach {
            it.session.send(Frame.Text(linkEntryUpdatedMessageInString))
        }
    }

    suspend fun notifyAboutClickCount(count: Int) {
        val allModeratorConnections: List<Connection> = connections.filter { it.moderator }

        if(isAnyModeratorConnected()) {
            val systemClicksCountResponse = SystemClicksCountResponse(count)
            val systemClicksCountResponseInString: String = Json.encodeToString(systemClicksCountResponse)

            allModeratorConnections.forEach {
                it.session.send(Frame.Text(systemClicksCountResponseInString))
            }
        }
    }

    fun isAnyModeratorConnected(): Boolean {
        val allModeratorConnections: List<Connection> = connections.filter { it.moderator }
        return allModeratorConnections.isNotEmpty()
    }
}