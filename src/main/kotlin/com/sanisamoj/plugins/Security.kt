package com.sanisamoj.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.SessionRepository
import com.sanisamoj.utils.analyzers.dotEnv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity(
    sessionRepository: SessionRepository = GlobalContext.getSessionRepository()
) {
    val jwtAudience: String = dotEnv("JWT_AUDIENCE")
    val jwtDomain: String  = dotEnv("JWT_DOMAIN")
    val userSecret: String  = dotEnv("USER_SECRET")
    val moderatorSecret: String  = dotEnv("MODERATOR_SECRET")
    val updatePasswordSecret: String  = dotEnv("UPDATE_PASSWORD_SECRET")

    authentication {
        jwt("user-jwt") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(userSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                val accountId = credential.payload.getClaim("id").asString()
                val sessionId = credential.payload.getClaim("session").asString()
                verifySession(
                    sessionRepository = sessionRepository,
                    accountId = accountId,
                    sessionId = sessionId
                )
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }

        jwt("moderator-jwt") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(moderatorSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                val accountId = credential.payload.getClaim("id").asString()
                val sessionId = credential.payload.getClaim("session").asString()
                verifySession(
                    sessionRepository = sessionRepository,
                    accountId = accountId,
                    sessionId = sessionId
                )
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }

        jwt("update-jwt") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(updatePasswordSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

private suspend fun verifySession(sessionRepository: SessionRepository, accountId: String, sessionId: String) {
    val sessionRevoked = sessionRepository.sessionRevoked(accountId, sessionId)
    if (sessionRevoked) throw Exception(Errors.ExpiredSession.description)
}
