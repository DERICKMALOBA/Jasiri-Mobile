package com.jasiri.fieldofficer.core.network

import com.jasiri.fieldofficer.core.network.dto.CreateCustomerRequest
import com.jasiri.fieldofficer.core.network.dto.CreateCustomerResponseDto
import com.jasiri.fieldofficer.core.network.dto.CustomerBundleEnvelope
import com.jasiri.fieldofficer.core.network.dto.CustomerListEnvelope
import com.jasiri.fieldofficer.core.network.dto.UploadResultDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Mirrors springboot-backend's CustomerController (`/api/registry/...`): photos go up as
 * multipart to `/upload` first, and the returned public URL is referenced in the JSON
 * create/update call to `/customers/full` — there is no combined multipart-create endpoint.
 */
interface CustomerApi {
    @GET("api/registry/customers")
    suspend fun listCustomers(): CustomerListEnvelope

    @GET("api/registry/customers/search")
    suspend fun searchCustomers(@Query("q") query: String): CustomerListEnvelope

    @GET("api/registry/customers/{id}/full")
    suspend fun getCustomerFull(@Path("id") id: Long): CustomerBundleEnvelope

    @Multipart
    @POST("api/registry/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("bucket") bucket: RequestBody,
        @Part("path") path: RequestBody,
        @Part("contentHash") contentHash: RequestBody
    ): UploadResultDto

    @POST("api/registry/customers/full")
    suspend fun createCustomerFull(@Body request: CreateCustomerRequest): CreateCustomerResponseDto
}
