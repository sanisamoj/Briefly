package com.sanisamoj.utils.generators

import kotlin.random.Random

object CharactersGenerator {
    fun codeValidationGenerate(): Int {
        return Random.nextInt(100_000, 1_000_000)
    }

    fun randomCodeGenerate(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%&*?"
        val sb = StringBuilder(length)
        val random = Random.Default

        repeat(length) {
            val index = random.nextInt(characters.length)
            sb.append(characters[index])
        }

        return sb.toString()
    }

    // Gera um conjunto de caracteres , com caracteres aceitos como nomes
    fun generateWithNoSymbols(maxChat: Int = 5): String {

        // Caracteres permitidos
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789@$&?!"

        // Irá gerar um conjunto de caracteres
        val characters = (1..maxChat).map{ chars.random() }.joinToString("")

        return characters

    }
}