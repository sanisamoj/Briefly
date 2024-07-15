package com.sanisamoj.data.models.dataclass

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession, val userId: String) {
    companion object {
        val lastId = AtomicInteger(0)
    }

    val id: Int = lastId.getAndIncrement()
    val user: String = userId
}