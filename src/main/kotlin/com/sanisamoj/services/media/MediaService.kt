package com.sanisamoj.services.media

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.config.GlobalContext.MEDIA_ROUTE
import com.sanisamoj.data.models.dataclass.SavedMediaResponse
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import io.ktor.http.content.*
import java.io.File

class MediaService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository()
) {
    fun getImage(imageName: String): File {
        return databaseRepository.getMedia(imageName)
    }

    suspend fun savePublicImage(multipartData: MultiPartData): List<SavedMediaResponse> {
        val listNames: List<String> = databaseRepository.saveMedia(multipartData)
        val saveMediaResponseList: MutableList<SavedMediaResponse> = mutableListOf()

        listNames.forEach {
            saveMediaResponseList.add(SavedMediaResponse(it, "$MEDIA_ROUTE$it"))
        }

        return saveMediaResponseList
    }

    fun deleteImage(imageName: String) {
        val imageFile: File = databaseRepository.getMedia(imageName)
        databaseRepository.deleteMedia(imageFile)
    }
}