package com.jasiri.fieldofficer.feature.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jasiri.fieldofficer.core.network.dto.DashboardSummaryDto
import java.util.Locale

private data class QuickAction(val label: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNewCustomer: () -> Unit = {},
    onNewLead: () -> Unit = {},
    onStartRoute: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        val summary = state.summary ?: return@Column

        if (state.isPlaceholder) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Preview data — the officer dashboard endpoint isn't live on the backend yet",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        LoanOverviewCard(summary)

        Spacer(Modifier.height(16.dp))
        TodayGoalCard(summary)

        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ProgressRing("Leads MTD", summary.leadsProgressPct)
            ProgressRing("Disbursed MTD", summary.loansDisbursedProgressPct)
            ProgressRing("Collections MTD", summary.collectionsProgressPct)
        }

        Spacer(Modifier.height(24.dp))
        SectionHeading("Loan Portfolio")
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LoanPortfolioRow("Active", summary.activeLoansAmount, summary.activeLoansCount, MaterialTheme.colorScheme.primary)
                LoanPortfolioRow("Overdue", summary.overdueLoansAmount, summary.overdueLoansCount, MaterialTheme.colorScheme.error)
                LoanPortfolioRow("Fully Paid", summary.fullyPaidLoansAmount, summary.fullyPaidLoansCount, MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionHeading("Collections")
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LoanPortfolioRow("Due Today", summary.collectionsDueTodayAmount, summary.collectionsDueTodayCount, MaterialTheme.colorScheme.tertiary)
                LoanPortfolioRow("Due This Month", summary.collectionsDueThisMonthAmount, summary.collectionsDueThisMonthCount, MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionHeading("Quick Actions")
        Spacer(Modifier.height(8.dp))
        val quickActions = listOf(
            QuickAction("New Customer", Icons.Filled.PersonAdd, onNewCustomer),
            QuickAction("New Lead", Icons.Filled.PersonSearch, onNewLead),
            QuickAction("Start Route", Icons.Filled.Place, onStartRoute),
            QuickAction("Collect Payment", Icons.Filled.AccountBalanceWallet) {},
            QuickAction("My Targets", Icons.Filled.TrackChanges) {}
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            quickActions.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { action ->
                        QuickActionCard(action.label, action.icon, Modifier.weight(1f), action.onClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

/** The dashboard's headline element — loan exposure at a glance, replacing "Today's Goal" as the focal card. */
@Composable
private fun LoanOverviewCard(summary: DashboardSummaryDto) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Loan Overview", color = Color.White, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HeroStat("OLB", formatMoney(summary.outstandingLoanBalance), "${summary.olbCount} loans")
                HeroStat(
                    "Arrears",
                    formatMoney(summary.overdueLoansAmount),
                    "${summary.overdueLoansCount} loans"
                )
                HeroStat(
                    "Due Today",
                    formatMoney(summary.arrearsDueTodayAmount),
                    "${summary.arrearsDueTodayCount} loans"
                )
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, amount: String, countLabel: String) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(2.dp))
        Text(amount, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(countLabel, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
    }
}

/** Demoted from the old hero spot — still visible, no longer the loudest element on the screen. */
@Composable
private fun TodayGoalCard(summary: DashboardSummaryDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Today's Goal", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(summary.todayGoalLabel, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            val progress = if (summary.todayStopsTotal > 0) {
                summary.todayStopsPlanned / summary.todayStopsTotal.toFloat()
            } else 0f
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ProgressRing(label: String, percent: Int) {
    val ringColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 6.dp.toPx()
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * (percent.coerceIn(0, 100) / 100f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Text("$percent%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 72.dp)
        )
    }
}

@Composable
private fun LoanPortfolioRow(label: String, amount: Double, count: Int, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = color, shape = RoundedCornerShape(5.dp))
            )
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatMoney(amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(
                "$count loans",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatMoney(amount: Double): String =
    "KES " + String.format(Locale.US, "%,.0f", amount)

@Composable
private fun QuickActionCard(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.clickable(onClick = onClick)) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }
    }
}
