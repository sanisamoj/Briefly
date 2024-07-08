package com.sanisamoj.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sanisamoj.utils.analyzers.dotEnv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

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
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
