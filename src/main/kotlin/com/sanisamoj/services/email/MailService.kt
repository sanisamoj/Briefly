package com.sanisamoj.services.email

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.config.GlobalContext.ACTIVATE_ACCOUNT_LINK_ROUTE
import com.sanisamoj.config.GlobalContext.globalWarnings
import com.sanisamoj.config.MailContext
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.ReportingRequest
import com.sanisamoj.data.models.dataclass.SendEmailData
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.enums.ReportType
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.utils.analyzers.dotEnv

class MailService(
    private val mailRepository: MailRepository = GlobalContext.getMailRepository()
) {
    fun sendConfirmationTokenEmail(name: String, token: String, to: String) {
        val activationLink = "$ACTIVATE_ACCOUNT_LINK_ROUTE?token=$token&email=$to"
        val text: String = MailContext.buildConfirmationTokenMail(name, activationLink)
        val topic: String = globalWarnings.activateYourAccount
        val sendEmailData = SendEmailData(to, topic, text, true)

        mailRepository.sendEmail(sendEmailData)
    }

    fun sendAccountActivationMail(username: String, to: String) {
        val text: String = MailContext.buildAccountActivationMail(username)
        val topic: String = globalWarnings.welcomeToBriefly
        val sendEmailData = SendEmailData(to, topic, text, true)
        mailRepository.sendEmail(sendEmailData)
    }

    fun sendLinkDeletedEmail(username:String, linkEntry: LinkEntry, to: String) {
        val text: String = MailContext.buildLinkDeletedMail(username, linkEntry)
        val topic: String = globalWarnings.removedLink
        val sendEmailData = SendEmailData(to, topic, text, true)

        mailRepository.sendEmail(sendEmailData)
    }
    
    fun sendReportingEmail(reportingRequest: ReportingRequest) {
        val reportingType: String = when(reportingRequest.reportType) {
            ReportType.SUGGESTION.description -> {
                ReportType.SUGGESTION.description
            }
            
            ReportType.ABUSE.description -> {
                ReportType.ABUSE.description
            }
            else -> { throw Error(Errors.InvalidReportType.description)}
        }
        val topic = "${globalWarnings.systemName} - $reportingType"
        val text: String = MailContext.buildReportMail(
            username = reportingRequest.username,
            email = reportingRequest.email,
            text = reportingRequest.reporting
        )
        val superAdminEmail: String = dotEnv("SUPERADMIN_EMAIL")
        val sendEmailData = SendEmailData(superAdminEmail, topic, text, true)
        mailRepository.sendEmail(sendEmailData)
    }
}