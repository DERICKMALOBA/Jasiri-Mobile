package com.jasiri.fieldofficer.feature.customer.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.customer.CustomerRepository
import com.jasiri.fieldofficer.core.network.dto.CreateCustomerFields
import com.jasiri.fieldofficer.core.network.dto.SpouseFields
import com.jasiri.fieldofficer.core.reference.OTHER_OPTION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class AddCustomerUiState(
    val step: Int = 1,
    val prefix: String = "",
    val firstName: String = "",
    val surname: String = "",
    val middleName: String = "",
    val mobile: String = "",
    val alternativeMobile: String = "",
    val idNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val maritalStatus: String = "",
    val residenceStatus: String = "",
    val occupation: String = "",
    val postalAddress: String = "",
    val postalCode: String = "",
    val town: String = "",
    val county: String = "",
    val spouseName: String = "",
    val spouseIdNumber: String = "",
    val spouseMobile: String = "",
    val spouseEconomicActivity: String = "",
    val businessName: String = "",
    val businessType: String = "",
    val businessTypeOther: String = "",
    val industry: String = "",
    val industryOther: String = "",
    val businessCounty: String = "",
    val businessLocation: String = "",
    val road: String = "",
    val landmark: String = "",
    val hasLocalAuthorityLicense: String = "",
    val businessLat: Double? = null,
    val businessLng: Double? = null,
    val isFetchingLocation: Boolean = false,
    val yearEstablished: String = "",
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

    fun onPrefixChange(v: String) { _uiState.value = _uiState.value.copy(prefix = v) }
    fun onFirstNameChange(v: String) { _uiState.value = _uiState.value.copy(firstName = v, error = null) }
    fun onSurnameChange(v: String) { _uiState.value = _uiState.value.copy(surname = v, error = null) }
    fun onMiddleNameChange(v: String) { _uiState.value = _uiState.value.copy(middleName = v) }
    fun onMobileChange(v: String) { _uiState.value = _uiState.value.copy(mobile = v, error = null) }
    fun onAlternativeMobileChange(v: String) { _uiState.value = _uiState.value.copy(alternativeMobile = v) }
    fun onIdNumberChange(v: String) { _uiState.value = _uiState.value.copy(idNumber = v, error = null) }
    fun onDateOfBirthChange(v: String) { _uiState.value = _uiState.value.copy(dateOfBirth = v, error = null) }
    fun onGenderChange(v: String) { _uiState.value = _uiState.value.copy(gender = v) }
    fun onMaritalStatusChange(v: String) { _uiState.value = _uiState.value.copy(maritalStatus = v) }
    fun onResidenceStatusChange(v: String) { _uiState.value = _uiState.value.copy(residenceStatus = v) }
    fun onOccupationChange(v: String) { _uiState.value = _uiState.value.copy(occupation = v) }
    fun onPostalAddressChange(v: String) { _uiState.value = _uiState.value.copy(postalAddress = v) }
    fun onPostalCodeChange(v: String) { _uiState.value = _uiState.value.copy(postalCode = v) }
    fun onTownChange(v: String) { _uiState.value = _uiState.value.copy(town = v, error = null) }
    fun onCountyChange(v: String) { _uiState.value = _uiState.value.copy(county = v, error = null) }
    fun onSpouseNameChange(v: String) { _uiState.value = _uiState.value.copy(spouseName = v) }
    fun onSpouseIdNumberChange(v: String) { _uiState.value = _uiState.value.copy(spouseIdNumber = v) }
    fun onSpouseMobileChange(v: String) { _uiState.value = _uiState.value.copy(spouseMobile = v) }
    fun onSpouseEconomicActivityChange(v: String) { _uiState.value = _uiState.value.copy(spouseEconomicActivity = v) }
    fun onBusinessNameChange(v: String) { _uiState.value = _uiState.value.copy(businessName = v, error = null) }
    fun onBusinessTypeChange(v: String) { _uiState.value = _uiState.value.copy(businessType = v) }
    fun onBusinessTypeOtherChange(v: String) { _uiState.value = _uiState.value.copy(businessTypeOther = v) }
    fun onIndustryChange(v: String) {
        _uiState.value = _uiState.value.copy(industry = v, businessType = "", businessTypeOther = "")
    }
    fun onIndustryOtherChange(v: String) { _uiState.value = _uiState.value.copy(industryOther = v) }
    fun onBusinessCountyChange(v: String) { _uiState.value = _uiState.value.copy(businessCounty = v, error = null) }
    fun onBusinessLocationChange(v: String) { _uiState.value = _uiState.value.copy(businessLocation = v, error = null) }
    fun onRoadChange(v: String) { _uiState.value = _uiState.value.copy(road = v, error = null) }
    fun onLandmarkChange(v: String) { _uiState.value = _uiState.value.copy(landmark = v, error = null) }
    fun onHasLocalAuthorityLicenseChange(v: String) { _uiState.value = _uiState.value.copy(hasLocalAuthorityLicense = v) }
    fun onYearEstablishedChange(v: String) { _uiState.value = _uiState.value.copy(yearEstablished = v, error = null) }
    fun onDailySalesChange(v: String) { _uiState.value = _uiState.value.copy(dailySales = v, error = null) }

    fun onFetchingLocation(fetching: Boolean) { _uiState.value = _uiState.value.copy(isFetchingLocation = fetching) }
    fun onLocationFetched(lat: Double?, lng: Double?) {
        _uiState.value = _uiState.value.copy(businessLat = lat, businessLng = lng, isFetchingLocation = false)
    }
    fun onLatChange(v: String) { _uiState.value = _uiState.value.copy(businessLat = v.toDoubleOrNull()) }
    fun onLngChange(v: String) { _uiState.value = _uiState.value.copy(businessLng = v.toDoubleOrNull()) }

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
        val dob = parseDateOrNull(s.dateOfBirth) ?: return "Enter the customer's date of birth (YYYY-MM-DD)"
        if (dob.isAfter(LocalDate.now().minusYears(18))) return "Customer must be at least 18 years old"
        if (s.county.isBlank()) return "Select a county"
        if (s.town.isBlank()) return "Enter the town/city"
        if (s.maritalStatus == "Married") {
            if (s.spouseName.isBlank() || s.spouseIdNumber.isBlank() || s.spouseMobile.isBlank()) {
                return "Enter the spouse's name, ID number and mobile number"
            }
        }
        return null
    }

    private fun validateStep2(s: AddCustomerUiState): String? {
        if (s.businessName.isBlank()) return "Enter the business name"
        if (s.industry.isBlank()) return "Select an industry"
        if (s.industry == OTHER_OPTION && s.industryOther.isBlank()) return "Specify the industry"
        if (s.businessType.isBlank()) return "Select a business type"
        if (s.businessType == OTHER_OPTION && s.businessTypeOther.isBlank()) return "Specify the business type"
        val established = parseDateOrNull(s.yearEstablished) ?: return "Enter the business's year established (YYYY-MM-DD)"
        if (established.isAfter(LocalDate.now().minusMonths(6))) return "Business must be at least 6 months old"
        if (s.dailySales.toDoubleOrNull() == null) return "Enter the average daily sales"
        if (s.businessCounty.isBlank()) return "Select a business county"
        if (s.businessLocation.isBlank()) return "Enter the business location"
        if (s.road.isBlank()) return "Enter the road"
        if (s.landmark.isBlank()) return "Enter a nearby landmark"
        return null
    }

    private fun resolveOrOther(selected: String, otherValue: String): String? =
        (if (selected == OTHER_OPTION) otherValue else selected).ifBlank { null }

    private fun parseDateOrNull(value: String): LocalDate? =
        try {
            LocalDate.parse(value)
        } catch (e: DateTimeParseException) {
            null
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
                val spouse = if (s.maritalStatus == "Married") {
                    SpouseFields(
                        name = s.spouseName,
                        idNumber = s.spouseIdNumber,
                        mobile = s.spouseMobile,
                        economicActivity = s.spouseEconomicActivity.ifBlank { null }
                    )
                } else {
                    null
                }
                repository.createCustomer(
                    CreateCustomerFields(
                        prefix = s.prefix.ifBlank { null },
                        firstname = s.firstName,
                        surname = s.surname,
                        middlename = s.middleName.ifBlank { null },
                        mobile = s.mobile,
                        alternativeMobile = s.alternativeMobile.ifBlank { null },
                        idNumber = s.idNumber,
                        dateOfBirth = s.dateOfBirth.ifBlank { null },
                        gender = s.gender.ifBlank { null },
                        maritalStatus = s.maritalStatus.ifBlank { null },
                        residenceStatus = s.residenceStatus.ifBlank { null },
                        occupation = s.occupation.ifBlank { null },
                        postalAddress = s.postalAddress.ifBlank { null },
                        code = s.postalCode.toIntOrNull(),
                        town = s.town.ifBlank { null },
                        county = s.county.ifBlank { null },
                        businessName = s.businessName,
                        businessType = resolveOrOther(s.businessType, s.businessTypeOther),
                        industry = resolveOrOther(s.industry, s.industryOther),
                        businessCounty = s.businessCounty.ifBlank { null },
                        businessLocation = s.businessLocation,
                        road = s.road.ifBlank { null },
                        landmark = s.landmark.ifBlank { null },
                        hasLocalAuthorityLicense = when (s.hasLocalAuthorityLicense) {
                            "Yes" -> true
                            "No" -> false
                            else -> null
                        },
                        businessLat = s.businessLat,
                        businessLng = s.businessLng,
                        dailySales = s.dailySales.toDoubleOrNull(),
                        yearEstablished = s.yearEstablished.ifBlank { null },
                        idFrontUrl = idFrontUrl,
                        idBackUrl = idBackUrl,
                        passportUrl = selfieUrl
                    ),
                    spouse = spouse
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
