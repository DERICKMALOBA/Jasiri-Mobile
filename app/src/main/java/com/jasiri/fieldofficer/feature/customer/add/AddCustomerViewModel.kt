package com.jasiri.fieldofficer.feature.customer.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.customer.CustomerRepository
import com.jasiri.fieldofficer.core.network.dto.CreateCustomerFields
import com.jasiri.fieldofficer.core.network.dto.GuarantorFields
import com.jasiri.fieldofficer.core.network.dto.GuarantorSecurityItemFields
import com.jasiri.fieldofficer.core.network.dto.NextOfKinFields
import com.jasiri.fieldofficer.core.network.dto.SecurityItemFields
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

enum class SecurityItemContext { BORROWER, GUARANTOR }

const val MAX_GUARANTORS = 3
const val MAX_NEXT_OF_KIN = 3

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
    val requestedAmount: String = "",
    val securityItems: List<SecurityItemEntry> = emptyList(),
    val guarantors: List<GuarantorEntry> = emptyList(),
    val nextOfKin: List<NextOfKinEntry> = emptyList(),
    val idFrontFile: File? = null,
    val idBackFile: File? = null,
    val residenceFile: File? = null,
    val businessImageFiles: List<File> = emptyList(),
    val isCapturingBusinessPhoto: Boolean = false,
    val selfieFile: File? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false,
    // Borrower/Guarantor Security item editor overlay.
    val editingSecurityItem: SecurityItemEntry? = null,
    val editingSecurityItemIndex: Int? = null,
    val securityItemContext: SecurityItemContext? = null,
    val isCapturingSecurityItemPhoto: Boolean = false,
    // Guarantor editor overlay.
    val editingGuarantor: GuarantorEntry? = null,
    val editingGuarantorIndex: Int? = null,
    val capturingGuarantorPhotoSlot: String? = null,
    // Next of Kin editor overlay.
    val editingNextOfKin: NextOfKinEntry? = null,
    val editingNextOfKinIndex: Int? = null
) {
    val totalSteps = 8
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
    fun onRequestedAmountChange(v: String) { _uiState.value = _uiState.value.copy(requestedAmount = v) }

    fun onFetchingLocation(fetching: Boolean) { _uiState.value = _uiState.value.copy(isFetchingLocation = fetching) }
    fun onLocationFetched(lat: Double?, lng: Double?) {
        _uiState.value = _uiState.value.copy(businessLat = lat, businessLng = lng, isFetchingLocation = false)
    }
    fun onLatChange(v: String) { _uiState.value = _uiState.value.copy(businessLat = v.toDoubleOrNull()) }
    fun onLngChange(v: String) { _uiState.value = _uiState.value.copy(businessLng = v.toDoubleOrNull()) }

    fun onIdFrontCaptured(file: File) { _uiState.value = _uiState.value.copy(idFrontFile = file, step = 6) }
    fun onIdBackCaptured(file: File) { _uiState.value = _uiState.value.copy(idBackFile = file, step = 7) }
    fun onResidenceCaptured(file: File) { _uiState.value = _uiState.value.copy(residenceFile = file, step = 8) }
    fun onSkipResidence() { _uiState.value = _uiState.value.copy(step = 8) }
    fun onSelfieCaptured(file: File) { _uiState.value = _uiState.value.copy(selfieFile = file) }

    fun onStartBusinessPhotoCapture() { _uiState.value = _uiState.value.copy(isCapturingBusinessPhoto = true) }
    fun onCancelBusinessPhotoCapture() { _uiState.value = _uiState.value.copy(isCapturingBusinessPhoto = false) }
    fun onBusinessPhotoCaptured(file: File) {
        _uiState.value = _uiState.value.copy(
            businessImageFiles = _uiState.value.businessImageFiles + file,
            isCapturingBusinessPhoto = false
        )
    }
    fun onRemoveBusinessImage(file: File) {
        _uiState.value = _uiState.value.copy(businessImageFiles = _uiState.value.businessImageFiles - file)
    }

    // --- Borrower / Guarantor Security items ---

    fun onStartAddSecurityItem(context: SecurityItemContext) {
        _uiState.value = _uiState.value.copy(
            editingSecurityItem = SecurityItemEntry(),
            editingSecurityItemIndex = null,
            securityItemContext = context
        )
    }

    fun onStartEditSecurityItem(context: SecurityItemContext, index: Int) {
        val s = _uiState.value
        val list = if (context == SecurityItemContext.BORROWER) s.securityItems else s.editingGuarantor?.securityItems.orEmpty()
        _uiState.value = s.copy(
            editingSecurityItem = list.getOrNull(index),
            editingSecurityItemIndex = index,
            securityItemContext = context
        )
    }

    fun onCancelSecurityItemEdit() {
        _uiState.value = _uiState.value.copy(editingSecurityItem = null, editingSecurityItemIndex = null, securityItemContext = null)
    }

    fun onSecurityItemFieldChange(
        item: String? = null,
        itemOther: String? = null,
        description: String? = null,
        identification: String? = null,
        value: String? = null
    ) {
        val draft = _uiState.value.editingSecurityItem ?: return
        _uiState.value = _uiState.value.copy(
            editingSecurityItem = draft.copy(
                item = item ?: draft.item,
                itemOther = itemOther ?: draft.itemOther,
                description = description ?: draft.description,
                identification = identification ?: draft.identification,
                value = value ?: draft.value
            )
        )
    }

    fun onStartCaptureSecurityItemPhoto() { _uiState.value = _uiState.value.copy(isCapturingSecurityItemPhoto = true) }
    fun onCancelCaptureSecurityItemPhoto() { _uiState.value = _uiState.value.copy(isCapturingSecurityItemPhoto = false) }
    fun onSecurityItemPhotoCaptured(file: File) {
        val draft = _uiState.value.editingSecurityItem ?: return
        _uiState.value = _uiState.value.copy(
            editingSecurityItem = draft.copy(photos = draft.photos + file),
            isCapturingSecurityItemPhoto = false
        )
    }
    fun onRemoveSecurityItemPhoto(file: File) {
        val draft = _uiState.value.editingSecurityItem ?: return
        _uiState.value = _uiState.value.copy(editingSecurityItem = draft.copy(photos = draft.photos - file))
    }

    fun onSaveSecurityItem() {
        val s = _uiState.value
        val draft = s.editingSecurityItem ?: return
        if (draft.item.isBlank() || (draft.item == OTHER_OPTION && draft.itemOther.isBlank())) return
        if (s.securityItemContext == SecurityItemContext.BORROWER) {
            val updated = upsert(s.securityItems, s.editingSecurityItemIndex, draft)
            _uiState.value = s.copy(
                securityItems = updated,
                editingSecurityItem = null, editingSecurityItemIndex = null, securityItemContext = null
            )
        } else {
            val guarantor = s.editingGuarantor ?: return
            val updated = upsert(guarantor.securityItems, s.editingSecurityItemIndex, draft)
            _uiState.value = s.copy(
                editingGuarantor = guarantor.copy(securityItems = updated),
                editingSecurityItem = null, editingSecurityItemIndex = null, securityItemContext = null
            )
        }
    }

    fun onRemoveSecurityItem(context: SecurityItemContext, index: Int) {
        val s = _uiState.value
        if (context == SecurityItemContext.BORROWER) {
            _uiState.value = s.copy(securityItems = s.securityItems.toMutableList().apply { removeAt(index) })
        } else {
            val guarantor = s.editingGuarantor ?: return
            _uiState.value = s.copy(
                editingGuarantor = guarantor.copy(
                    securityItems = guarantor.securityItems.toMutableList().apply { removeAt(index) }
                )
            )
        }
    }

    // --- Guarantors ---

    fun onStartAddGuarantor() {
        if (_uiState.value.guarantors.size >= MAX_GUARANTORS) return
        _uiState.value = _uiState.value.copy(editingGuarantor = GuarantorEntry(), editingGuarantorIndex = null)
    }

    fun onStartEditGuarantor(index: Int) {
        val s = _uiState.value
        _uiState.value = s.copy(editingGuarantor = s.guarantors.getOrNull(index), editingGuarantorIndex = index)
    }

    fun onCancelGuarantorEdit() {
        _uiState.value = _uiState.value.copy(editingGuarantor = null, editingGuarantorIndex = null)
    }

    fun onGuarantorFieldChange(update: (GuarantorEntry) -> GuarantorEntry) {
        val draft = _uiState.value.editingGuarantor ?: return
        _uiState.value = _uiState.value.copy(editingGuarantor = update(draft))
    }

    fun onStartCaptureGuarantorPhoto(slot: String) { _uiState.value = _uiState.value.copy(capturingGuarantorPhotoSlot = slot) }
    fun onCancelCaptureGuarantorPhoto() { _uiState.value = _uiState.value.copy(capturingGuarantorPhotoSlot = null) }
    fun onGuarantorPhotoCaptured(file: File) {
        val s = _uiState.value
        val draft = s.editingGuarantor ?: return
        val updated = when (s.capturingGuarantorPhotoSlot) {
            "passport" -> draft.copy(passportFile = file)
            "idFront" -> draft.copy(idFrontFile = file)
            "idBack" -> draft.copy(idBackFile = file)
            else -> draft
        }
        _uiState.value = s.copy(editingGuarantor = updated, capturingGuarantorPhotoSlot = null)
    }

    fun onSaveGuarantor() {
        val s = _uiState.value
        val draft = s.editingGuarantor ?: return
        if (draft.firstname.isBlank() || draft.surname.isBlank()) {
            return
        }
        val updated = upsert(s.guarantors, s.editingGuarantorIndex, draft)
        _uiState.value = s.copy(guarantors = updated, editingGuarantor = null, editingGuarantorIndex = null)
    }

    fun onRemoveGuarantor(index: Int) {
        _uiState.value = _uiState.value.copy(
            guarantors = _uiState.value.guarantors.toMutableList().apply { removeAt(index) }
        )
    }

    // --- Next of Kin ---

    fun onStartAddNextOfKin() {
        if (_uiState.value.nextOfKin.size >= MAX_NEXT_OF_KIN) return
        _uiState.value = _uiState.value.copy(editingNextOfKin = NextOfKinEntry(), editingNextOfKinIndex = null)
    }

    fun onStartEditNextOfKin(index: Int) {
        val s = _uiState.value
        _uiState.value = s.copy(editingNextOfKin = s.nextOfKin.getOrNull(index), editingNextOfKinIndex = index)
    }

    fun onCancelNextOfKinEdit() {
        _uiState.value = _uiState.value.copy(editingNextOfKin = null, editingNextOfKinIndex = null)
    }

    fun onNextOfKinFieldChange(update: (NextOfKinEntry) -> NextOfKinEntry) {
        val draft = _uiState.value.editingNextOfKin ?: return
        _uiState.value = _uiState.value.copy(editingNextOfKin = update(draft))
    }

    fun onSaveNextOfKin() {
        val s = _uiState.value
        val draft = s.editingNextOfKin ?: return
        if (draft.firstname.isBlank() || draft.surname.isBlank() || draft.relationship.isBlank() ||
            draft.mobile.isBlank() || draft.employmentStatus.isBlank() || draft.county.isBlank()
        ) {
            return
        }
        val updated = upsert(s.nextOfKin, s.editingNextOfKinIndex, draft)
        _uiState.value = s.copy(nextOfKin = updated, editingNextOfKin = null, editingNextOfKinIndex = null)
    }

    fun onRemoveNextOfKin(index: Int) {
        _uiState.value = _uiState.value.copy(
            nextOfKin = _uiState.value.nextOfKin.toMutableList().apply { removeAt(index) }
        )
    }

    private fun <T> upsert(list: List<T>, index: Int?, value: T): List<T> =
        if (index != null) list.toMutableList().apply { set(index, value) } else list + value

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
                val houseImageUrl = s.residenceFile?.let { repository.uploadPhoto(it, "documents").getOrThrow() }
                val businessImageUrls = s.businessImageFiles.map {
                    repository.uploadPhoto(it, "business_images").getOrThrow()
                }
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

                val securityItems = s.securityItems.map { uploadBorrowerSecurityItem(it) }

                val guarantorFields = s.guarantors.map { guarantor ->
                    GuarantorFields(
                        prefix = guarantor.prefix.ifBlank { null },
                        maritalStatus = guarantor.maritalStatus.ifBlank { null },
                        gender = guarantor.gender.ifBlank { null },
                        mobile = guarantor.mobile,
                        postalAddress = guarantor.postalAddress.ifBlank { null },
                        code = guarantor.postalCode.toIntOrNull(),
                        occupation = guarantor.occupation.ifBlank { null },
                        // guarantors table has no relationship_other column — unlike next_of_kin,
                        // the "Other" free text goes straight into relationship itself.
                        relationship = if (guarantor.relationship == "Other") {
                            guarantor.relationshipOther.ifBlank { "Other" }
                        } else {
                            guarantor.relationship
                        },
                        dateOfBirth = guarantor.dateOfBirth.ifBlank { null },
                        residenceStatus = guarantor.residenceStatus.ifBlank { null },
                        idNumber = guarantor.idNumber,
                        firstname = guarantor.firstname,
                        surname = guarantor.surname,
                        middlename = guarantor.middlename.ifBlank { null },
                        cityTown = guarantor.cityTown.ifBlank { null },
                        passportUrl = guarantor.passportFile?.let { repository.uploadPhoto(it, "documents").getOrThrow() },
                        idFrontUrl = guarantor.idFrontFile?.let { repository.uploadPhoto(it, "documents").getOrThrow() },
                        idBackUrl = guarantor.idBackFile?.let { repository.uploadPhoto(it, "documents").getOrThrow() },
                        county = guarantor.county.ifBlank { null },
                        alternativeNumber = guarantor.alternativeNumber.ifBlank { null }
                    )
                }
                // Backend only ever attaches guarantorSecurityItems to the first guarantor — matches that quirk.
                val guarantorSecurityItems = s.guarantors.firstOrNull()?.securityItems.orEmpty()
                    .map { uploadGuarantorSecurityItem(it) }

                val nextOfKinFields = s.nextOfKin.map { kin ->
                    NextOfKinFields(
                        firstname = kin.firstname,
                        middlename = kin.middlename.ifBlank { null },
                        surname = kin.surname,
                        idNumber = kin.idNumber.toIntOrNull(),
                        // next_of_kin DOES have a relationship_other column (unlike guarantors) — keep
                        // "Other" as the literal relationship value and put the free text in its own field.
                        relationship = kin.relationship,
                        relationshipOther = if (kin.relationship == "Other") kin.relationshipOther.ifBlank { null } else null,
                        mobile = kin.mobile,
                        alternativeNumber = kin.alternativeNumber.ifBlank { null },
                        employmentStatus = kin.employmentStatus,
                        companyName = kin.companyName.ifBlank { null },
                        salary = kin.salary.toDoubleOrNull(),
                        businessName = kin.businessName.ifBlank { null },
                        businessIncome = kin.businessIncome.toDoubleOrNull(),
                        county = kin.county,
                        cityTown = kin.cityTown.ifBlank { null }
                    )
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
                        requestedAmount = s.requestedAmount.toDoubleOrNull(),
                        idFrontUrl = idFrontUrl,
                        idBackUrl = idBackUrl,
                        passportUrl = selfieUrl,
                        houseImageUrl = houseImageUrl
                    ),
                    spouse = spouse,
                    businessImages = businessImageUrls.ifEmpty { null },
                    securityItems = securityItems.ifEmpty { null },
                    guarantors = guarantorFields.ifEmpty { null },
                    guarantorSecurityItems = guarantorSecurityItems.ifEmpty { null },
                    nextOfKins = nextOfKinFields.ifEmpty { null }
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

    // Both use the "documents" bucket — the only confirmed-allowed bucket that fits security/ID
    // documentation photos (CustomerServiceImpl's allow-list doesn't have a dedicated one for these).
    private suspend fun uploadBorrowerSecurityItem(entry: SecurityItemEntry): SecurityItemFields {
        val imageUrls = entry.photos.map { repository.uploadPhoto(it, "documents").getOrThrow() }
        return SecurityItemFields(
            item = resolveSecurityItemName(entry),
            description = entry.description.ifBlank { null },
            identification = entry.identification.ifBlank { null },
            value = entry.value.toDoubleOrNull(),
            images = imageUrls
        )
    }

    private suspend fun uploadGuarantorSecurityItem(entry: SecurityItemEntry): GuarantorSecurityItemFields {
        val imageUrls = entry.photos.map { repository.uploadPhoto(it, "documents").getOrThrow() }
        return GuarantorSecurityItemFields(
            item = resolveSecurityItemName(entry),
            description = entry.description.ifBlank { null },
            identification = entry.identification.ifBlank { null },
            estimatedMarketValue = entry.value.toDoubleOrNull(),
            images = imageUrls
        )
    }

    private fun resolveSecurityItemName(entry: SecurityItemEntry): String =
        if (entry.item == OTHER_OPTION) entry.itemOther else entry.item
}
