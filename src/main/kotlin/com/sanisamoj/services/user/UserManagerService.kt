package com.sanisamoj.services.user

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.SavedMediaResponse
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.services.media.MediaService
import io.ktor.http.content.*

class UserManagerService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository()
) {

    suspend fun updateImageProfile(userId: String, multipartData: MultiPartData): SavedMediaResponse {
        val user: User = databaseRepository.getUserById(userId)
        val imageNameInUser: String = user.imageProfile

        val mediaService = MediaService()
        if(imageNameInUser != "") {
            try {
                mediaService.deleteImage(imageNameInUser)
            } catch (_: Throwable) {}
        }

        val savedImage: SavedMediaResponse = MediaService().savePublicImage(multipartData)[0]
        databaseRepository.updateUser(userId, OperationField(Fields.ImageProfile, savedImage.filename))
        return savedImage
    }

    suspend fun deleteImageProfile(userId: String) {
        val user: User = databaseRepository.getUserById(userId)
        databaseRepository.updateUser(userId, OperationField(Fields.ImageProfile, value = ""))
        MediaService().deleteImage(user.imageProfile)
    }

}