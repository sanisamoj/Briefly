package com.sanisamoj.services.media

import com.sanisamoj.TestContext
import com.sanisamoj.TestContext.IMAGE_TEST_NAME
import com.sanisamoj.TestContext.NOT_ALLOWED_IMAGE_TEST_NAME
import com.sanisamoj.data.models.dataclass.SavedMediaResponse
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.eraseAllDataToTests
import io.ktor.http.content.*
import io.ktor.server.testing.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class MediaServiceTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    init {
        eraseAllDataToTests()
    }

    @Test
    fun getImage() = testApplication {
        val file: File = MediaService().getImage(IMAGE_TEST_NAME)
        assertEquals(true, file.exists())
        assertFails {
            MediaService().getImage("non-exist-file")
        }
    }

    @Test
    fun saveMedia() = testApplication {
        val file: File = MediaService().getImage(IMAGE_TEST_NAME)
        val multiPartData:  MultiPartData = TestContext.createMultiPartData(file, IMAGE_TEST_NAME)
        val savedMediaResponseList: List<SavedMediaResponse> = MediaService().savePublicImage(multiPartData)
        val savedFile: File = databaseRepository.getMedia(savedMediaResponseList[0].filename)

        assertEquals(true, savedFile.exists())
        assertFails { databaseRepository.getMedia(savedMediaResponseList[0].fileLink) }

        val fileWithNotAllowedType: File = MediaService().getImage(NOT_ALLOWED_IMAGE_TEST_NAME)
        val notAllowedTypeMedia: MultiPartData = TestContext.createMultiPartData(fileWithNotAllowedType, NOT_ALLOWED_IMAGE_TEST_NAME)

        assertFails {
            MediaService().savePublicImage(notAllowedTypeMedia)
        }

        databaseRepository.deleteMedia(savedFile)
    }
}