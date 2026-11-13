package com.jasiri.fieldofficer.feature.customer.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.customer.CustomerRepository
import com.jasiri.fieldofficer.core.network.dto.CustomerDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CustomerFilter(val label: String) {
    ALL("All"),
    KYC_PENDING("KYC Pending")
}

data class CustomerListUiState(
    val customers: List<CustomerDto> = emptyList(),
    val query: String = "",
    val filter: CustomerFilter = CustomerFilter.ALL,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val visibleCustomers: List<CustomerDto>
        get() = customers
            .filter { filter == CustomerFilter.ALL || !it.isKycComplete }
            .filter {
                query.isBlank() ||
                    it.fullName.contains(query, ignoreCase = true) ||
                    (it.mobile?.contains(query) == true)
            }
}

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerListUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.listCustomers()
                .onSuccess { customers ->
                    _uiState.value = _uiState.value.copy(customers = customers, isLoading = false)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Couldn't load customers — check your connection"
                    )
                }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(query = value)
    }

    fun onFilterChange(filter: CustomerFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }
}
