package com.jasiri.fieldofficer.core.performance

import com.jasiri.fieldofficer.BuildConfig
import com.jasiri.fieldofficer.core.network.PerformanceApi
import com.jasiri.fieldofficer.core.network.dto.CollectionBucketDto
import com.jasiri.fieldofficer.core.network.dto.CollectionMetricsDto
import com.jasiri.fieldofficer.core.network.dto.DisbursementMetricsDto
import com.jasiri.fieldofficer.core.network.dto.LeadKpisDto
import com.jasiri.fieldofficer.core.network.dto.TomorrowBucketDto
import javax.inject.Inject
import javax.inject.Singleton

data class PerformanceSummary(
    val disbursements: DisbursementMetricsDto,
    val collections: CollectionMetricsDto,
    val leadKpis: LeadKpisDto
)

@Singleton
class PerformanceRepository @Inject constructor(
    private val api: PerformanceApi
) {
    suspend fun getPerformance(): Result<PerformanceSummary> = runCatching {
        val overview = api.getOverview().data
        val leads = api.getLeadsDashboard().data
        PerformanceSummary(
            disbursements = overview?.disbursements ?: DisbursementMetricsDto(),
            collections = overview?.collections ?: CollectionMetricsDto(),
            leadKpis = leads?.kpis ?: LeadKpisDto()
        )
    }.recoverIfDebugAndFailed { mockSummary() }

    // Read-only display data — unlike STK push, faking this when the backend/DB is down is safe
    // and matches the precedent set for Customers/Dashboard/Routes/Collections.
    private inline fun <T> Result<T>.recoverIfDebugAndFailed(fallback: () -> T): Result<T> =
        if (isFailure && BuildConfig.DEBUG) Result.success(fallback()) else this

    private fun mockSummary() = PerformanceSummary(
        disbursements = DisbursementMetricsDto(
            total = 48, totalAmount = 1_240_000.0,
            today = 1, todayAmount = 25_000.0,
            thisMonth = 9, thisMonthAmount = 212_000.0,
            ytd = 48, ytdAmount = 1_240_000.0
        ),
        collections = CollectionMetricsDto(
            today = CollectionBucketDto(collected = 22_500.0, expected = 30_000.0, rate = 0.75),
            month = CollectionBucketDto(collected = 318_900.0, expected = 410_000.0, rate = 0.78),
            tomorrow = TomorrowBucketDto(expected = 27_000.0, prepaid = 4_000.0, rate = 0.15)
        ),
        leadKpis = LeadKpisDto(
            totalLeads = 20, prospectLeads = 6, coldLeads = 4, warmLeads = 5, hotLeads = 5,
            convertedLeads = 12, conversionRate = 0.6
        )
    )
}
