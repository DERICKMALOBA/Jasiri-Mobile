package com.jasiri.fieldofficer.core.network

import com.jasiri.fieldofficer.core.network.dto.StkPushInitiateRequest
import com.jasiri.fieldofficer.core.network.dto.StkPushInitiateResponse
import com.jasiri.fieldofficer.core.network.dto.StkPushStatusDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StkPushApi {
    @POST("api/c2b/stkpush")
    suspend fun initiate(@Body request: StkPushInitiateRequest): StkPushInitiateResponse

    @GET("api/c2b/stkpush/status/{id}")
    suspend fun getStatus(@Path("id") id: String): StkPushStatusDto
}
