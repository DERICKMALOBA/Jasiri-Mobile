package com.jasiri.fieldofficer.feature.performance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jasiri.fieldofficer.core.network.dto.CollectionBucketDto
import com.jasiri.fieldofficer.core.network.dto.DisbursementMetricsDto
import com.jasiri.fieldofficer.core.network.dto.LeadKpisDto
import com.jasiri.fieldofficer.core.performance.PerformanceSummary
import java.util.Locale

@Composable
fun PerformanceScreen(viewModel: PerformanceViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("My Performance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Your own actuals, month-to-date and year-to-date.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))

        when {
            state.isLoading -> Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = viewModel::load) { Text("Retry") }
                }
            }
            state.summary != null -> PerformanceContent(state.summary!!)
        }
    }
}

@Composable
private fun PerformanceContent(summary: PerformanceSummary) {
    SectionHeading("Loans Disbursed")
    Spacer(Modifier.height(8.dp))
    DisbursementsCard(summary.disbursements)

    Spacer(Modifier.height(24.dp))
    SectionHeading("Collections")
    Spacer(Modifier.height(8.dp))
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CollectionBucketRow("Today", summary.collections.today ?: CollectionBucketDto())
            CollectionBucketRow("This Month", summary.collections.month ?: CollectionBucketDto())
        }
    }

    Spacer(Modifier.height(24.dp))
    SectionHeading("Leads")
    Spacer(Modifier.height(8.dp))
    LeadsCard(summary.leadKpis)
}

@Composable
private fun SectionHeading(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun DisbursementsCard(d: DisbursementMetricsDto) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DisbursementMetricRow("Today", d.today, d.todayAmount)
            DisbursementMetricRow("This Month", d.thisMonth, d.thisMonthAmount)
            DisbursementMetricRow("Year to Date", d.ytd, d.ytdAmount)
        }
    }
}

@Composable
private fun DisbursementMetricRow(label: String, count: Long, amount: Double) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Column(horizontalAlignment = Alignment.End) {
            Text(formatMoney(amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(
                "$count loan${if (count == 1L) "" else "s"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CollectionBucketRow(label: String, bucket: CollectionBucketDto) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text("${(bucket.rate * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(progress = bucket.rate.toFloat().coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(4.dp))
        Text(
            "${formatMoney(bucket.collected)} collected of ${formatMoney(bucket.expected)} expected",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LeadsCard(kpis: LeadKpisDto) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                LeadStat("Total", kpis.totalLeads.toString())
                LeadStat("Converted", kpis.convertedLeads.toString())
                LeadStat("Conv. Rate", "${(kpis.conversionRate * 100).toInt()}%")
            }
            Spacer(Modifier.height(16.dp))
            Text("Pipeline", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PipelineChip("Prospect", kpis.prospectLeads)
                PipelineChip("Cold", kpis.coldLeads)
                PipelineChip("Warm", kpis.warmLeads)
                PipelineChip("Hot", kpis.hotLeads)
            }
        }
    }
}

@Composable
private fun LeadStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PipelineChip(label: String, count: Long) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun formatMoney(amount: Double): String =
    "KES " + String.format(Locale.US, "%,.0f", amount)
