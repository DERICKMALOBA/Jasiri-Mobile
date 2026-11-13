package com.jasiri.fieldofficer.feature.customer.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jasiri.fieldofficer.feature.customer.capture.IdCaptureScreen
import java.io.File

private const val ID_CARD_ASPECT = 1.586f
private const val SELFIE_ASPECT = 0.75f

private data class CaptureSpec(
    val title: String,
    val instruction: String,
    val aspectRatio: Float,
    val useFrontCamera: Boolean,
    val onCaptured: (File) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    viewModel: AddCustomerViewModel = hiltViewModel(),
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val onSelfieStep = state.step == 5 && state.selfieFile == null
    if (state.step in 3..4 || onSelfieStep) {
        val spec = when (state.step) {
            3 -> CaptureSpec(
                "Front of National ID",
                "Align the front of the ID within the frame.",
                ID_CARD_ASPECT,
                useFrontCamera = false
            ) { viewModel.onIdFrontCaptured(it) }
            4 -> CaptureSpec(
                "Back of National ID",
                "Align the back of the ID within the frame.",
                ID_CARD_ASPECT,
                useFrontCamera = false
            ) { viewModel.onIdBackCaptured(it) }
            else -> CaptureSpec(
                "Selfie",
                "Center your face within the frame.",
                SELFIE_ASPECT,
                useFrontCamera = true
            ) { viewModel.onSelfieCaptured(it) }
        }
        key(state.step) {
            IdCaptureScreen(
                stepLabel = "Step ${state.step} of ${state.totalSteps}",
                title = spec.title,
                instruction = spec.instruction,
                aspectRatio = spec.aspectRatio,
                useFrontCamera = spec.useFrontCamera,
                onCaptured = spec.onCaptured,
                onBack = viewModel::goBack
            )
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Customer") },
                navigationIcon = {
                    IconButton(onClick = { if (state.step == 1) onCancel() else viewModel.goBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { Text("Step ${state.step} of ${state.totalSteps}", modifier = Modifier.padding(end = 16.dp)) }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LinearProgressIndicator(
                progress = state.step / state.totalSteps.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Column(Modifier.weight(1f).padding(16.dp)) {
                when (state.step) {
                    1 -> PersonalInfoStep(state, viewModel)
                    2 -> BusinessInfoStep(state, viewModel)
                    5 -> ReviewSubmitStep(state)
                }

                state.error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            when (state.step) {
                1, 2 -> Button(
                    onClick = viewModel::nextStep,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) { Text("Continue") }
                5 -> Button(
                    onClick = { viewModel.submit(onDone) },
                    enabled = !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Submit Customer")
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoStep(state: AddCustomerUiState, viewModel: AddCustomerViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Personal Information", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(state.firstName, viewModel::onFirstNameChange, label = { Text("First name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.middleName, viewModel::onMiddleNameChange, label = { Text("Middle name (optional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.surname, viewModel::onSurnameChange, label = { Text("Surname") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.mobile, viewModel::onMobileChange, label = { Text("Mobile number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.idNumber, viewModel::onIdNumberChange, label = { Text("National ID number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.gender, viewModel::onGenderChange, label = { Text("Gender") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.maritalStatus, viewModel::onMaritalStatusChange, label = { Text("Marital status") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.town, viewModel::onTownChange, label = { Text("Town") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.county, viewModel::onCountyChange, label = { Text("County") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun BusinessInfoStep(state: AddCustomerUiState, viewModel: AddCustomerViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Business Information", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(state.businessName, viewModel::onBusinessNameChange, label = { Text("Business name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.businessType, viewModel::onBusinessTypeChange, label = { Text("Business type") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.industry, viewModel::onIndustryChange, label = { Text("Industry") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.businessLocation, viewModel::onBusinessLocationChange, label = { Text("Business location") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.dailySales, viewModel::onDailySalesChange, label = { Text("Average daily sales (KES)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ReviewSubmitStep(state: AddCustomerUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Review & Submit", style = MaterialTheme.typography.titleMedium)
        Text("${state.firstName} ${state.surname} — ${state.mobile}")
        Text("ID: ${state.idNumber}")
        Text("Business: ${state.businessName}")
        Text("ID front, ID back and selfie captured ✓", color = MaterialTheme.colorScheme.primary)
    }
}
