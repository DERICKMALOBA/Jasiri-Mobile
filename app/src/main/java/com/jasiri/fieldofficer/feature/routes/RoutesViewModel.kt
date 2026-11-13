package com.jasiri.fieldofficer.feature.routes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.routes.RouteRepository
import com.jasiri.fieldofficer.core.routes.RouteStop
import com.jasiri.fieldofficer.core.routes.VisitStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutesUiState(
    val stops: List<RouteStop> = emptyList(),
    val isLoading: Boolean = true
) {
    val completedCount: Int get() = stops.count { it.status == VisitStatus.VISITED }
    val nextStopId: Long? get() = stops.firstOrNull { it.status == VisitStatus.PENDING }?.id
}

@HiltViewModel
class RoutesViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            routeRepository.stops.collect { stops ->
                _uiState.value = RoutesUiState(stops = stops, isLoading = false)
            }
        }
    }
}
