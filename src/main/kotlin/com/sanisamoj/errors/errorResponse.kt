package com.sanisamoj.errors

import com.sanisamoj.data.models.dataclass.ErrorResponse
import com.sanisamoj.data.models.enums.ActionsMessages
import com.sanisamoj.data.models.enums.Errors
import io.ktor.http.*

fun errorResponse(errorMessage: String?): Pair<HttpStatusCode, ErrorResponse> {
    println(errorMessage)
    val response = when(errorMessage) {
        Errors.UserAlreadyExists.description -> {
            HttpStatusCode.Conflict to ErrorResponse(Errors.UserAlreadyExists.description)
        }

        Errors.DataIsMissing.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(Errors.DataIsMissing.description)
        }

        Errors.UserNotFound.description -> {
            HttpStatusCode.NotFound to ErrorResponse(Errors.UserNotFound.description)
        }

        Errors.InvalidLogin.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(Errors.InvalidLogin.description)
        }

        Errors.BlockedAccount.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(Errors.BlockedAccount.description)
        }

        Errors.InactiveAccount.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(
                error = Errors.InactiveAccount.description,
                details = ActionsMessages.ActivateAccount.description
            )
        }

        Errors.ShortLinkNotFound.description -> {
            HttpStatusCode.NotFound to ErrorResponse(Errors.ShortLinkNotFound.description)
        }

        Errors.AccessProhibited.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.AccessProhibited.description)
        }

        Errors.ProtectedLink.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.ProtectedLink.description)
        }

        Errors.InvalidPassword.description -> {
            HttpStatusCode.Unauthorized to ErrorResponse(Errors.InvalidPassword.description)
        }

        Errors.ExpiredLink.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.ExpiredLink.description)
        }

        Errors.InactiveRedirection.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.InactiveRedirection.description)
        }

        Errors.TermsOfServiceNotFound.description -> {
            HttpStatusCode.NotFound to ErrorResponse(
                error = Errors.TermsOfServiceNotFound.description,
                details = ActionsMessages.ContactSupport.description
            )
        }

        else -> {
            HttpStatusCode.InternalServerError to ErrorResponse(Errors.InternalServerError.description)
        }
    }

    return response
}