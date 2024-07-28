package com.sanisamoj.services.linkEntry

import com.sanisamoj.config.GlobalContext.SELF_URL
import com.sanisamoj.data.models.dataclass.*

object LinkEntryFactory {
    fun linkEntryResponse(linkEntry: LinkEntry): LinkEntryResponse {
        val selfUrl: String = SELF_URL
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
            createdAt = linkEntry.createdAt
        )
    }

    fun midLinkEntryResponse(linkEntry: LinkEntry): MidLinkEntryResponse {
        val selfUrl: String = SELF_URL
        val shortLink = "${selfUrl}/${linkEntry.shortLink}"
        val qrCodeLink = "${selfUrl}/qrcode?short=${linkEntry.shortLink}"
        val totalVisitsResponseList: List<ClickerResponse> = linkEntry.totalVisits.map { clickerResponse(it) }

        return MidLinkEntryResponse(
            active = linkEntry.active,
            shortLink = shortLink,
            qrCodeLink = qrCodeLink,
            originalLink = linkEntry.originalLink,
            totalVisits = totalVisitsResponseList,
            expiresAt = linkEntry.expiresAt,
            createAt = linkEntry.createdAt
        )
    }

    fun linkEntryResponseToMidLinkEntryResponse(linkEntryResponse: LinkEntryResponse): MidLinkEntryResponse {
        return MidLinkEntryResponse(
            active = linkEntryResponse.active,
            shortLink = linkEntryResponse.shortLink,
            qrCodeLink = linkEntryResponse.qrCodeLink,
            originalLink = linkEntryResponse.originalLink,
            totalVisits = linkEntryResponse.totalVisits,
            expiresAt = linkEntryResponse.expiresAt,
            createAt = linkEntryResponse.createdAt
        )
    }

    fun clickerResponse(clicker: Clicker): ClickerResponse {
        return ClickerResponse(
            region = clicker.region,
            deviceInfo = clicker.deviceInfo,
            clickedAt = clicker.clickedAt,
        )
    }
}