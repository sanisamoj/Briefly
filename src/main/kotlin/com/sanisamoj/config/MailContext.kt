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
                    h1 { +"Bem-vindo ao Briefly!, $username!" }
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
                        +"Equipe Sanisamoj"
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
        .info {
            margin-top: 20px;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .info h2 {
            margin-top: 0;
        }
        .info p {
            margin: 5px 0;
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
                        +"Obrigado por se registrar no Briefly!"
                    }

                    div("info") {
                        h2 { +"Sobre o Briefly" }
                        p {
                            +"Briefly é um serviço de encurtamento de links que oferece diversas funcionalidades para gerenciar e analisar seus links. Veja abaixo alguns recursos importantes:"
                        }
                        h3 { +"Informações Coletadas das Conexões" }
                        ul {
                            li { +"IP: O endereço IP do usuário será registrado para fins de análise e segurança." }
                            li { +"Geolocalização: Com base no endereço IP, a localização geográfica do usuário será determinada." }
                            li { +"Horário: O timestamp de cada conexão será registrado." }
                            li { +"Dispositivo: Informações sobre o dispositivo do usuário, incluindo tipo (desktop, mobile), sistema operacional (Windows, iOS, Android, etc.) e navegador (Chrome, Firefox, Safari, etc.)." }
                            li { +"URL de origem: A URL de origem da qual o usuário foi redirecionado para o link encurtado." }
                        }
                        h3 { +"Informações dos Links" }
                        ul {
                            li { +"Cliques: Número total de cliques em cada link encurtado." }
                            li { +"Filtros de Análise: Quantidade de cliques por hora, dia, semana e mês; Distribuição geográfica dos cliques; Análise dos cliques com base no tipo de dispositivo, sistema operacional e navegador." }
                        }
                        h3 { +"Funcionalidades dos Links" }
                        ul {
                            li { +"Links Expiráveis: Possibilidade de criar links que expiram após um determinado período." }
                            li { +"QR Code: Possibilidade de compartilhar um QR Code que contém o link encurtado." }
                            li { +"Proteção de Links: Os links terão a possibilidade de serem protegidos por senha." }
                        }
                        h3 { +"Funcionalidades do Sistema" }
                        ul {
                            li { +"Possibilidade de encurtar links (Estando ou não logado)." }
                            li { +"Possibilidade de acompanhar informações coletadas de cliques dos links." }
                            li { +"Notificações via E-mail aos usuários cadastrados sobre seus links." }
                            li { +"Gerenciar links como atualizar status ou removê-lo." }
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
                        +". O link que você criou foi deletado porque não foi acessado há mais de um ano."
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

}