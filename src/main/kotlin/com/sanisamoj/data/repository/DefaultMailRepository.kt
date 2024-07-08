package com.sanisamoj.data.repository

import com.sanisamoj.data.models.dataclass.SendEmailData
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.utils.analyzers.dotEnv
import java.util.*
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object DefaultMailRepository : MailRepository {
    private val session: Session by lazy { session() }
    private val email: String = dotEnv("EMAIL_SYSTEM")
    private val password: String = dotEnv("EMAIL_PASSWORD")

    private val smtpHost =  dotEnv("SMTP_HOST")
    private val smtpStartTls = dotEnv("SMTP_STARTTLS_ENABLE")
    private val smtpSSLProtocols = dotEnv("SMTP_SSL_PROTOCOLS")
    private val smtpSocketFactoryPort = dotEnv("SMTP_SOCKETFACTORY_PORT")
    private val smtpSocketFactoryClass = dotEnv("SMTP_SOCKETFACTORY_CLASS")
    private val smtpAuth = dotEnv("SMTP_AUTH")
    private val smtpPort = dotEnv("SMTP_PORT")
    private val smtpSSLTrust = dotEnv("SMTP_SSL_TRUST")

    private val props = Properties().apply {
        put("mail.smtp.host", smtpHost)
        put("mail.smtp.starttls.enable", smtpStartTls)
        put("mail.smtp.ssl.protocols", smtpSSLProtocols)
        put("mail.smtp.socketFactory.port", smtpSocketFactoryPort)
        put("mail.smtp.socketFactory.class", smtpSocketFactoryClass)
        put("mail.smtp.auth", smtpAuth)
        put("mail.smtp.port", smtpPort)
        put("mail.smtp.ssl.trust", smtpSSLTrust)
    }

    override fun sendEmail(sendEmailData: SendEmailData) {
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(email))
                setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(sendEmailData.to)
                )
                subject = sendEmailData.topic
                if(sendEmailData.isHtml) {
                    setContent(sendEmailData.text, "text/html")
                } else {
                    setText(sendEmailData.text)
                }

            }

            Transport.send(message)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun session(): Session {
        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password)
            }
        }) ?: throw Exception("Without Mail Session")

        return session
    }
}