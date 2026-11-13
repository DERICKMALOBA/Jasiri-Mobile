package com.jasiri.fieldofficer.feature.dashboard

import com.jasiri.fieldofficer.core.network.DashboardApi
import com.jasiri.fieldofficer.core.network.dto.DashboardSummaryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val dashboardApi: DashboardApi
) {
    /**
     * Returns the summary plus whether it's placeholder data. The real
     * endpoint doesn't exist on the backend yet (see DashboardApi), so this
     * fails over to a clearly-labeled placeholder instead of crashing the
     * Home screen.
     */
    suspend fun getSummary(): Pair<DashboardSummaryDto, Boolean> {
        return try {
            dashboardApi.getSummary() to false
        } catch (e: Exception) {
            PLACEHOLDER to true
        }
    }

    private companion object {
        val PLACEHOLDER = DashboardSummaryDto(
            officerName = "Officer",
            todayGoalLabel = "Today's route isn't set up yet",
            todayStopsPlanned = 0,
            todayStopsTotal = 1,
            leadsProgressPct = 0,
            loansDisbursedProgressPct = 0,
            collectionsProgressPct = 0,
            leadsCount = 12,
            outstandingLoanBalance = 862_400.0,
            olbCount = 48,
            arrearsDueTodayAmount = 14_200.0,
            arrearsDueTodayCount = 3,
            activeLoansAmount = 724_600.0,
            activeLoansCount = 34,
            overdueLoansAmount = 97_800.0,
            overdueLoansCount = 8,
            fullyPaidLoansAmount = 40_000.0,
            fullyPaidLoansCount = 6,
            collectionsDueTodayAmount = 22_500.0,
            collectionsDueTodayCount = 5,
            collectionsDueThisMonthAmount = 318_900.0,
            collectionsDueThisMonthCount = 41
        )
    }
}
