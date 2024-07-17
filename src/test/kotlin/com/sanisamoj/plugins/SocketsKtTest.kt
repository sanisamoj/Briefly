package com.sanisamoj.plugins

import com.sanisamoj.module
import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import kotlin.test.Test

class SocketsKtTest {

    @Test
    fun testWebsocketServer() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(WebSockets)
        }
        client.webSocket("/server") {
            TODO("Please write your test here")
        }
    }
}