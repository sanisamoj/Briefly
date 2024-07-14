package com.sanisamoj.api

import com.sanisamoj.data.models.dataclass.IpInfo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IpInfoApiService {
    @GET("{ip}")
    suspend fun getIpInfo(@Path("ip") ip: String, @Query("token") token: String): IpInfo
}