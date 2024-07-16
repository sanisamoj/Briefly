package com.sanisamoj.services.linkEntry

import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.utils.analyzers.dotEnv

object LinkEntryFactory {
    fun linkEntryResponse(linkEntry: LinkEntry): LinkEntryResponse {
        val selfUrl: String = dotEnv("SELF_URL")
        val shortLink = "${selfUrl}/${linkEntry.shortLink}"
        val qrCodeLink = "${selfUrl}/qrcode?short=${linkEntry.shortLink}"
        val totalVisitsResponseList: List<ClickerResponse> = linkEntry.totalVisits.map { clickerResponse(it) }

        return LinkEntryResponse(
            userId = linkEntry.userId,
            active = linkEntry.active,
            shortLink = shortLink,
            qrCodeLink = qrCodeLink,
            originalLink = linkEntry.originalLink,
            totalVisits = totalVisitsResponseList,
            expiresAt = linkEntry.expiresAt,
        )
    }

    fun midLinkEntryResponse(linkEntry: LinkEntry): MidLinkEntryResponse {
        val selfUrl: String = dotEnv("SELF_URL")
        val shortLink = "${selfUrl}/${linkEntry.shortLink}"
        val qrCodeLink = "${selfUrl}/qrcode?short=${linkEntry.shortLink}"

        return MidLinkEntryResponse(
            active = linkEntry.active,
            shortLink = shortLink,
            qrCodeLink = qrCodeLink,
            originalLink = linkEntry.originalLink,
            expiresAt = linkEntry.expiresAt,
        )
    }

    private fun clickerResponse(clicker: Clicker): ClickerResponse {
        return ClickerResponse(
            region = clicker.region,
            deviceInfo = clicker.deviceInfo,
            clickedAt = clicker.clickedAt,
        )
    }
}