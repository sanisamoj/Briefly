package com.sanisamoj.config

import com.sanisamoj.data.models.dataclass.ClickerResponse
import com.sanisamoj.data.models.dataclass.Connection
import com.sanisamoj.data.models.dataclass.LinkEntryUpdatedMessage
import com.sanisamoj.data.models.dataclass.CountResponse
import com.sanisamoj.data.models.enums.WebSocketCountItem
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

object WebSocketManager {
    private val moderatorConnections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    private val userConnections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    fun addModeratorConnection(connection: Connection) {
        moderatorConnections += connection
    }

    fun addUserConnection(connection: Connection) {
        userConnections += connection
    }

    fun removeModeratorConnection(connection: Connection) {
        moderatorConnections -= connection
    }

    fun removeUserConnection(connection: Connection) {
        userConnections -= connection
    }

    suspend fun notifyAboutShortLink(userId: String, shortLink: String, clickerResponse: ClickerResponse) {
        val allConnectionsFromUserId: List<Connection> = userConnections.filter { it.userId == userId }
        val linkEntryUpdatedMessage = LinkEntryUpdatedMessage(userId, shortLink, clickerResponse)
        val linkEntryUpdatedMessageInString: String = Json.encodeToString(linkEntryUpdatedMessage)

        allConnectionsFromUserId.forEach {
            it.session.send(Frame.Text(linkEntryUpdatedMessageInString))
        }
    }

    suspend fun notifyAboutClickCount(count: Int) {
        val allModeratorConnections: List<Connection> = moderatorConnections.filter { it.moderator }

        if(isAnyModeratorConnected()) {
            val countResponse = CountResponse(WebSocketCountItem.SystemClicksCount.name, count)
            val systemClicksCountResponseInString: String = Json.encodeToString(countResponse)

            allModeratorConnections.forEach {
                it.session.send(Frame.Text(systemClicksCountResponseInString))
            }
        }
    }

    suspend fun notifyAboutLinkEntryCount(count: Int) {
        val allModeratorConnections: List<Connection> = moderatorConnections.filter { it.moderator }

        if(isAnyModeratorConnected()) {
            val countResponse = CountResponse(WebSocketCountItem.LinkEntriesCount.name, count)
            val systemClicksCountResponseInString: String = Json.encodeToString(countResponse)

            allModeratorConnections.forEach {
                it.session.send(Frame.Text(systemClicksCountResponseInString))
            }
        }
    }

    fun isAnyModeratorConnected(): Boolean {
        val allModeratorConnections: List<Connection> = moderatorConnections.filter { it.moderator }
        return allModeratorConnections.isNotEmpty()
    }
}