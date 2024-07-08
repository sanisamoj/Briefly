package com.sanisamoj.data.pages

import kotlinx.html.*

fun HTML.tokenExpiredPage() {
    head {
        title("Token Expirado")
        style {
            unsafe {
                +"""
                    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
                    body {
                        font-family: 'Roboto', sans-serif;
                        background-color: #f4f4f9;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                    }
                    .container {
                        background-color: white;
                        padding: 2em;
                        border-radius: 8px;
                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        text-align: center;
                    }
                    h1 {
                        color: #333;
                    }
                    p {
                        color: #666;
                    }
                    a {
                        display: inline-block;
                        margin-top: 1em;
                        padding: 0.5em 1em;
                        color: white;
                        background-color: #6f42c1;
                        border-radius: 4px;
                        text-decoration: none;
                    }
                    a:hover {
                        background-color: #5930a0;
                    }
                    .expired-container {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        margin-top: 2em;
                    }
                    .expired-icon {
                        width: 50px;
                        height: 50px;
                        border-radius: 50%;
                        background-color: #f44336;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }
                    .expired-icon::after {
                        content: '✘';
                        color: white;
                        font-size: 1.5em;
                    }
                """.trimIndent()
            }
        }
    }
    body {
        div("container") {
            h1 { +"Token Expirado" }
            p { +"O token de confirmação de conta expirou. Por favor, solicite um novo token para continuar." }
            div("expired-container") {
                div("expired-icon") {}  // Círculo com o ícone de expiração
            }
            a(href = "/request-new-token") { +"Solicitar Novo Token" }
        }
    }
}
