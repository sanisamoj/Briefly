package com.sanisamoj.api.geo

import com.maxmind.geoip2.DatabaseReader
import com.sanisamoj.data.models.dataclass.AsnInfo
import com.sanisamoj.data.models.dataclass.CityInfo
import com.sanisamoj.data.models.dataclass.SubdivisionInfo
import com.sanisamoj.utils.analyzers.ResourceLoader
import java.net.InetAddress

object GeoIPService {

    private val cityDbReader: DatabaseReader by lazy {
        val cityDbFile = ResourceLoader.loadResourceAsStream("/geo/GeoLite2-City.mmdb")
        DatabaseReader.Builder(cityDbFile).build()
    }

    private val asnDbReader: DatabaseReader by lazy {
        val asnDbFile = ResourceLoader.loadResourceAsStream("/geo/GeoLite2-ASN.mmdb")
        DatabaseReader.Builder(asnDbFile).build()
    }

    /**
     * Gets city information for a given IP address.
     *
     * @param ipAddress The IP address to look up.
     * @return CityInfo containing city information.
     */
    fun getCityInfo(ipAddress: String): CityInfo? {
        val inetAddress = InetAddress.getByName(ipAddress)
        val response = cityDbReader.city(inetAddress)
        return response?.let {
            CityInfo(
                continentCode = it.continent.code,
                continentName = it.continent.name,
                countryIsoCode = it.country.isoCode,
                countryName = it.country.name,
                cityGeonameId = it.city.geoNameId,
                cityName = it.city.name,
                latitude = it.location.latitude,
                longitude = it.location.longitude,
                timeZone = it.location.timeZone,
                postalCode = it.postal.code,
                subdivisions = it.subdivisions.map { sub ->
                    SubdivisionInfo(
                        isoCode = sub.isoCode,
                        geonameId = sub.geoNameId,
                        name = sub.name
                    )
                }
            )
        }
    }

    /**
     * Gets ASN information for a given IP address.
     *
     * @param ipAddress The IP address to look up.
     * @return AsnInfo containing ASN information.
     */
    fun getASNInfo(ipAddress: String): AsnInfo? {
        val inetAddress = InetAddress.getByName(ipAddress)
        val response = asnDbReader.asn(inetAddress)
        return response?.let {
            AsnInfo(
                autonomousSystemNumber = it.autonomousSystemNumber,
                autonomousSystemOrganization = it.autonomousSystemOrganization
            )
        }
    }
}
