package com.jasiri.fieldofficer.feature.collections

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.collections.CollectionsRepository
import com.jasiri.fieldofficer.core.collections.STK_PUSH_TERMINAL_STATUSES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val POLL_INTERVAL_MS = 3_000L
private const val POLL_TIMEOUT_MS = 90_000L

data class CollectPaymentUiState(
    val phone: String = "",
    val amount: String = "",
    val error: String? = null,
    val isSubmitting: Boolean = false,
    val pendingTxId: String? = null,
    val status: String? = null,
    val statusDetail: String? = null,
    val timedOut: Boolean = false
) {
    val isTerminal: Boolean get() = status in STK_PUSH_TERMINAL_STATUSES
}

@HiltViewModel
class CollectPaymentViewModel @Inject constructor(
    private val repository: CollectionsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CollectPaymentUiState(
            phone = savedStateHandle.get<String>("phone").orEmpty(),
            amount = savedStateHandle.get<String>("amount").orEmpty()
        )
    )
    val uiState = _uiState.asStateFlow()

    private var pollJob: Job? = null

    fun onPhoneChange(v: String) { _uiState.value = _uiState.value.copy(phone = v, error = null) }
    fun onAmountChange(v: String) { _uiState.value = _uiState.value.copy(amount = v, error = null) }

    fun sendStkPush() {
        val s = _uiState.value
        if (!repository.isValidPhone(s.phone)) {
            _uiState.value = s.copy(error = "Enter a valid Safaricom number (e.g. 0712345678)")
            return
        }
        val amount = s.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = s.copy(error = "Enter a valid amount")
            return
        }
        _uiState.value = s.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            repository.initiateStkPush(s.phone, amount)
                .onSuccess { pendingTxId ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        pendingTxId = pendingTxId,
                        status = "pending"
                    )
                    startPolling(pendingTxId)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = e.message ?: "Couldn't send the STK push — check your connection and try again"
                    )
                }
        }
    }

    private fun startPolling(pendingTxId: String) {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            val deadline = System.currentTimeMillis() + POLL_TIMEOUT_MS
            while (System.currentTimeMillis() < deadline) {
                delay(POLL_INTERVAL_MS)
                val result = repository.getStatus(pendingTxId)
                result.onSuccess { dto ->
                    _uiState.value = _uiState.value.copy(status = dto.status, statusDetail = dto.callbackStatus)
                    if (dto.status in STK_PUSH_TERMINAL_STATUSES) return@launch
                }
            }
            if (_uiState.value.status !in STK_PUSH_TERMINAL_STATUSES) {
                _uiState.value = _uiState.value.copy(timedOut = true)
            }
        }
    }

    fun startNewPayment() {
        pollJob?.cancel()
        _uiState.value = CollectPaymentUiState()
    }

    override fun onCleared() {
        pollJob?.cancel()
        super.onCleared()
    }
}
