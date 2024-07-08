package com.sanisamoj.data.models.interfaces

import com.sanisamoj.data.models.dataclass.SendEmailData

interface MailRepository {
    fun sendEmail(sendEmailData: SendEmailData)
}