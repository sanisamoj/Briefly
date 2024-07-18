package com.sanisamoj.routing

import com.sanisamoj.module
import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ModeratorRoutingKtTest {

    @Test
    fun testDeleteModeratorLink() = testApplication {
        application {
            module()
        }
        client.delete("/moderator/link").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetModeratorLink() = testApplication {
        application {
            module()
        }
        client.get("/moderator/link").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutModeratorBlock() = testApplication {
        application {
            module()
        }
        client.put("/moderator/block").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetModeratorUsers() = testApplication {
        application {
            module()
        }
        client.get("/moderator/users").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetModeratorUser() = testApplication {
        application {
            module()
        }
        client.get("/moderator/user").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostModerator() = testApplication {
        application {
            module()
        }
        client.post("/moderator").apply {
            TODO("Please write your test here")
        }
    }
}