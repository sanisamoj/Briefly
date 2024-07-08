package com.sanisamoj.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.utils.analyzers.dotEnv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtAudience = dotEnv("JWT_AUDIENCE")
    val jwtDomain = dotEnv("JWT_DOMAIN")
    val userSecret = dotEnv("USER_SECRET")

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
                val sessionRepository = GlobalContext.getSessionRepository()
                val sessionRevoked = sessionRepository.sessionRevoked(accountId, sessionId)
                if (sessionRevoked) throw Exception(Errors.ExpiredSession.description)
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
