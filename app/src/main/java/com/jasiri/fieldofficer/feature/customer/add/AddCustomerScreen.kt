package com.jasiri.fieldofficer.feature.customer.add

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jasiri.fieldofficer.core.location.DeviceLocationProvider
import com.jasiri.fieldofficer.core.reference.GENDER_OPTIONS
import com.jasiri.fieldofficer.core.reference.INDUSTRY_BUSINESS_TYPES
import com.jasiri.fieldofficer.core.reference.KENYA_COUNTIES
import com.jasiri.fieldofficer.core.reference.MARITAL_STATUS_OPTIONS
import com.jasiri.fieldofficer.core.reference.OTHER_OPTION
import com.jasiri.fieldofficer.core.reference.PREFIX_OPTIONS
import com.jasiri.fieldofficer.core.reference.RESIDENCE_STATUS_OPTIONS
import com.jasiri.fieldofficer.core.reference.YES_NO_OPTIONS
import com.jasiri.fieldofficer.feature.common.FormDateField
import com.jasiri.fieldofficer.feature.common.FormDropdownField
import com.jasiri.fieldofficer.feature.customer.capture.IdCaptureScreen
import kotlinx.coroutines.launch
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

    if (state.isCapturingBusinessPhoto) {
        IdCaptureScreen(
            stepLabel = "Business Photo",
            title = "Business Photo",
            instruction = "Capture a photo of the business premises or stock.",
            aspectRatio = ID_CARD_ASPECT,
            useFrontCamera = false,
            onCaptured = { viewModel.onBusinessPhotoCaptured(it) },
            onBack = viewModel::onCancelBusinessPhotoCapture
        )
        return
    }

    if (state.isCapturingSecurityItemPhoto) {
        IdCaptureScreen(
            stepLabel = "Security Item Photo",
            title = "Security Item Photo",
            instruction = "Capture a photo of the security item.",
            aspectRatio = ID_CARD_ASPECT,
            useFrontCamera = false,
            onCaptured = { viewModel.onSecurityItemPhotoCaptured(it) },
            onBack = viewModel::onCancelCaptureSecurityItemPhoto
        )
        return
    }

    val guarantorPhotoSlot = state.capturingGuarantorPhotoSlot
    if (guarantorPhotoSlot != null) {
        val label = when (guarantorPhotoSlot) {
            "passport" -> "Guarantor Passport Photo"
            "idFront" -> "Guarantor ID Front"
            else -> "Guarantor ID Back"
        }
        IdCaptureScreen(
            stepLabel = label,
            title = label,
            instruction = "Align the photo within the frame.",
            aspectRatio = if (guarantorPhotoSlot == "passport") SELFIE_ASPECT else ID_CARD_ASPECT,
            useFrontCamera = false,
            onCaptured = { viewModel.onGuarantorPhotoCaptured(it) },
            onBack = viewModel::onCancelCaptureGuarantorPhoto
        )
        return
    }

    val editingSecurityItem = state.editingSecurityItem
    if (editingSecurityItem != null) {
        SecurityItemFormScreen(
            draft = editingSecurityItem,
            onItemChange = { viewModel.onSecurityItemFieldChange(item = it) },
            onItemOtherChange = { viewModel.onSecurityItemFieldChange(itemOther = it) },
            onDescriptionChange = { viewModel.onSecurityItemFieldChange(description = it) },
            onIdentificationChange = { viewModel.onSecurityItemFieldChange(identification = it) },
            onValueChange = { viewModel.onSecurityItemFieldChange(value = it) },
            onAddPhoto = viewModel::onStartCaptureSecurityItemPhoto,
            onRemovePhoto = viewModel::onRemoveSecurityItemPhoto,
            onSave = viewModel::onSaveSecurityItem,
            onCancel = viewModel::onCancelSecurityItemEdit
        )
        return
    }

    val editingGuarantor = state.editingGuarantor
    if (editingGuarantor != null) {
        val isFirstGuarantor = (state.editingGuarantorIndex ?: state.guarantors.size) == 0
        GuarantorFormScreen(
            draft = editingGuarantor,
            isFirstGuarantor = isFirstGuarantor,
            onFieldChange = viewModel::onGuarantorFieldChange,
            onCapturePhoto = viewModel::onStartCaptureGuarantorPhoto,
            onAddSecurityItem = { viewModel.onStartAddSecurityItem(SecurityItemContext.GUARANTOR) },
            onEditSecurityItem = { viewModel.onStartEditSecurityItem(SecurityItemContext.GUARANTOR, it) },
            onRemoveSecurityItem = { viewModel.onRemoveSecurityItem(SecurityItemContext.GUARANTOR, it) },
            onSave = viewModel::onSaveGuarantor,
            onCancel = viewModel::onCancelGuarantorEdit
        )
        return
    }

    val editingNextOfKin = state.editingNextOfKin
    if (editingNextOfKin != null) {
        NextOfKinFormScreen(
            draft = editingNextOfKin,
            onFieldChange = viewModel::onNextOfKinFieldChange,
            onSave = viewModel::onSaveNextOfKin,
            onCancel = viewModel::onCancelNextOfKinEdit
        )
        return
    }

    val onSelfieStep = state.step == 8 && state.selfieFile == null
    if (state.step in 5..7 || onSelfieStep) {
        val spec = when (state.step) {
            5 -> CaptureSpec(
                "Front of National ID",
                "Align the front of the ID within the frame.",
                ID_CARD_ASPECT,
                useFrontCamera = false
            ) { viewModel.onIdFrontCaptured(it) }
            6 -> CaptureSpec(
                "Back of National ID",
                "Align the back of the ID within the frame.",
                ID_CARD_ASPECT,
                useFrontCamera = false
            ) { viewModel.onIdBackCaptured(it) }
            7 -> CaptureSpec(
                "Residence Photo",
                "Capture a photo of the customer's residence.",
                ID_CARD_ASPECT,
                useFrontCamera = false
            ) { viewModel.onResidenceCaptured(it) }
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
                onBack = viewModel::goBack,
                // Residence Image is tenant-conditional on the web app, unlike ID/selfie — skippable here.
                onSkip = if (state.step == 7) viewModel::onSkipResidence else null
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
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (state.step) {
                    1 -> PersonalInfoStep(state, viewModel)
                    2 -> BusinessInfoStep(state, viewModel)
                    3 -> GuarantorsStep(state, viewModel)
                    4 -> NextOfKinStep(state, viewModel)
                    8 -> ReviewSubmitStep(state)
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
                1, 2, 3, 4 -> Button(
                    onClick = viewModel::nextStep,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) { Text("Continue") }
                8 -> Button(
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
        OutlinedTextField(state.idNumber, viewModel::onIdNumberChange, label = { Text("National ID Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        FormDropdownField("Prefix", state.prefix, PREFIX_OPTIONS, viewModel::onPrefixChange)
        OutlinedTextField(state.firstName, viewModel::onFirstNameChange, label = { Text("First Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.middleName, viewModel::onMiddleNameChange, label = { Text("Middle Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.surname, viewModel::onSurnameChange, label = { Text("Surname") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.mobile, viewModel::onMobileChange, label = { Text("Mobile Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.alternativeMobile, viewModel::onAlternativeMobileChange, label = { Text("Alternative Mobile") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        FormDateField("Date of Birth", state.dateOfBirth, viewModel::onDateOfBirthChange)
        FormDropdownField("Gender", state.gender, GENDER_OPTIONS, viewModel::onGenderChange)
        FormDropdownField("Marital Status", state.maritalStatus, MARITAL_STATUS_OPTIONS, viewModel::onMaritalStatusChange)

        if (state.maritalStatus == "Married") {
            Text("Spouse Information", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(state.spouseName, viewModel::onSpouseNameChange, label = { Text("Spouse Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.spouseIdNumber, viewModel::onSpouseIdNumberChange, label = { Text("Spouse ID Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.spouseMobile, viewModel::onSpouseMobileChange, label = { Text("Spouse Mobile") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.spouseEconomicActivity, viewModel::onSpouseEconomicActivityChange, label = { Text("Spouse Economic Activity") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }

        FormDropdownField("Residence Status", state.residenceStatus, RESIDENCE_STATUS_OPTIONS, viewModel::onResidenceStatusChange)
        OutlinedTextField(state.occupation, viewModel::onOccupationChange, label = { Text("Occupation") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.postalAddress, viewModel::onPostalAddressChange, label = { Text("Postal Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.postalCode, viewModel::onPostalCodeChange, label = { Text("Postal Code") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        FormDropdownField("County", state.county, KENYA_COUNTIES, viewModel::onCountyChange)
        OutlinedTextField(state.town, viewModel::onTownChange, label = { Text("Town / City") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun BusinessInfoStep(state: AddCustomerUiState, viewModel: AddCustomerViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            scope.launch {
                viewModel.onFetchingLocation(true)
                val location = DeviceLocationProvider.getCurrentLocation(context)
                viewModel.onLocationFetched(location?.latitude, location?.longitude)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Business Information", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(state.businessName, viewModel::onBusinessNameChange, label = { Text("Business Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        FormDropdownField(
            "Industry",
            state.industry,
            INDUSTRY_BUSINESS_TYPES.keys.toList() + OTHER_OPTION,
            viewModel::onIndustryChange
        )
        if (state.industry == OTHER_OPTION) {
            OutlinedTextField(state.industryOther, viewModel::onIndustryOtherChange, label = { Text("Specific Industry") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }

        if (state.industry.isNotBlank()) {
            val businessTypeOptions = (INDUSTRY_BUSINESS_TYPES[state.industry] ?: emptyList()) + OTHER_OPTION
            FormDropdownField("Business Type", state.businessType, businessTypeOptions, viewModel::onBusinessTypeChange)
            if (state.businessType == OTHER_OPTION) {
                OutlinedTextField(state.businessTypeOther, viewModel::onBusinessTypeOtherChange, label = { Text("Specific Business Type") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        }

        FormDateField("Year Established", state.yearEstablished, viewModel::onYearEstablishedChange)
        OutlinedTextField(state.dailySales, viewModel::onDailySalesChange, label = { Text("Daily Sales (KES)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.requestedAmount, viewModel::onRequestedAmountChange, label = { Text("Requested Loan Amount (KES)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        FormDropdownField("County", state.businessCounty, KENYA_COUNTIES, viewModel::onBusinessCountyChange)
        OutlinedTextField(state.businessLocation, viewModel::onBusinessLocationChange, label = { Text("Business Location") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.road, viewModel::onRoadChange, label = { Text("Road") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.landmark, viewModel::onLandmarkChange, label = { Text("Landmark") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        FormDropdownField("Local Authority License", state.hasLocalAuthorityLicense, YES_NO_OPTIONS, viewModel::onHasLocalAuthorityLicenseChange)

        Text("GPS Location", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.businessLat?.toString() ?: "",
                onValueChange = viewModel::onLatChange,
                label = { Text("Latitude") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.businessLng?.toString() ?: "",
                onValueChange = viewModel::onLngChange,
                label = { Text("Longitude") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedButton(
            onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
            enabled = !state.isFetchingLocation,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isFetchingLocation) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Filled.MyLocation, contentDescription = null)
            }
            Text(" Get Current Location")
        }

        Text("Business Images", style = MaterialTheme.typography.titleSmall)
        if (state.businessImageFiles.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.businessImageFiles, key = { it.absolutePath }) { file ->
                    Box {
                        AsyncImage(
                            model = file,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
                        )
                        IconButton(
                            onClick = { viewModel.onRemoveBusinessImage(file) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        OutlinedButton(onClick = viewModel::onStartBusinessPhotoCapture, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.AddAPhoto, contentDescription = null)
            Text(" Add Business Photo")
        }

        Text("Borrower Security", style = MaterialTheme.typography.titleSmall)
        state.securityItems.forEachIndexed { index, item ->
            SecurityItemSummaryCard(
                entry = item,
                onEdit = { viewModel.onStartEditSecurityItem(SecurityItemContext.BORROWER, index) },
                onRemove = { viewModel.onRemoveSecurityItem(SecurityItemContext.BORROWER, index) }
            )
        }
        OutlinedButton(
            onClick = { viewModel.onStartAddSecurityItem(SecurityItemContext.BORROWER) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add Security Item") }
    }
}

@Composable
private fun GuarantorsStep(state: AddCustomerUiState, viewModel: AddCustomerViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Guarantors", style = MaterialTheme.typography.titleMedium)
        Text(
            "Up to $MAX_GUARANTORS guarantors. Only the first guarantor's security items are recorded.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        state.guarantors.forEachIndexed { index, guarantor ->
            PersonSummaryCard(
                title = "${guarantor.firstname} ${guarantor.surname}".trim(),
                subtitle = guarantor.relationship,
                onEdit = { viewModel.onStartEditGuarantor(index) },
                onRemove = { viewModel.onRemoveGuarantor(index) }
            )
        }
        if (state.guarantors.size < MAX_GUARANTORS) {
            OutlinedButton(onClick = viewModel::onStartAddGuarantor, modifier = Modifier.fillMaxWidth()) {
                Text("Add Guarantor")
            }
        }
    }
}

@Composable
private fun NextOfKinStep(state: AddCustomerUiState, viewModel: AddCustomerViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Next of Kin", style = MaterialTheme.typography.titleMedium)
        Text(
            "Up to $MAX_NEXT_OF_KIN next of kin.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        state.nextOfKin.forEachIndexed { index, kin ->
            PersonSummaryCard(
                title = "${kin.firstname} ${kin.surname}".trim(),
                subtitle = kin.relationship,
                onEdit = { viewModel.onStartEditNextOfKin(index) },
                onRemove = { viewModel.onRemoveNextOfKin(index) }
            )
        }
        if (state.nextOfKin.size < MAX_NEXT_OF_KIN) {
            OutlinedButton(onClick = viewModel::onStartAddNextOfKin, modifier = Modifier.fillMaxWidth()) {
                Text("Add Next of Kin")
            }
        }
    }
}

@Composable
private fun PersonSummaryCard(title: String, subtitle: String, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit)) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title.ifBlank { "Unnamed" }, fontWeight = FontWeight.Bold)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
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
        if (state.residenceFile != null) {
            Text("Residence photo captured ✓", color = MaterialTheme.colorScheme.primary)
        }
        if (state.businessImageFiles.isNotEmpty()) {
            Text("${state.businessImageFiles.size} business photo(s) attached", color = MaterialTheme.colorScheme.primary)
        }
        if (state.securityItems.isNotEmpty()) {
            Text("${state.securityItems.size} security item(s)", color = MaterialTheme.colorScheme.primary)
        }
        if (state.guarantors.isNotEmpty()) {
            Text("${state.guarantors.size} guarantor(s)", color = MaterialTheme.colorScheme.primary)
        }
        if (state.nextOfKin.isNotEmpty()) {
            Text("${state.nextOfKin.size} next of kin", color = MaterialTheme.colorScheme.primary)
        }
    }
}
