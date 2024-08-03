package com.sanisamoj.data.models.enums

import com.sanisamoj.config.GlobalContext.MIME_TYPE_ALLOWED

enum class ActionsMessages(val description: String) {
    ActivateAccount("Activate Account"),
    ContactSupport("Contact support"),
    MimeTypesAllowed("the permitted types are: ${MIME_TYPE_ALLOWED.joinToString(", ")}!")
}