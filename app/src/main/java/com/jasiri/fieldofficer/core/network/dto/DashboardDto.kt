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
    val collectionsProgressPct: Int,
    val leadsCount: Int = 0,
    val totalLoansCount: Int = 0,
    val outstandingLoanBalance: Double = 0.0,
    val cleanBookPct: Int = 0,
    val collectionRatePct: Int = 0,
    val portfolioAtRiskPct: Int = 0,
    val activeLoansCount: Int = 0,
    val overdueLoansCount: Int = 0,
    val fullyPaidLoansCount: Int = 0
)
