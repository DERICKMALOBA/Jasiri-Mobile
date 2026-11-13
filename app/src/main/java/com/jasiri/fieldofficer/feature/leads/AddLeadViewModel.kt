package com.jasiri.fieldofficer.feature.leads

import androidx.lifecycle.ViewModel
import com.jasiri.fieldofficer.core.leads.LeadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AddLeadUiState(
    val name: String = "",
    val mobile: String = "",
    val businessIdea: String = "",
    val notes: String = "",
    val error: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class AddLeadViewModel @Inject constructor(
    private val leadRepository: LeadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLeadUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(v: String) { _uiState.value = _uiState.value.copy(name = v, error = null) }
    fun onMobileChange(v: String) { _uiState.value = _uiState.value.copy(mobile = v, error = null) }
    fun onBusinessIdeaChange(v: String) { _uiState.value = _uiState.value.copy(businessIdea = v) }
    fun onNotesChange(v: String) { _uiState.value = _uiState.value.copy(notes = v) }

    fun save() {
        val s = _uiState.value
        if (s.name.isBlank()) {
            _uiState.value = s.copy(error = "Enter the lead's name")
            return
        }
        if (!s.mobile.matches(Regex("^[0-9]{10,15}$"))) {
            _uiState.value = s.copy(error = "Enter a valid phone number (digits only)")
            return
        }
        leadRepository.addLead(name = s.name, mobile = s.mobile, businessIdea = s.businessIdea, notes = s.notes)
        _uiState.value = s.copy(isComplete = true)
    }
}
