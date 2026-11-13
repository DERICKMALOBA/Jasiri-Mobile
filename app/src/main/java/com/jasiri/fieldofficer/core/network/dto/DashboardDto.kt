package com.jasiri.fieldofficer.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardSummaryDto(
    val officerName: String,
    val todayGoalLabel: String,
    val todayStopsPlanned: Int,
    val todayStopsTotal: Int,
    val leadsProgressPct: Int,
    val loansDisbursedProgressPct: Int,
    val collectionsProgressPct: Int
)
