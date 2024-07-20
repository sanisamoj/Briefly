package com.sanisamoj.plugins

import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.security.AccessGuardianService
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


fun Application.rateLimit() {
    install(RateLimit) {
        register(RateLimitName("register")) {
            rateLimiter(limit = 2, refillPeriod = 1.hours)
        }

        register(RateLimitName("validation")) {
            rateLimiter(limit = 3, refillPeriod = 24.hours)
        }

        register(RateLimitName("lightweight")) {
            rateLimiter(limit = 3, refillPeriod = 1.seconds)
        }

        register(RateLimitName("login")) {
            rateLimiter(limit = 5, refillPeriod = 1.hours)
        }

        register(RateLimitName("publicLinkEntry-forMinute")) {
            rateLimiter(limit = 10, refillPeriod = 1.minutes)

            modifyResponse { applicationCall, state ->
                when (state) {
                    is RateLimiter.State.Exhausted -> {
                        val ip: String = applicationCall.request.origin.remoteHost
                        val route: String = applicationCall.request.uri
                        AccessGuardianService.blockIp(ip, route)
                        throw Exception(Errors.AccessProhibited.description)
                    }
                    is RateLimiter.State.Available -> {}
                }
            }
        }

        register(RateLimitName("publicLinkEntry-forHour")) {
            rateLimiter(limit = 50, refillPeriod = 1.hours)

            modifyResponse { applicationCall, state ->
                when (state) {
                    is RateLimiter.State.Exhausted -> {
                        val ip: String = applicationCall.request.origin.remoteHost
                        val route: String = applicationCall.request.uri
                        AccessGuardianService.blockIp(ip, route)
                        throw Exception(Errors.AccessProhibited.description)
                    }
                    is RateLimiter.State.Available -> {}
                }
            }
        }

        register(RateLimitName("publicLinkEntry-forDay")) {
            rateLimiter(limit = 200, refillPeriod = 24.hours)

            modifyResponse { applicationCall, state ->
                when (state) {
                    is RateLimiter.State.Exhausted -> {
                        val ip: String = applicationCall.request.origin.remoteHost
                        val route: String = applicationCall.request.uri
                        AccessGuardianService.blockIp(ip, route)
                        throw Exception(Errors.AccessProhibited.description)
                    }
                    is RateLimiter.State.Available -> {}
                }
            }
        }
    }
}