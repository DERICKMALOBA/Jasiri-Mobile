package com.jasiri.fieldofficer.feature.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasiri.fieldofficer.core.collections.CollectionsRepository
import com.jasiri.fieldofficer.core.network.dto.CollectionItemDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CollectionBucket(val apiValue: String, val label: String) {
    OVERDUE("overdue", "Overdue"),
    DUE_TODAY("due_today", "Due Today"),
    DUE_MONTH("due_month", "Due This Month"),
    COLLECTED("collected", "Collected")
}

data class CollectionsListUiState(
    val bucket: CollectionBucket = CollectionBucket.OVERDUE,
    val items: List<CollectionItemDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class CollectionsListViewModel @Inject constructor(
    private val repository: CollectionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionsListUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun onBucketSelected(bucket: CollectionBucket) {
        _uiState.value = _uiState.value.copy(bucket = bucket)
        load()
    }

    fun load() {
        val bucket = _uiState.value.bucket
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.listCollections(bucket.apiValue)
                .onSuccess { items -> _uiState.value = _uiState.value.copy(items = items, isLoading = false) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Couldn't load collections — check your connection"
                    )
                }
        }
    }
}

fun CollectionItemDto.outstandingAmount(): Double =
    ((dueAmount ?: 0.0) - (paidAmount ?: 0.0)).coerceAtLeast(0.0)
