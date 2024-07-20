package com.sanisamoj.plugins

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.redis.Redis
import com.sanisamoj.utils.eraseAllDataToTests
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ClickerCountTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    init {
        eraseAllDataToTests()
    }

    @Test
    fun clickerCountTest() = testApplication {
        val ip = "1.1.1.1.1"
        val route = "routeTest"
        databaseRepository.applicationClicksInc(ip, route)
        databaseRepository.applicationClicksInc(ip, route)

        val clickerCount: Int = databaseRepository.getCountApplicationClicks()
        assertEquals(2, clickerCount)
    }
}