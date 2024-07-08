package com.sanisamoj.utils.generators

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sanisamoj.data.models.dataclass.TokenInfo
import com.sanisamoj.utils.analyzers.dotEnv
import java.util.*

object TokenGenerator {
    private val audience: String = dotEnv("JWT_AUDIENCE")
    private val domain: String = dotEnv("JWT_DOMAIN")
    private val userSecret = dotEnv("USER_SECRET")
    private val moderatorSecret = dotEnv("MODERATOR_SECRET")

    fun user(tokenInfo: TokenInfo): String {
        val currentTime: Long = System.currentTimeMillis()

        val token: String = JWT.create()
            .withClaim("id", tokenInfo.id)
            .withClaim("email", tokenInfo.email)
            .withClaim("session", tokenInfo.sessionId)
            .withAudience(audience)
            .withIssuer(domain)
            .withExpiresAt(Date(currentTime + tokenInfo.time))
            .sign(Algorithm.HMAC256(userSecret))

        return token
    }

    fun moderator(tokenInfo: TokenInfo): String {
        val currentTime: Long = System.currentTimeMillis()

        val token: String = JWT.create()
            .withClaim("id", tokenInfo.id)
            .withClaim("email", tokenInfo.email)
            .withAudience(audience)
            .withIssuer(domain)
            .withExpiresAt(Date(currentTime + tokenInfo.time))
            .sign(Algorithm.HMAC256(moderatorSecret))

        return token
    }
}