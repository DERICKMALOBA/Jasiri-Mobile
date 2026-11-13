package com.jasiri.fieldofficer.feature.leads

import androidx.lifecycle.ViewModel
import com.jasiri.fieldofficer.core.leads.LeadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AddLeadUiState(
    val mobile: String = "",
    val firstname: String = "",
    val surname: String = "",
    val businessName: String = "",
    val industry: String = "",
    val industryOther: String = "",
    val businessType: String = "",
    val businessTypeOther: String = "",
    val county: String = "",
    val error: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class AddLeadViewModel @Inject constructor(
    private val leadRepository: LeadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLeadUiState())
    val uiState = _uiState.asStateFlow()

    fun onMobileChange(v: String) { _uiState.value = _uiState.value.copy(mobile = v, error = null) }
    fun onFirstnameChange(v: String) { _uiState.value = _uiState.value.copy(firstname = v, error = null) }
    fun onSurnameChange(v: String) { _uiState.value = _uiState.value.copy(surname = v, error = null) }
    fun onBusinessNameChange(v: String) { _uiState.value = _uiState.value.copy(businessName = v, error = null) }
    fun onIndustryChange(v: String) {
        _uiState.value = _uiState.value.copy(industry = v, businessType = "", businessTypeOther = "")
    }
    fun onIndustryOtherChange(v: String) { _uiState.value = _uiState.value.copy(industryOther = v) }
    fun onBusinessTypeChange(v: String) { _uiState.value = _uiState.value.copy(businessType = v) }
    fun onBusinessTypeOtherChange(v: String) { _uiState.value = _uiState.value.copy(businessTypeOther = v) }
    fun onCountyChange(v: String) { _uiState.value = _uiState.value.copy(county = v) }

    fun save() {
        val s = _uiState.value
        if (!s.mobile.matches(Regex("^[0-9]{10,15}$"))) {
            _uiState.value = s.copy(error = "Enter a valid phone number (digits only)")
            return
        }
        if (s.firstname.isBlank() || s.surname.isBlank()) {
            _uiState.value = s.copy(error = "Enter the lead's first name and surname")
            return
        }
        if (s.businessName.isBlank()) {
            _uiState.value = s.copy(error = "Enter the business / enterprise name")
            return
        }
        leadRepository.addLead(
            mobile = s.mobile,
            firstname = s.firstname,
            surname = s.surname,
            businessName = s.businessName,
            industry = s.industry.ifBlank { s.industryOther },
            businessType = s.businessType.ifBlank { s.businessTypeOther },
            county = s.county
        )
        _uiState.value = s.copy(isComplete = true)
    }
}
