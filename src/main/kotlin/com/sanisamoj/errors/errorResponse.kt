package com.sanisamoj.errors

import com.sanisamoj.data.models.dataclass.ErrorResponse
import com.sanisamoj.data.models.enums.ActionsMessages
import com.sanisamoj.data.models.enums.Errors
import io.ktor.http.*

fun errorResponse(errorMessage: String?): Pair<HttpStatusCode, ErrorResponse> {
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
            HttpStatusCode.Unauthorized to ErrorResponse(Errors.InvalidLogin.description)
        }

        Errors.InactiveAccount.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(
                error = Errors.InactiveAccount.description,
                details = ActionsMessages.ActivateAccount.description
            )
        }

        Errors.BlockedAccount.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(
                error = Errors.BlockedAccount.description,
                details = ActionsMessages.ContactSupport.description
            )
        }

        Errors.SuspendedAccount.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(
                error = Errors.SuspendedAccount.description,
                details = ActionsMessages.ContactSupport.description
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

        Errors.PersonalizedShortLinkAlreadyExist.description -> {
            HttpStatusCode.Conflict to ErrorResponse(Errors.PersonalizedShortLinkAlreadyExist.description)
        }

        Errors.UnableToComplete.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.UnableToComplete.description)
        }

        Errors.MediaNotExist.description -> {
            HttpStatusCode.NotFound to ErrorResponse(Errors.MediaNotExist.description)
        }

        Errors.InvalidLink.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(Errors.InvalidLink.description)
        }

        Errors.UnsupportedMediaType.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(
                error = Errors.UnsupportedMediaType.description,
                details = ActionsMessages.MimeTypesAllowed.description
            )
        }

        Errors.TheLinkHasOwner.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.TheLinkHasOwner.description)
        }

        Errors.TheLimitMaxImageAllowed.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(Errors.TheLimitMaxImageAllowed.description)
        }

        Errors.ExpiredValidationCode.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.ExpiredValidationCode.description)
        }

        Errors.InvalidValidationCode.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.InvalidValidationCode.description)
        }

        Errors.InvalidPageOrSizeParameters.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(Errors.InvalidPageOrSizeParameters.description)
        }

        Errors.ShortLinkOrPaginationRequired.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(Errors.ShortLinkOrPaginationRequired.description)
        }

        else -> {
            HttpStatusCode.InternalServerError to ErrorResponse(Errors.InternalServerError.description)
        }
    }

    return response
}