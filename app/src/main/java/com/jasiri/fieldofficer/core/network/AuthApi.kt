package com.jasiri.fieldofficer.core.network

import com.jasiri.fieldofficer.core.network.dto.LoginRequest
import com.jasiri.fieldofficer.core.network.dto.LoginResponse
import com.jasiri.fieldofficer.core.network.dto.RefreshRequest
import com.jasiri.fieldofficer.core.network.dto.RefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * NOTE: requires the backend login/refresh change described in
 * docs/Field-Officer-Mobile-App-Plan.pdf §4 — mobile clients need the JWT back
 * in the JSON response body, not only in the web's httpOnly cookie.
 * `SessionAuthenticationFilter` already accepts the resulting bearer token.
 */
interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): RefreshResponse
}
