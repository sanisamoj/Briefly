package com.sanisamoj.config

import kotlinx.html.*
import kotlinx.html.stream.createHTML

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
                    h1 { +"Bem-vindo ao Snapurl!, $username!" }
                    p {
                        +"Estamos muito felizes em tê-lo conosco. Você precisa ativar a sua conta. "
                    }
                    p {
                        +"Por favor, clique no link abaixo para confirmar o seu endereço de e-mail e ativar sua conta:"
                    }
                    a(href = activationLink, classes = "button") { +"Ativar Conta" }
                    p {
                        +"Caso o link acima não funcione, copie e cole a URL no seu navegador:"
                    }
                    p {
                        +"$activationLink"
                    }
                    p {
                        +"O código estará válido apenas por 5 minutos."
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

}