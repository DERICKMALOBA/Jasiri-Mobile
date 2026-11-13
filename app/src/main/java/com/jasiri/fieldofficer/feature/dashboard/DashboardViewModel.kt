package com.jasiri.fieldofficer.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.leads.LeadRepository
import com.jasiri.fieldofficer.core.network.dto.DashboardSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val summary: DashboardSummaryDto? = null,
    val isPlaceholder: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val leadRepository: LeadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val (summary, placeholder) = repository.getSummary()
            leadRepository.leads.collect { leads ->
                _uiState.value = DashboardUiState(
                    summary = summary.copy(leadsCount = leads.size),
                    isPlaceholder = placeholder,
                    isLoading = false
                )
            }
        }
    }
}
