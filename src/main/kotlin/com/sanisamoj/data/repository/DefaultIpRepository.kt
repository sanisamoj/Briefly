package com.sanisamoj.data.repository

import com.sanisamoj.api.geo.GeoIPService
import com.sanisamoj.data.models.dataclass.AsnInfo
import com.sanisamoj.data.models.dataclass.CityInfo
import com.sanisamoj.data.models.dataclass.IpInfo
import com.sanisamoj.data.models.interfaces.IpRepository

class DefaultIpRepository(private val geoIpService: GeoIPService = GeoIPService) : IpRepository {
    override suspend fun getInfoByIp(ip: String): IpInfo {
        val cityInfo: CityInfo? = try {
            geoIpService.getCityInfo(ip)
        } catch (_: Throwable) {
            null
        }

        val asnInfo: AsnInfo? = try {
            geoIpService.getASNInfo(ip)
        } catch (_: Throwable) {
            null
        }

        val ipInfo: IpInfo = ipInfoFactory(ip, cityInfo, asnInfo)
        return ipInfo
    }

    private fun ipInfoFactory(ip: String, cityInfo: CityInfo?, asnInfo: AsnInfo?): IpInfo {
        return IpInfo(
            ip = ip,
            hostname = asnInfo?.autonomousSystemOrganization,
            city = cityInfo?.cityName,
            cityIsoCode = cityInfo?.subdivisions?.get(0)?.isoCode,
            country = cityInfo?.countryName,
            countryIsoCode = cityInfo?.countryIsoCode,
            continent = cityInfo?.continentName,
            latitude = cityInfo?.latitude,
            longitude = cityInfo?.longitude,
            postal = cityInfo?.postalCode,
            timezone = cityInfo?.timeZone
        )
    }
}