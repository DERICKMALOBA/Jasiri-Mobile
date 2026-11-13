package com.jasiri.fieldofficer.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionItemDto(
    val installmentId: String,
    val loanId: Long,
    val loanNumber: String? = null,
    val customerId: Long? = null,
    val customerName: String? = null,
    val customerMobile: String? = null,
    val dueDate: String? = null,
    val dueAmount: Double? = null,
    val paidAmount: Double? = null,
    val status: String? = null,
    val daysOverdue: Int? = null,
    val paidDate: String? = null
)

@Serializable
data class CollectionItemsEnvelope(
    val success: Boolean = true,
    val data: CollectionItemsData? = null
)

@Serializable
data class CollectionItemsData(
    val items: List<CollectionItemDto> = emptyList()
)
