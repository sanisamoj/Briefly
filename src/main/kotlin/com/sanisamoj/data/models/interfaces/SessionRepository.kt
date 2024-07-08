package com.sanisamoj.data.models.interfaces

import com.sanisamoj.data.models.dataclass.SessionEntry
import com.sanisamoj.data.models.dataclass.Sessions

interface SessionRepository {
    suspend fun getSession(accountId: String) : Sessions
    suspend fun saveSession(session: Sessions)
    suspend fun setSessionEntry(accountId: String, entry: SessionEntry)
    suspend fun getSessionEntry(accountId: String, sessionId: String) : SessionEntry?
    suspend fun revokeSession(accountId: String, sessionId: String)
    suspend fun sessionRevoked (accountId: String, sessionId: String) : Boolean
}