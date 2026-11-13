package com.jasiri.fieldofficer.feature.collections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CollectPaymentScreen(viewModel: CollectPaymentViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Collect Payment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Send an M-Pesa STK push — the customer enters their PIN on their own phone to pay.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        if (state.pendingTxId == null) {
            PaymentForm(state, viewModel)
        } else {
            PaymentStatusView(state, onNewPayment = viewModel::startNewPayment)
        }
    }
}

@Composable
private fun PaymentForm(state: CollectPaymentUiState, viewModel: CollectPaymentViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Customer's Phone Number") },
            placeholder = { Text("0712345678") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.amount,
            onValueChange = viewModel::onAmountChange,
            label = { Text("Amount (KES)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = viewModel::sendStkPush,
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Send STK Push")
            }
        }
    }
}

@Composable
private fun PaymentStatusView(state: CollectPaymentUiState, onNewPayment: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 32.dp)) {
        when {
            state.status == "applied" -> StatusIcon(Icons.Filled.CheckCircle, MaterialTheme.colorScheme.primary)
            state.status == "suspense" -> StatusIcon(Icons.Filled.Warning, Color(0xFFB8860B))
            state.status == "failed" -> StatusIcon(Icons.Filled.Error, MaterialTheme.colorScheme.error)
            state.timedOut -> StatusIcon(Icons.Filled.Warning, Color(0xFFB8860B))
            else -> CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }

        Spacer(Modifier.height(16.dp))
        Text(
            statusHeadline(state),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            statusDetail(state),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (state.isTerminal || state.timedOut) {
            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = onNewPayment, modifier = Modifier.fillMaxWidth()) {
                Text("New Payment")
            }
        }
    }
}

@Composable
private fun StatusIcon(icon: ImageVector, tint: Color) {
    Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(64.dp))
    }
}

private fun statusHeadline(state: CollectPaymentUiState): String = when {
    state.status == "applied" -> "Payment Received"
    state.status == "suspense" -> "Payment Received — Needs Review"
    state.status == "failed" -> "Payment Failed"
    state.timedOut -> "Still Waiting"
    else -> "Waiting for Customer"
}

private fun statusDetail(state: CollectPaymentUiState): String = when {
    state.status == "applied" -> "KES ${state.amount} from ${state.phone} was received and applied."
    state.status == "suspense" -> "Safaricom confirmed the payment but it couldn't be auto-matched. Check with your supervisor."
    state.status == "failed" -> state.statusDetail ?: "The customer may have cancelled or entered the wrong PIN."
    state.timedOut -> "No result yet — ask the customer to check their phone, or try again shortly."
    else -> "Ask ${state.phone} to check their phone and enter their M-Pesa PIN."
}
