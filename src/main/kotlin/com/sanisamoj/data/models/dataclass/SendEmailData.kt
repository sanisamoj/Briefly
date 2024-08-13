package com.sanisamoj.data.models.dataclass

data class SendEmailData(
    val to: String,
    val topic: String,
    val text: String,
    val isHtml: Boolean = false
)