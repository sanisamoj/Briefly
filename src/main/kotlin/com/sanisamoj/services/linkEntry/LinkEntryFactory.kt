package com.sanisamoj.services.linkEntry

import com.sanisamoj.data.models.dataclass.Clicker
import com.sanisamoj.data.models.dataclass.ClickerResponse
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.LinkEntryResponse
import com.sanisamoj.utils.analyzers.dotEnv

object LinkEntryFactory {
    fun linkEntryResponse(linkEntry: LinkEntry): LinkEntryResponse {
        val selfUrl: String = dotEnv("SELF_URL")
        val shortLink = "${selfUrl}/${linkEntry.shortLink}"
        val uniqueClickersResponseList: List<ClickerResponse> = linkEntry.uniqueClickers.map { clickerResponse(it) }
        val totalVisitsResponseList: List<ClickerResponse> = linkEntry.totalVisits.map { clickerResponse(it) }

        return LinkEntryResponse(
            id = linkEntry.id.toString(),
            userId = linkEntry.userId,
            active = linkEntry.active,
            shortLink = shortLink,
            originalLink = linkEntry.originalLink,
            uniqueClickers = uniqueClickersResponseList,
            totalVisits = totalVisitsResponseList,
            expiresAt = linkEntry.expiresAt,
        )
    }

    private fun clickerResponse(clicker: Clicker): ClickerResponse {
        return ClickerResponse(
            region = clicker.region,
            deviceInfo = clicker.deviceInfo,
            clickCount = clicker.clickCount,
            clickedAt = clicker.clickedAt,
        )
    }
}