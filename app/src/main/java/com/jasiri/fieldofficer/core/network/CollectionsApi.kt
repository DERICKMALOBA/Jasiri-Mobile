package com.jasiri.fieldofficer.core.network

import com.jasiri.fieldofficer.core.network.dto.CollectionItemsEnvelope
import retrofit2.http.GET
import retrofit2.http.Query

interface CollectionsApi {
    @GET("api/collections")
    suspend fun list(@Query("bucket") bucket: String): CollectionItemsEnvelope
}
