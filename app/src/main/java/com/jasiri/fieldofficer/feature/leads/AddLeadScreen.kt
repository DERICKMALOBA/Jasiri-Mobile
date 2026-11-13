package com.jasiri.fieldofficer.feature.leads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jasiri.fieldofficer.core.reference.INDUSTRY_BUSINESS_TYPES
import com.jasiri.fieldofficer.core.reference.KENYA_COUNTIES
import com.jasiri.fieldofficer.core.reference.OTHER_OPTION
import com.jasiri.fieldofficer.feature.common.FormDropdownField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLeadScreen(
    viewModel: AddLeadViewModel = hiltViewModel(),
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) onDone()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Lead") },
                navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.Filled.ArrowBack, contentDescription = "Cancel") } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.mobile,
                onValueChange = viewModel::onMobileChange,
                label = { Text("Mobile Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.firstname,
                onValueChange = viewModel::onFirstnameChange,
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.surname,
                onValueChange = viewModel::onSurnameChange,
                label = { Text("Surname") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.businessName,
                onValueChange = viewModel::onBusinessNameChange,
                label = { Text("Business / Enterprise Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            FormDropdownField(
                label = "Industry Segment",
                selected = state.industry,
                options = INDUSTRY_BUSINESS_TYPES.keys.toList() + OTHER_OPTION,
                onSelected = viewModel::onIndustryChange
            )
            if (state.industry == OTHER_OPTION) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.industryOther,
                    onValueChange = viewModel::onIndustryOtherChange,
                    label = { Text("Specify Industry") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.industry.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                val businessTypeOptions = (INDUSTRY_BUSINESS_TYPES[state.industry] ?: emptyList()) + OTHER_OPTION
                FormDropdownField(
                    label = "Business Type",
                    selected = state.businessType,
                    options = businessTypeOptions,
                    onSelected = viewModel::onBusinessTypeChange
                )
                if (state.businessType == OTHER_OPTION) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.businessTypeOther,
                        onValueChange = viewModel::onBusinessTypeOtherChange,
                        label = { Text("Specify Business Type") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            FormDropdownField(
                label = "Operational Location (County)",
                selected = state.county,
                options = KENYA_COUNTIES,
                onSelected = viewModel::onCountyChange
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = "Field Agent",
                onValueChange = {},
                label = { Text("Lead Source") },
                enabled = false,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(20.dp))
            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Save Lead")
            }
        }
    }
}
