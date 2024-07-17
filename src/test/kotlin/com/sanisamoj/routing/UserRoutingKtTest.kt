package com.sanisamoj.routing

import com.sanisamoj.module
import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test

class UserRoutingKtTest {

    @Test
    fun testPostUser() = testApplication {
        application {
            module()
        }
        client.post("/user").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testDeleteUserLink() = testApplication {
        application {
            module()
        }
        client.delete("/user/link").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutUserLink() = testApplication {
        application {
            module()
        }
        client.put("/user/link").apply {
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

    @Test
    fun testPostAuthenticationGenerate() = testApplication {
        application {
            module()
        }
        client.post("/authentication/generate").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostAuthenticationLogin() = testApplication {
        application {
            module()
        }
        client.post("/authentication/login").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetAuthenticationActivate() = testApplication {
        application {
            module()
        }
        client.get("/authentication/activate").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostAuthenticationSession() = testApplication {
        application {
            module()
        }
        client.post("/authentication/session").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testDeleteAuthenticationSession() = testApplication {
        application {
            module()
        }
        client.delete("/authentication/session").apply {
            TODO("Please write your test here")
        }
    }
}