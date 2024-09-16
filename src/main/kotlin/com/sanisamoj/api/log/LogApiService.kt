package com.sanisamoj.api.log

import com.sanisamoj.data.models.dataclass.ApplicationServiceLoginRequest
import com.sanisamoj.data.models.dataclass.CreateEventRequest
import com.sanisamoj.data.models.dataclass.LogEventResponse
import com.sanisamoj.data.models.dataclass.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LogApiService {

    @POST("application/login")
    suspend fun applicationLogin(
        @Body loginRequest: ApplicationServiceLoginRequest
    ) : TokenResponse

    @POST("application/login")
    suspend fun registerLog(
        @Body event: CreateEventRequest,
        @Header("Authorization") token: String
    ) : LogEventResponse

}