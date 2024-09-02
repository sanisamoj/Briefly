package com.sanisamoj.utils.generators

import kotlin.random.Random

object CharactersGenerator {
    // Generates a character set, with characters accepted as names
    fun generateWithNoSymbols(maxChat: Int = 5): String {

        // Allowed characters
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789@$"

        // Will generate a set of characters
        val characters = (1..maxChat).map{ chars.random() }.joinToString("")

        return characters

    }

    fun codeValidationGenerate(): Int {
        return Random.nextInt(100_000, 1_000_000)
    }
}