package com.jasiri.fieldofficer.feature.customer.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.customer.CustomerRepository
import com.jasiri.fieldofficer.core.network.dto.CustomerBundleDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerDetailUiState(
    val bundle: CustomerBundleDto? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun load(customerId: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getCustomerFull(customerId)
                .onSuccess { bundle -> _uiState.value = _uiState.value.copy(bundle = bundle, isLoading = false) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Couldn't load this customer's profile"
                    )
                }
        }
    }
}
