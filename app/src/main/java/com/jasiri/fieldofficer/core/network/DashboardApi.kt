package com.jasiri.fieldofficer.core.network

import com.jasiri.fieldofficer.core.network.dto.DashboardSummaryDto
import retrofit2.http.GET

/**
 * NOTE: GET /api/officer/dashboard does not exist on the backend yet — it is
 * one of the Assignment/Targets rows in the backend-additions table in
 * docs/Field-Officer-Mobile-App-Plan.pdf §10. Until it ships, DashboardRepository
 * falls back to placeholder data so the app stays navigable.
 */
interface DashboardApi {
    @GET("api/officer/dashboard")
    suspend fun getSummary(): DashboardSummaryDto
}
