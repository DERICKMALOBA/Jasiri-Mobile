package com.jasiri.fieldofficer.feature.routes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.customer.CustomerRepository
import com.jasiri.fieldofficer.core.network.dto.CustomerDto
import com.jasiri.fieldofficer.core.routes.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectVisitCustomersUiState(
    val customers: List<CustomerDto> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class SelectVisitCustomersViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectVisitCustomersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val alreadyPlanned = routeRepository.stops.value.mapNotNull { it.customer.id }.toSet()
        viewModelScope.launch {
            customerRepository.listCustomers()
                .onSuccess { customers ->
                    _uiState.value = _uiState.value.copy(
                        customers = customers,
                        selectedIds = alreadyPlanned,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Couldn't load customers"
                    )
                }
        }
    }

    fun toggleSelected(customerId: Long) {
        val current = _uiState.value.selectedIds
        _uiState.value = _uiState.value.copy(
            selectedIds = if (customerId in current) current - customerId else current + customerId
        )
    }

    fun saveRoute() {
        val state = _uiState.value
        val selected = state.customers.filter { it.id != null && it.id in state.selectedIds }
        routeRepository.planStops(selected)
        _uiState.value = state.copy(isSaved = true)
    }
}
