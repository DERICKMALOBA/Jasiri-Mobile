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
    // Outstanding Loan Balance — headline loan overview.
    val outstandingLoanBalance: Double = 0.0,
    val olbCount: Int = 0,
    // Arrears = total overdue across the portfolio; same figures as the Overdue row below.
    val arrearsDueTodayAmount: Double = 0.0,
    val arrearsDueTodayCount: Int = 0,
    // Loan Portfolio breakdown — amount + count per bucket.
    val activeLoansAmount: Double = 0.0,
    val activeLoansCount: Int = 0,
    val overdueLoansAmount: Double = 0.0,
    val overdueLoansCount: Int = 0,
    val fullyPaidLoansAmount: Double = 0.0,
    val fullyPaidLoansCount: Int = 0,
    // Collections due.
    val collectionsDueTodayAmount: Double = 0.0,
    val collectionsDueTodayCount: Int = 0,
    val collectionsDueThisMonthAmount: Double = 0.0,
    val collectionsDueThisMonthCount: Int = 0
)
