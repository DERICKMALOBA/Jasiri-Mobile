package com.jasiri.fieldofficer.feature.customer.add

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jasiri.fieldofficer.core.reference.EMPLOYMENT_STATUS_OPTIONS
import com.jasiri.fieldofficer.core.reference.GENDER_OPTIONS
import com.jasiri.fieldofficer.core.reference.GUARANTOR_RELATIONSHIP_OPTIONS
import com.jasiri.fieldofficer.core.reference.KENYA_COUNTIES
import com.jasiri.fieldofficer.core.reference.MARITAL_STATUS_OPTIONS
import com.jasiri.fieldofficer.core.reference.NEXT_OF_KIN_RELATIONSHIP_OPTIONS
import com.jasiri.fieldofficer.core.reference.OTHER_OPTION
import com.jasiri.fieldofficer.core.reference.PREFIX_OPTIONS
import com.jasiri.fieldofficer.core.reference.RESIDENCE_STATUS_OPTIONS
import com.jasiri.fieldofficer.core.reference.SECURITY_ITEM_CATEGORIES
import com.jasiri.fieldofficer.feature.common.FormDateField
import com.jasiri.fieldofficer.feature.common.FormDropdownField
import java.io.File

@Composable
fun PhotoThumbnail(file: File, onRemove: () -> Unit) {
    Box {
        AsyncImage(
            model = file,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityItemFormScreen(
    draft: SecurityItemEntry,
    onItemChange: (String) -> Unit,
    onItemOtherChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onIdentificationChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (File) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Item") },
                navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.Filled.ArrowBack, contentDescription = "Cancel") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(16.dp)) {
            FormDropdownField("Item Type", draft.item, SECURITY_ITEM_CATEGORIES + OTHER_OPTION, onItemChange)
            if (draft.item == OTHER_OPTION) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(draft.itemOther, onItemOtherChange, label = { Text("Specify Item") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(draft.description, onDescriptionChange, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(draft.identification, onIdentificationChange, label = { Text("Identification (e.g. serial/plate number)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(draft.value, onValueChange, label = { Text("Estimated Value (KES)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
            Text("Photos", style = MaterialTheme.typography.titleSmall)
            if (draft.photos.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(draft.photos, key = { it.absolutePath }) { file -> PhotoThumbnail(file) { onRemovePhoto(file) } }
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onAddPhoto, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.AddAPhoto, contentDescription = null)
                Text(" Add Photo")
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onSave,
                enabled = draft.item.isNotBlank() && (draft.item != OTHER_OPTION || draft.itemOther.isNotBlank()),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Save Item") }
        }
    }
}

@Composable
fun SecurityItemSummaryCard(entry: SecurityItemEntry, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onEdit)) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(entry.item.let { if (it == OTHER_OPTION) entry.itemOther else it }, fontWeight = FontWeight.Bold)
                if (entry.value.isNotBlank()) {
                    Text("KES ${entry.value}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuarantorFormScreen(
    draft: GuarantorEntry,
    isFirstGuarantor: Boolean,
    onFieldChange: ((GuarantorEntry) -> GuarantorEntry) -> Unit,
    onCapturePhoto: (String) -> Unit,
    onAddSecurityItem: () -> Unit,
    onEditSecurityItem: (Int) -> Unit,
    onRemoveSecurityItem: (Int) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guarantor") },
                navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.Filled.ArrowBack, contentDescription = "Cancel") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormDropdownField("Prefix", draft.prefix, PREFIX_OPTIONS) { v -> onFieldChange { it.copy(prefix = v) } }
            OutlinedTextField(draft.firstname, { v -> onFieldChange { it.copy(firstname = v) } }, label = { Text("First Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.middlename, { v -> onFieldChange { it.copy(middlename = v) } }, label = { Text("Middle Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.surname, { v -> onFieldChange { it.copy(surname = v) } }, label = { Text("Surname") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.idNumber, { v -> onFieldChange { it.copy(idNumber = v) } }, label = { Text("ID Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.mobile, { v -> onFieldChange { it.copy(mobile = v) } }, label = { Text("Mobile Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.alternativeNumber, { v -> onFieldChange { it.copy(alternativeNumber = v) } }, label = { Text("Alternative Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            FormDateField("Date of Birth", draft.dateOfBirth) { v -> onFieldChange { it.copy(dateOfBirth = v) } }
            FormDropdownField("Gender", draft.gender, GENDER_OPTIONS) { v -> onFieldChange { it.copy(gender = v) } }
            FormDropdownField("Marital Status", draft.maritalStatus, MARITAL_STATUS_OPTIONS) { v -> onFieldChange { it.copy(maritalStatus = v) } }
            FormDropdownField("Residence Status", draft.residenceStatus, RESIDENCE_STATUS_OPTIONS) { v -> onFieldChange { it.copy(residenceStatus = v) } }
            OutlinedTextField(draft.occupation, { v -> onFieldChange { it.copy(occupation = v) } }, label = { Text("Occupation") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            FormDropdownField(
                "Relationship to Customer",
                draft.relationship,
                GUARANTOR_RELATIONSHIP_OPTIONS,
                { v -> onFieldChange { it.copy(relationship = v) } }
            )
            if (draft.relationship == "Other") {
                OutlinedTextField(draft.relationshipOther, { v -> onFieldChange { it.copy(relationshipOther = v) } }, label = { Text("Specify Relationship") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
            OutlinedTextField(draft.postalAddress, { v -> onFieldChange { it.copy(postalAddress = v) } }, label = { Text("Postal Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.postalCode, { v -> onFieldChange { it.copy(postalCode = v) } }, label = { Text("Postal Code") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            FormDropdownField("County", draft.county, KENYA_COUNTIES) { v -> onFieldChange { it.copy(county = v) } }
            OutlinedTextField(draft.cityTown, { v -> onFieldChange { it.copy(cityTown = v) } }, label = { Text("City / Town") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            Text("Photos", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PhotoSlotButton("Passport", draft.passportFile) { onCapturePhoto("passport") }
                PhotoSlotButton("ID Front", draft.idFrontFile) { onCapturePhoto("idFront") }
                PhotoSlotButton("ID Back", draft.idBackFile) { onCapturePhoto("idBack") }
            }

            if (isFirstGuarantor) {
                Text("Guarantor Security", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Only the first guarantor's security items are recorded by the backend.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                draft.securityItems.forEachIndexed { index, item ->
                    SecurityItemSummaryCard(item, onEdit = { onEditSecurityItem(index) }, onRemove = { onRemoveSecurityItem(index) })
                }
                OutlinedButton(onClick = onAddSecurityItem, modifier = Modifier.fillMaxWidth()) {
                    Text("Add Security Item")
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onSave,
                enabled = draft.firstname.isNotBlank() && draft.surname.isNotBlank() && draft.mobile.isNotBlank() &&
                    draft.idNumber.isNotBlank() && draft.relationship.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Save Guarantor") }
        }
    }
}

@Composable
private fun PhotoSlotButton(label: String, file: File?, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (file != null) {
            AsyncImage(
                model = file,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AddAPhoto, contentDescription = "Capture $label")
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextOfKinFormScreen(
    draft: NextOfKinEntry,
    onFieldChange: ((NextOfKinEntry) -> NextOfKinEntry) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Next of Kin") },
                navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.Filled.ArrowBack, contentDescription = "Cancel") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(draft.firstname, { v -> onFieldChange { it.copy(firstname = v) } }, label = { Text("First Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.middlename, { v -> onFieldChange { it.copy(middlename = v) } }, label = { Text("Middle Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.surname, { v -> onFieldChange { it.copy(surname = v) } }, label = { Text("Surname") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.idNumber, { v -> onFieldChange { it.copy(idNumber = v) } }, label = { Text("ID Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            FormDropdownField("Relationship", draft.relationship, NEXT_OF_KIN_RELATIONSHIP_OPTIONS) { v -> onFieldChange { it.copy(relationship = v) } }
            if (draft.relationship == "Other") {
                OutlinedTextField(draft.relationshipOther, { v -> onFieldChange { it.copy(relationshipOther = v) } }, label = { Text("Specify Relationship") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
            OutlinedTextField(draft.mobile, { v -> onFieldChange { it.copy(mobile = v) } }, label = { Text("Mobile Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(draft.alternativeNumber, { v -> onFieldChange { it.copy(alternativeNumber = v) } }, label = { Text("Alternative Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            FormDropdownField("Employment Status", draft.employmentStatus, EMPLOYMENT_STATUS_OPTIONS) { v -> onFieldChange { it.copy(employmentStatus = v) } }

            if (draft.employmentStatus == "Employed") {
                OutlinedTextField(draft.companyName, { v -> onFieldChange { it.copy(companyName = v) } }, label = { Text("Company Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(draft.salary, { v -> onFieldChange { it.copy(salary = v) } }, label = { Text("Salary (KES)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            } else if (draft.employmentStatus == "Self Employed") {
                OutlinedTextField(draft.businessName, { v -> onFieldChange { it.copy(businessName = v) } }, label = { Text("Business Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(draft.businessIncome, { v -> onFieldChange { it.copy(businessIncome = v) } }, label = { Text("Business Income (KES)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }

            FormDropdownField("County", draft.county, KENYA_COUNTIES) { v -> onFieldChange { it.copy(county = v) } }
            OutlinedTextField(draft.cityTown, { v -> onFieldChange { it.copy(cityTown = v) } }, label = { Text("City / Town") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onSave,
                enabled = draft.firstname.isNotBlank() && draft.surname.isNotBlank() && draft.relationship.isNotBlank() &&
                    draft.mobile.isNotBlank() && draft.employmentStatus.isNotBlank() && draft.county.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Save Next of Kin") }
        }
    }
}
