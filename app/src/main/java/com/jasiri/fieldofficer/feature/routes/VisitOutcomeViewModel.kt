package com.jasiri.fieldofficer.feature.routes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jasiri.fieldofficer.core.routes.RouteRepository
import com.jasiri.fieldofficer.core.routes.RouteStop
import com.jasiri.fieldofficer.core.routes.VisitOutcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class VisitOutcomeUiState(
    val stop: RouteStop? = null,
    val selectedOutcome: VisitOutcome? = null,
    val note: String = "",
    val isSaved: Boolean = false
)

@HiltViewModel
class VisitOutcomeViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stopId: Long = checkNotNull(savedStateHandle["stopId"])

    private val _uiState = MutableStateFlow(
        VisitOutcomeUiState(stop = routeRepository.stops.value.find { it.id == stopId })
    )
    val uiState = _uiState.asStateFlow()

    fun onOutcomeSelected(outcome: VisitOutcome) {
        _uiState.value = _uiState.value.copy(selectedOutcome = outcome)
    }

    fun onNoteChange(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun saveAndContinue() {
        val outcome = _uiState.value.selectedOutcome ?: return
        routeRepository.markVisited(stopId, outcome, _uiState.value.note)
        _uiState.value = _uiState.value.copy(isSaved = true)
    }
}
