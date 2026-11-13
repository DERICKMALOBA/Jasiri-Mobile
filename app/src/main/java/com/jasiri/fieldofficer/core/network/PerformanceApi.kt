package com.jasiri.fieldofficer.core.network

import com.jasiri.fieldofficer.core.network.dto.DashboardOverviewEnvelope
import com.jasiri.fieldofficer.core.network.dto.LeadDashboardEnvelope
import retrofit2.http.GET

interface PerformanceApi {
    // No query params — for an officer-role JWT (scope "own"), the backend filters by the
    // caller's own bookedBy/createdBy automatically; region/branch/ro params only matter for
    // manager-level scopes.
    @GET("api/dashboard/overview")
    suspend fun getOverview(): DashboardOverviewEnvelope

    @GET("api/leads/dashboard")
    suspend fun getLeadsDashboard(): LeadDashboardEnvelope
}
