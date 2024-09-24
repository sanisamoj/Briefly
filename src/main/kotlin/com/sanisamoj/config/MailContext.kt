package com.sanisamoj.config

import com.sanisamoj.config.GlobalContext.UNKNOWN
import com.sanisamoj.data.models.dataclass.LinkEntry
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MailContext {

    fun buildConfirmationTokenMail(username: String, activationLink: String): String {

        return createHTML().html {
            head {
                style {
                    unsafe {
                        raw(
                            """
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 50px auto;
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 8px;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        }
                        h1 {
                            color: #333333;
                            text-align: center;
                        }
                        p {
                            color: #666666;
                            line-height: 1.5;
                        }
                        .button {
                            display: block;
                            width: 200px;
                            margin: 20px auto;
                            padding: 10px;
                            text-align: center;
                            background-color: #28a745;
                            color: #155724;
                            text-decoration: none;
                            border-radius: 5px;
                            font-weight: bold;
                        }
                        .footer {
                            text-align: center;
                            color: #999999;
                            font-size: 12px;
                            margin-top: 20px;
                        }
                    """.trimIndent()
                        )
                    }
                }
            }
            body {
                div("container") {
                    h1 { +"Bem-vindo ao Briefly! $username!" }
                    p {
                        +"Estamos muito felizes em tê-lo conosco. Você precisa ativar a sua conta. "
                    }
                    p {
                        +"Por favor, clique no link abaixo para confirmar o seu endereço de e-mail e ativar sua conta:"
                    }
                    a(href = activationLink, classes = "button") { +"Ativar Conta" }
                    p {
                        +"O código estará válido apenas por 5 minutos."
                    }
                    p {
                        +"Se você não se registrou para este serviço, por favor, ignore este e-mail."
                    }
                    p {
                        +"Atenciosamente,"
                        br()
                        +"Equipe Briefly"
                    }
                    div("footer") {
                        +"© 2024 Sanisamoj. Todos os direitos reservados."
                    }
                }
            }
        }
    }

    private val activationAccountMailCssStyles = """
        body {
            font-family: Arial, sans-serif;
            background-color: #ffffff;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 50px auto;
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            color: #333333;
            text-align: center;
        }
        p, h3 {
            color: #333333;
            line-height: 1.6;
        }
        ul {
            padding-left: 15px; /* Reduz o recuo dos itens da lista */
            margin-left: 0; /* Remove qualquer margem extra à esquerda */
        }
        li {
            margin-bottom: 8px; /* Adiciona um espaço entre os itens */
        }
        .button {
            display: block;
            width: 200px;
            margin: 20px auto;
            padding: 10px;
            text-align: center;
            background-color: #007bff;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
        }
        .footer {
            text-align: center;
            color: #666666;
            font-size: 12px;
            margin-top: 20px;
        }
        .info {
            margin-top: 20px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
    """.trimIndent()

    fun buildAccountActivationMail(username: String): String {
        return createHTML().html {
            head {
                title("Ativação de Conta")
                style {
                    unsafe { +activationAccountMailCssStyles }
                }
            }
            body {
                div("container") {
                    h1 { +"Bem-vindo ao Briefly, $username!" }
                    p {
                        +"Obrigado por se registrar no Briefly! Sua conta foi criada com sucesso."
                    }
                    div("info") {
                        h2 { +"Sobre o Briefly" }
                        p {
                            +"O Briefly é um serviço de encurtamento de links que oferece diversas funcionalidades para você gerenciar e analisar seus links. Confira abaixo algumas de nossas principais funcionalidades:"
                        }
                        h3 { +"Informações Coletadas" }
                        ul {
                            li { +"IP: O endereço IP do usuário será registrado para fins de análise e segurança." }
                            li { +"Geolocalização: A localização geográfica será determinada com base no endereço IP." }
                            li { +"Horário: O registro de cada conexão será armazenado com um timestamp." }
                            li { +"Dispositivo: Informações sobre o dispositivo utilizado, como tipo (desktop, mobile), sistema operacional (Windows, iOS, Android) e navegador (Chrome, Firefox, Safari)." }
                            li { +"URL de origem: A URL da página de onde o usuário foi redirecionado para o link encurtado." }
                        }
                        h3 { +"Análise de Links" }
                        ul {
                            li { +"Cliques: Total de cliques em cada link encurtado." }
                            li { +"Filtros: Análise por hora, dia, semana e mês; Distribuição geográfica dos cliques; Estatísticas por dispositivo, sistema operacional e navegador." }
                        }
                        h3 { +"Funcionalidades dos Links" }
                        ul {
                            li { +"Links Expiráveis: Criação de links com validade definida." }
                            li { +"QR Code: Geração de QR Codes que levam ao link encurtado." }
                            li { +"Proteção de Links: Links podem ser protegidos por senha para maior segurança." }
                        }
                        h3 { +"Funcionalidades do Sistema" }
                        ul {
                            li { +"Encurtamento de links, com ou sem login." }
                            li { +"Monitoramento e visualização das informações coletadas sobre os cliques." }
                            li { +"Notificações por e-mail sobre o desempenho dos links." }
                            li { +"Gerenciamento de links: Atualizar status ou excluir links a qualquer momento." }
                        }
                    }
                    p {
                        +"Atenciosamente,"
                        br()
                        +"Equipe Briefly"
                    }
                    div("footer") {
                        +"© 2024 Briefly. Todos os direitos reservados."
                    }
                }
            }
        }
    }

    fun buildLinkDeletedMail(username: String, linkEntry: LinkEntry): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

        return createHTML().html {
            head {
                title("Link Deletado por Inatividade")
                style {
                    unsafe {
                        +"""
                    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
                    body {
                        font-family: 'Roboto', sans-serif;
                        background-color: #f4f4f9;
                        padding: 20px;
                        margin: 0;
                    }
                    .container {
                        background-color: white;
                        padding: 2em;
                        border-radius: 8px;
                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                    }
                    h1, h2 {
                        color: #333;
                    }
                    p {
                        color: #666;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 1em;
                    }
                    th, td {
                        border: 1px solid #ddd;
                        padding: 8px;
                        text-align: left;
                    }
                    th {
                        background-color: #f2f2f2;
                    }
                """.trimIndent()
                    }
                }
            }
            body {
                div("container") {
                    h1 { +"Link Deletado por Inatividade" }
                    p {
                        +"Olá, "
                        +username
                        +". O link que você criou foi deletado porque não foi acessado há mais de 3 anos."
                    }
                    h2 { +"Estatísticas do Link" }
                    table {
                        thead {
                            tr {
                                th { +"Propriedade" }
                                th { +"Valor" }
                            }
                        }
                        tbody {
                            tr {
                                td { +"Link Original" }
                                td { +linkEntry.originalLink }
                            }
                            tr {
                                td { +"Link Encurtado" }
                                td { +linkEntry.shortLink }
                            }
                            tr {
                                td { +"Data de Criação" }
                                td { +LocalDateTime.parse(linkEntry.createdAt).format(formatter) }
                            }
                            tr {
                                td { +"Expira em" }
                                td { +LocalDateTime.parse(linkEntry.expiresAt).format(formatter) }
                            }
                            tr {
                                td { +"Total de Visitas" }
                                td { +linkEntry.totalVisits.size.toString() }
                            }
                        }
                    }
                    if (linkEntry.totalVisits.isNotEmpty()) {
                        h2 { +"Detalhes dos Clicks" }
                        table {
                            thead {
                                tr {
                                    th { +"Região" }
                                    th { +"Dispositivo" }
                                    th { +"Sistema Operacional" }
                                    th { +"Navegador" }
                                    th { +"Referência" }
                                    th { +"Data do Click" }
                                }
                            }
                            tbody {
                                linkEntry.totalVisits.forEach { clicker ->
                                    tr {
                                        td { +"${clicker.region.country}, ${clicker.region.city}-${clicker.region.cityIsoCode}, ${clicker.region.timezone}" }
                                        td { +clicker.deviceInfo.deviceType }
                                        td { +clicker.deviceInfo.operatingSystem }
                                        td { +clicker.deviceInfo.browser }
                                        td { if(clicker.referer == "null") +UNKNOWN else +clicker.referer }
                                        td { +LocalDateTime.parse(clicker.clickedAt).format(formatter) }
                                    }
                                }
                            }
                        }
                    }
                    p {
                        +"Para mais informações ou suporte, entre em contato conosco."
                    }
                }
            }
        }
    }

    fun buildReportMail(username: String?, email: String?, text: String): String {
        return createHTML().html {
            head {
                style {
                    unsafe {
                        raw(
                            """
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 50px auto;
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 8px;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        }
                        h1 {
                            color: #333333;
                            text-align: center;
                        }
                        p {
                            color: #666666;
                            line-height: 1.5;
                        }
                        .footer {
                            text-align: center;
                            color: #999999;
                            font-size: 12px;
                            margin-top: 20px;
                        }
                    """.trimIndent()
                        )
                    }
                }
            }
            body {
                div("container") {
                    h1 { +"Relatório de Abuso/Sugestão" }
                    p {
                        +"Recebemos o seguinte relatório:"
                    }
                    p {
                        +text
                    }
                    username?.let {
                        p {
                            +"Usuário: $it"
                        }
                    }
                    email?.let {
                        p {
                            +"E-mail: $it"
                        }
                    }
                    p {
                        +"Se precisar de mais informações, por favor, entre em contato com o usuário."
                    }
                    div("footer") {
                        +"© 2024 Sanisamoj. Todos os direitos reservados."
                    }
                }
            }
        }
    }

    fun buildAccountRemovalMail(userId: String, email: String, reportType: String, report: String): String {
        return createHTML().html {
            head {
                style {
                    unsafe {
                        raw(
                            """
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 50px auto;
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 8px;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        }
                        h1 {
                            color: #333333;
                            text-align: center;
                        }
                        p {
                            color: #666666;
                            line-height: 1.5;
                        }
                        .footer {
                            text-align: center;
                            color: #999999;
                            font-size: 12px;
                            margin-top: 20px;
                        }
                    """.trimIndent()
                        )
                    }
                }
            }
            body {
                div("container") {
                    h1 { +"Solicitação de Remoção de Conta" }
                    p {
                        +"Recebemos o seguinte pedido de remoção de conta do sistema:"
                    }
                    p {
                        +"Tipo de Relatório: $reportType"
                    }
                    p {
                        +"Descrição do Relatório: $report"
                    }
                    p {
                        +"ID do Usuário: $userId"
                    }
                    p {
                        +"E-mail do Usuário: $email"
                    }
                    p {
                        +"Por favor, prossiga com a remoção da conta deste usuário conforme solicitado."
                    }
                    div("footer") {
                        +"© 2024 Sanisamoj. Todos os direitos reservados."
                    }
                }
            }
        }
    }

    fun buildPasswordResetTokenMail(username: String, resetLink: String): String {
        return createHTML().html {
            head {
                title("Redefinição de Senha")
                style {
                    unsafe {
                        raw(
                            """
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 50px auto;
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 8px;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        }
                        h1 {
                            color: #333333;
                            text-align: center;
                        }
                        p {
                            color: #666666;
                            line-height: 1.5;
                        }
                        .button {
                            display: block;
                            width: 200px;
                            margin: 20px auto;
                            padding: 10px;
                            text-align: center;
                            background-color: #007bff;
                            color: #ffffff;
                            text-decoration: none;
                            border-radius: 5px;
                            font-weight: bold;
                        }
                        .footer {
                            text-align: center;
                            color: #999999;
                            font-size: 12px;
                            margin-top: 20px;
                        }
                    """.trimIndent()
                        )
                    }
                }
            }
            body {
                div("container") {
                    h1 { +"Redefinição de Senha, $username!" }
                    p {
                        +"Você solicitou a redefinição de sua senha. Por favor, clique no link abaixo para continuar e criar uma nova senha."
                    }
                    a(href = resetLink, classes = "button") { +"Redefinir Senha" }
                    p {
                        +"Este link é válido por apenas 5 minutos. Após esse período, você precisará solicitar um novo link de redefinição."
                    }
                    p {
                        +"Se você não solicitou a redefinição de senha, por favor, ignore este e-mail."
                    }
                    p {
                        +"Atenciosamente,"
                        br()
                        +"Equipe Briefly"
                    }
                    div("footer") {
                        +"© 2024 Briefly. Todos os direitos reservados."
                    }
                }
            }
        }
    }
}