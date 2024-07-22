package com.sanisamoj.plugins

import com.sanisamoj.config.WebSocketManager
import com.sanisamoj.data.models.dataclass.Connection
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

fun Application.configureSockets(
    webSocketManager: WebSocketManager = WebSocketManager
) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        authenticate("user-jwt") {
            webSocket("/links") {
                val userId = call.parameters["id"].toString()
                val thisConnection = Connection(this, userId)
                webSocketManager.addConnection(thisConnection)

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Close) {
                            webSocketManager.removeConnection(thisConnection)
                            break
                        }
                    }
                } finally {
                    webSocketManager.removeConnection(thisConnection)
                }
            }
        }

        authenticate("moderator-jwt") {
            webSocket("/server") {
                val userId = call.parameters["id"].toString()
                val thisConnection = Connection(this, userId, admin = true)
                webSocketManager.addConnection(thisConnection)

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Close) {
                            webSocketManager.removeConnection(thisConnection)
                            break
                        }
                    }
                } finally {
                    webSocketManager.removeConnection(thisConnection)
                }
            }
        }
    }
}
