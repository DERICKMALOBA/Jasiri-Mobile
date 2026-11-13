package com.jasiri.fieldofficer.feature.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.performance.PerformanceRepository
import com.jasiri.fieldofficer.core.performance.PerformanceSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerformanceUiState(
    val summary: PerformanceSummary? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val repository: PerformanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerformanceUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getPerformance()
                .onSuccess { summary -> _uiState.value = PerformanceUiState(summary = summary, isLoading = false) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Couldn't load performance data — check your connection"
                    )
                }
        }
    }
}
