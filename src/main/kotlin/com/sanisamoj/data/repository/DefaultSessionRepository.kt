package com.sanisamoj.data.repository

import com.sanisamoj.data.models.dataclass.SessionEntry
import com.sanisamoj.data.models.dataclass.Sessions
import com.sanisamoj.data.models.interfaces.SessionRepository
import com.sanisamoj.database.redis.CollectionsInRedis
import com.sanisamoj.database.redis.DataIdentificationRedis
import com.sanisamoj.database.redis.Redis
import java.util.concurrent.TimeUnit

class DefaultSessionRepository: SessionRepository {
    override suspend fun getSession(accountId: String): Sessions {
        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.LiveSessions, accountId)
        val sessions: Sessions = Redis.getObject<Sessions>(identificationRedis) ?: createEmptySession(accountId)
        return sessions
    }

    private fun createEmptySession(accountId: String): Sessions {
        return Sessions(accountId)
    }

    override suspend fun saveSession(session: Sessions) {
        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.LiveSessions, session.accountId)
        Redis.setObject(identificationRedis, session)
    }

    override suspend fun setSessionEntry(accountId: String, entry: SessionEntry) {
        val session: Sessions = getSession(accountId)
        val liveSessions: MutableList<SessionEntry> = session.liveSessions.toMutableList()
        liveSessions.add(entry)
        val newSession: Sessions = session.copy(liveSessions = liveSessions)

        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.LiveSessions, session.accountId)
        Redis.setObject(identificationRedis, newSession)
    }

    override suspend fun getSessionEntry(accountId: String, sessionId: String): SessionEntry? {
        val session: Sessions = getSession(accountId)
        return session.liveSessions.find { it.sessionId == sessionId }
    }

    override suspend fun revokeSession(accountId: String, sessionId: String) {
        val session: Sessions = getSession(accountId)
        val liveSessions: MutableList<SessionEntry> = session.liveSessions.toMutableList()
        val revokedSessions: MutableList<SessionEntry> = session.revokedSessions.toMutableList()

        val liveSessionIndex: Int = liveSessions.indexOfFirst { it.sessionId == sessionId }
        val revokedSession: SessionEntry = liveSessions[liveSessionIndex]

        revokedSessions.add(revokedSession)
        liveSessions.removeAt(liveSessionIndex)

        val newSession: Sessions = session.copy(liveSessions = liveSessions, revokedSessions = revokedSessions)
        saveSession(newSession)

        val identificationRedisToRevokedSessions = DataIdentificationRedis(CollectionsInRedis.RevokedSessions, sessionId)
        Redis.setWithTimeToLive(
            identification = identificationRedisToRevokedSessions,
            value = sessionId,
            time = TimeUnit.DAYS.toMillis(30)
        )
    }

    override suspend fun sessionRevoked(accountId: String, sessionId: String): Boolean {
        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.RevokedSessions, sessionId)
        val sessionIdInRedis: String? = Redis.get(identificationRedis)
        return sessionIdInRedis != null
    }

}