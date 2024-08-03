package com.sanisamoj.routing

import com.sanisamoj.TestContext
import com.sanisamoj.TestContext.IMAGE_TEST_NAME
import com.sanisamoj.TestContext.NOT_ALLOWED_IMAGE_TEST_NAME
import com.sanisamoj.data.models.dataclass.SavedMediaResponse
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.UserTest
import com.sanisamoj.utils.converters.ObjectConverter
import com.sanisamoj.utils.eraseAllDataToTests
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class MediaRoutingTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    init {
        eraseAllDataToTests()
    }

    @Test
    fun testGetImageName() = testApplication {
        var response = client.get("/media/$IMAGE_TEST_NAME")
        assertEquals(HttpStatusCode.OK, response.status)

        response = client.get("/media/nonExistMedia")
        assertEquals(HttpStatusCode.NotFound, response.status)

    }

    @Test
    fun testPostImage() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(AccountStatus.Active)
        val token: String = userTest.token()

        val file: File = databaseRepository.getMedia(IMAGE_TEST_NAME)
        var response = client.post("/media") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("private", "no")
                        append("image", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jpg")
                            append(HttpHeaders.ContentDisposition, "filename=\"$IMAGE_TEST_NAME\"")
                        })
                    }
                )
            )
        }

        val imageSavedResponseInString: String = response.bodyAsText()
        val imageSavedResponseInObject: SavedMediaResponse = ObjectConverter()
            .stringToObject<SavedMediaResponse>(imageSavedResponseInString)

        val fileToDelete: File = databaseRepository.getMedia(imageSavedResponseInObject.filename)
        assertEquals(HttpStatusCode.OK, response.status)

        val userInDb: User = databaseRepository.getUserById(user.id.toString())
        assertEquals(userInDb.imageProfile, imageSavedResponseInObject.filename)

        response = client.post("/media") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("private", "no")
                        append("image", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/raw")
                            append(HttpHeaders.ContentDisposition, "filename=\"$NOT_ALLOWED_IMAGE_TEST_NAME\"")
                        })
                    }
                )
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        userTest.deleteUserTest()
        try {
            databaseRepository.deleteMedia(fileToDelete)
        } catch (_: Throwable) {}
    }
}