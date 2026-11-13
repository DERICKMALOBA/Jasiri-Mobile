package com.jasiri.fieldofficer.feature.customer.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jasiri.fieldofficer.core.network.dto.CustomerBundleDto

private enum class DetailTab(val label: String) { OVERVIEW("Overview"), LOANS("Loans"), HISTORY("History"), DOCUMENTS("Documents") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    viewModel: CustomerDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(DetailTab.OVERVIEW) }
    val context = LocalContext.current

    LaunchedEffect(customerId) { viewModel.load(customerId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Profile") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
            }
            state.bundle != null -> {
                val bundle = state.bundle!!
                val customer = bundle.customer

                Column(Modifier.fillMaxSize().padding(padding)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        androidx.compose.material3.Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    customer?.fullName?.take(2)?.uppercase() ?: "?",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(customer?.fullName ?: "Unknown customer", style = MaterialTheme.typography.titleLarge)
                        Text(
                            customer?.mobile ?: "—",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (customer?.isKycComplete == true) {
                            Spacer(Modifier.height(4.dp))
                            AssistChip(onClick = {}, label = { Text("KYC Verified") })
                        }
                    }

                    TabRow(selectedTabIndex = selectedTab.ordinal) {
                        DetailTab.values().forEach { tab ->
                            Tab(selected = selectedTab == tab, onClick = { selectedTab = tab }, text = { Text(tab.label) })
                        }
                    }

                    when (selectedTab) {
                        DetailTab.OVERVIEW -> OverviewTab(bundle, customer?.mobile, context)
                        DetailTab.LOANS -> LoansTab(bundle)
                        DetailTab.HISTORY -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Visit and payment history isn't wired up yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        DetailTab.DOCUMENTS -> DocumentsTab(bundle)
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(bundle: CustomerBundleDto, mobile: String?, context: Context) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        bundle.loans.firstOrNull()?.let { loan ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(loan.productName ?: "Loan", fontWeight = FontWeight.Bold)
                    Text("Due ${loan.dueDate ?: "—"}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Balance: KES ${loan.balance ?: 0.0}")
                    loan.overdueAmount?.takeIf { it > 0 }?.let {
                        Text("KES $it overdue", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionIcon(Icons.Filled.Call, "Call") {
                if (!mobile.isNullOrBlank()) context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobile")))
            }
            ActionIcon(Icons.Filled.Sms, "SMS") {
                if (!mobile.isNullOrBlank()) context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$mobile")))
            }
            ActionIcon(Icons.Filled.AccountBalanceWallet, "Collect", emphasized = true) {
                // TODO: wire to the Collections/STK-push screen once it is built
            }
            ActionIcon(Icons.Filled.NoteAdd, "New App") {
                // TODO: wire to the Loan Application screen once it is built
            }
        }
    }
}

@Composable
private fun ActionIcon(icon: ImageVector, label: String, emphasized: Boolean = false, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(
            onClick = onClick,
            colors = if (emphasized) {
                IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.secondary)
            } else {
                IconButtonDefaults.filledTonalIconButtonColors()
            }
        ) { Icon(icon, contentDescription = label) }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun LoansTab(bundle: CustomerBundleDto) {
    if (bundle.loans.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("No loans on file for this customer.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(bundle.loans) { loan ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(loan.productName ?: "Loan #${loan.id}", fontWeight = FontWeight.Bold)
                    Text("Balance: KES ${loan.balance ?: 0.0}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun DocumentsTab(bundle: CustomerBundleDto) {
    val images = listOfNotNull(bundle.customer?.passportUrl, bundle.customer?.idFrontUrl, bundle.customer?.idBackUrl) +
        bundle.businessImages.mapNotNull { it.imageUrl }
    if (images.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("No documents uploaded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(images) { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
        }
    }
}
