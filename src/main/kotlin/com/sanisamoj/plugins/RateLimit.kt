package com.sanisamoj.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.hours
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
    }
}