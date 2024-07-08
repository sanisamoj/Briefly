package com.sanisamoj.data.pages

import kotlinx.html.*

fun HTML.confirmationPage() {
    head {
        title("Criação de Conta Confirmada")
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
                    .checkmark-container {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        margin-top: 2em;
                    }
                    .checkmark {
                        width: 50px;
                        height: 50px;
                        border-radius: 50%;
                        background-color: #4caf50;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }
                    .checkmark::after {
                        content: '✔';
                        color: white;
                        font-size: 1.5em;
                    }
                """.trimIndent()
            }
        }
    }
    body {
        div("container") {
            h1 { +"Conta Criada com Sucesso!" }
            p { +"Sua conta foi criada e confirmada com sucesso." }
            div("checkmark-container") {
                div("checkmark") {}  // Círculo com o ícone de verificação
            }
            a(href = "/") { +"Voltar para a Página Inicial" }
        }
    }
}