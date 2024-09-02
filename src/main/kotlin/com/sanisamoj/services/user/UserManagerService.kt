package com.sanisamoj.services.user

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.MessageToSend
import com.sanisamoj.data.models.dataclass.SavedMediaResponse
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.dataclass.UserResponse
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.enums.Fields
import com.sanisamoj.data.models.interfaces.BotRepository
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.services.media.MediaService
import com.sanisamoj.utils.generators.CharactersGenerator
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class UserManagerService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val botRepository: BotRepository = GlobalContext.getBotRepository()
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

    suspend fun updateName(userId: String, name: String): UserResponse {
        databaseRepository.updateUser(userId, OperationField(Fields.Name, value = name))
        val user: User = databaseRepository.getUserById(userId)
        return UserFactory.userResponse(user)
    }

    suspend fun updatePhone(userId: String, newPhone: String) {
        val validationCode: Int = CharactersGenerator.codeValidationGenerate()
        databaseRepository.updateUser(userId, OperationField(Fields.ValidationCode, validationCode))
        deleteValidationCodeInSpecificTime(userId)

        val messageToSend1 = MessageToSend(newPhone, GlobalContext.globalWarnings.thisYourValidationCode)
        val messageToSend2 = MessageToSend(newPhone, validationCode.toString())
        botRepository.sendMessage(messageToSend1)
        botRepository.sendMessage(messageToSend2)
    }

    private fun deleteValidationCodeInSpecificTime(userId: String, timeInMinutes: Long = 5) {
        val executorService = Executors.newSingleThreadScheduledExecutor()
        executorService.schedule({
            runBlocking {
                val update = OperationField(Fields.ValidationCode, -1)
                databaseRepository.updateUser(userId, update)
            }
        }, timeInMinutes, TimeUnit.MINUTES)

        return
    }

    suspend fun validateValidationCodeToUpdatePhone(userId: String, newPhone: String, validationCode: Int): UserResponse {
        val user: User = databaseRepository.getUserById(userId)
        if(user.validationCode == validationCode) {
            databaseRepository.updateUser(userId, OperationField(Fields.Phone, newPhone))
            val updatedUser: User = databaseRepository.getUserById(userId)
            return UserFactory.userResponse(updatedUser)
        } else if(user.validationCode == -1) {
            throw Exception(Errors.ExpiredValidationCode.description)
        } else {
            throw Exception(Errors.InvalidValidationCode.description)
        }
    }

    suspend fun updateEmail(userId: String, newEmail: String): UserResponse {
        databaseRepository.updateUser(userId, OperationField(Fields.Email, value = newEmail))
        val user: User = databaseRepository.getUserById(userId)
        return UserFactory.userResponse(user)
    }

    suspend fun updatePassword(userId: String, newPassword: String) {
        databaseRepository.updateUser(userId, OperationField(Fields.Password, value = newPassword))
    }

}