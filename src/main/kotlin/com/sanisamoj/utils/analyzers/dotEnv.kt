package com.sanisamoj.utils.analyzers

import io.github.cdimascio.dotenv.Dotenv

fun dotEnv(secretName: String?): String {
    val dotenv : Dotenv = Dotenv.configure().ignoreIfMissing().load()
    return dotenv[secretName].toString()
}