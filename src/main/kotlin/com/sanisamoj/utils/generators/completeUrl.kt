package com.sanisamoj.utils.generators

import okhttp3.HttpUrl

fun completeUrl(url: String, defaultScheme: String = "https"): String {
    return if (!url.startsWith("http://") && !url.startsWith("https://")) {
        "$defaultScheme://$url"
    } else {
        url
    }
}

fun completeAndBuildUrl(baseUrl: String): String? {
    val completeBaseUrl = completeUrl(baseUrl)

    val urlBuilder = HttpUrl.parse(completeBaseUrl)?.newBuilder()
        ?: return null

    return urlBuilder.build().toString()
}
