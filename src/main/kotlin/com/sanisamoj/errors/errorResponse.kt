package com.sanisamoj.errors

import com.sanisamoj.config.GlobalContext
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
            HttpStatusCode.Conflict to ErrorResponse(Errors.UserNotFound.description)
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

        Errors.MaximumShortLinksExceeded.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(
                error = Errors.MaximumShortLinksExceeded.description,
                details = "${ActionsMessages.MaxAllowedShortLinks} ${GlobalContext.MAX_SHORT_LINK_BY_ACCOUNT} short links."
            )
        }

        Errors.ExpiredLink.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.ExpiredLink.description)
        }

        Errors.InactiveRedirection.description -> {
            HttpStatusCode.Forbidden to ErrorResponse(Errors.InactiveRedirection.description)
        }

        else -> {
            HttpStatusCode.InternalServerError to ErrorResponse(Errors.InternalServerError.description)
        }
    }

    return response
}