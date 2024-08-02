package com.sanisamoj.services.email

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.config.GlobalContext.ACTIVATE_ACCOUNT_LINK_ROUTE
import com.sanisamoj.config.MailContext
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.SendEmailData
import com.sanisamoj.data.models.interfaces.MailRepository

class MailService(
    private val mailRepository: MailRepository = GlobalContext.getMailRepository()
) {
    fun sendConfirmationTokenEmail(name: String, token: String, to: String) {
        val activationLink = "$ACTIVATE_ACCOUNT_LINK_ROUTE?token=$token"
        val text: String = MailContext.buildConfirmationTokenMail(name, activationLink)
        val topic = "Ative sua conta!"
        val sendEmailData = SendEmailData(to, topic, text, true)

        mailRepository.sendEmail(sendEmailData)
    }

    fun sendAccountActivationMail(username: String, to: String) {
        val text: String = MailContext.buildAccountActivationMail(username)
        val topic = "Bem-vindo ao Briefly!"
        val sendEmailData = SendEmailData(to, topic, text, true)
        mailRepository.sendEmail(sendEmailData)
    }

    fun sendLinkDeletedEmail(username:String, linkEntry: LinkEntry, to: String) {
        val text = MailContext.buildLinkDeletedMail(username, linkEntry)
        val topic = "O link encurtado foi removido!"
        val sendEmailData = SendEmailData(to, topic, text, true)

        mailRepository.sendEmail(sendEmailData)
    }
}