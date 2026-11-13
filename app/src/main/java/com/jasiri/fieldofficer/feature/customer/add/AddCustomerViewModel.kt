package com.jasiri.fieldofficer.feature.customer.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.customer.CustomerRepository
import com.jasiri.fieldofficer.core.network.dto.CreateCustomerFields
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class AddCustomerUiState(
    val step: Int = 1,
    val firstName: String = "",
    val surname: String = "",
    val middleName: String = "",
    val mobile: String = "",
    val idNumber: String = "",
    val gender: String = "",
    val maritalStatus: String = "",
    val town: String = "",
    val county: String = "",
    val businessName: String = "",
    val businessType: String = "",
    val industry: String = "",
    val businessLocation: String = "",
    val dailySales: String = "",
    val idFrontFile: File? = null,
    val idBackFile: File? = null,
    val selfieFile: File? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false
) {
    val totalSteps = 5
}

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCustomerUiState())
    val uiState = _uiState.asStateFlow()

    fun onFirstNameChange(v: String) { _uiState.value = _uiState.value.copy(firstName = v, error = null) }
    fun onSurnameChange(v: String) { _uiState.value = _uiState.value.copy(surname = v, error = null) }
    fun onMiddleNameChange(v: String) { _uiState.value = _uiState.value.copy(middleName = v) }
    fun onMobileChange(v: String) { _uiState.value = _uiState.value.copy(mobile = v, error = null) }
    fun onIdNumberChange(v: String) { _uiState.value = _uiState.value.copy(idNumber = v, error = null) }
    fun onGenderChange(v: String) { _uiState.value = _uiState.value.copy(gender = v) }
    fun onMaritalStatusChange(v: String) { _uiState.value = _uiState.value.copy(maritalStatus = v) }
    fun onTownChange(v: String) { _uiState.value = _uiState.value.copy(town = v) }
    fun onCountyChange(v: String) { _uiState.value = _uiState.value.copy(county = v) }
    fun onBusinessNameChange(v: String) { _uiState.value = _uiState.value.copy(businessName = v, error = null) }
    fun onBusinessTypeChange(v: String) { _uiState.value = _uiState.value.copy(businessType = v) }
    fun onIndustryChange(v: String) { _uiState.value = _uiState.value.copy(industry = v) }
    fun onBusinessLocationChange(v: String) { _uiState.value = _uiState.value.copy(businessLocation = v, error = null) }
    fun onDailySalesChange(v: String) { _uiState.value = _uiState.value.copy(dailySales = v) }

    fun onIdFrontCaptured(file: File) { _uiState.value = _uiState.value.copy(idFrontFile = file, step = 4) }
    fun onIdBackCaptured(file: File) { _uiState.value = _uiState.value.copy(idBackFile = file, step = 5) }
    fun onSelfieCaptured(file: File) { _uiState.value = _uiState.value.copy(selfieFile = file) }

    fun goBack() {
        _uiState.value = _uiState.value.copy(step = (_uiState.value.step - 1).coerceAtLeast(1), error = null)
    }

    fun nextStep() {
        val s = _uiState.value
        val error = when (s.step) {
            1 -> validateStep1(s)
            2 -> validateStep2(s)
            else -> null
        }
        if (error != null) {
            _uiState.value = s.copy(error = error)
            return
        }
        _uiState.value = s.copy(step = s.step + 1, error = null)
    }

    private fun validateStep1(s: AddCustomerUiState): String? {
        if (s.firstName.isBlank() || s.surname.isBlank()) return "Enter the customer's first and last name"
        if (!s.mobile.matches(Regex("^[0-9]{10,15}$"))) return "Enter a valid phone number (digits only)"
        if (!s.idNumber.matches(Regex("^[0-9]{6,12}$"))) return "Enter a valid national ID number"
        return null
    }

    private fun validateStep2(s: AddCustomerUiState): String? {
        if (s.businessName.isBlank()) return "Enter the business name"
        if (s.businessLocation.isBlank()) return "Enter the business location"
        return null
    }

    fun submit(onDone: () -> Unit) {
        val s = _uiState.value
        val idFront = s.idFrontFile
        val idBack = s.idBackFile
        val selfie = s.selfieFile
        if (idFront == null || idBack == null || selfie == null) {
            _uiState.value = s.copy(error = "Capture the ID front, ID back and selfie before submitting")
            return
        }
        _uiState.value = s.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            val result = runCatching {
                val idFrontUrl = repository.uploadPhoto(idFront, "documents").getOrThrow()
                val idBackUrl = repository.uploadPhoto(idBack, "documents").getOrThrow()
                val selfieUrl = repository.uploadPhoto(selfie, "customers").getOrThrow()
                repository.createCustomer(
                    CreateCustomerFields(
                        firstname = s.firstName,
                        surname = s.surname,
                        middlename = s.middleName.ifBlank { null },
                        mobile = s.mobile,
                        idNumber = s.idNumber,
                        gender = s.gender.ifBlank { null },
                        maritalStatus = s.maritalStatus.ifBlank { null },
                        town = s.town.ifBlank { null },
                        county = s.county.ifBlank { null },
                        businessName = s.businessName,
                        businessType = s.businessType.ifBlank { null },
                        industry = s.industry.ifBlank { null },
                        businessLocation = s.businessLocation,
                        dailySales = s.dailySales.toDoubleOrNull(),
                        idFrontUrl = idFrontUrl,
                        idBackUrl = idBackUrl,
                        passportUrl = selfieUrl
                    )
                ).getOrThrow()
            }
            result
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, isComplete = true)
                    onDone()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = e.message ?: "Couldn't save this customer — check your connection and try again"
                    )
                }
        }
    }
}
