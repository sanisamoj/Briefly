package com.sanisamoj.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.hours


fun Application.rateLimit() {
    install(RateLimit) {
        register(RateLimitName("register")) {
            rateLimiter(limit = 2, refillPeriod = 1.hours)
        }
    }
}