package com.jasiri.fieldofficer.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardOverviewEnvelope(
    val success: Boolean = true,
    val data: DashboardOverviewDto? = null
)

// Plain camelCase — DashboardOverviewDto deliberately has no @JsonProperty on the backend.
@Serializable
data class DashboardOverviewDto(
    val disbursements: DisbursementMetricsDto? = null,
    val collections: CollectionMetricsDto? = null
)

@Serializable
data class DisbursementMetricsDto(
    val total: Long = 0,
    val totalAmount: Double = 0.0,
    val today: Long = 0,
    val todayAmount: Double = 0.0,
    val thisMonth: Long = 0,
    val thisMonthAmount: Double = 0.0,
    val ytd: Long = 0,
    val ytdAmount: Double = 0.0
)

@Serializable
data class CollectionMetricsDto(
    val today: CollectionBucketDto? = null,
    val month: CollectionBucketDto? = null,
    val tomorrow: TomorrowBucketDto? = null
)

@Serializable
data class CollectionBucketDto(
    val collected: Double = 0.0,
    val expected: Double = 0.0,
    val rate: Double = 0.0
)

@Serializable
data class TomorrowBucketDto(
    val expected: Double = 0.0,
    val prepaid: Double = 0.0,
    val rate: Double = 0.0
)

@Serializable
data class LeadDashboardEnvelope(
    val success: Boolean = true,
    val data: LeadDashboardDto? = null
)

@Serializable
data class LeadDashboardDto(
    val kpis: LeadKpisDto? = null
)

// kpis itself is snake_case via explicit @JsonProperty on the backend, unlike DashboardOverviewDto.
@Serializable
data class LeadKpisDto(
    @SerialName("total_leads") val totalLeads: Long = 0,
    @SerialName("prospect_leads") val prospectLeads: Long = 0,
    @SerialName("cold_leads") val coldLeads: Long = 0,
    @SerialName("warm_leads") val warmLeads: Long = 0,
    @SerialName("hot_leads") val hotLeads: Long = 0,
    @SerialName("converted_leads") val convertedLeads: Long = 0,
    @SerialName("conversion_rate") val conversionRate: Double = 0.0
)
