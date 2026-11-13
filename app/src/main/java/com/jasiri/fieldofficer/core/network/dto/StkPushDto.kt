package com.jasiri.fieldofficer.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StkPushInitiateRequest(
    val amount: Double,
    val phone: String,
    @SerialName("account_reference") val accountReference: String? = null,
    @SerialName("loan_id") val loanId: Long? = null,
    @SerialName("customer_id") val customerId: Long? = null
)

@Serializable
data class StkPushInitiateResponse(
    val success: Boolean = true,
    val message: String? = null,
    @SerialName("pending_tx_id") val pendingTxId: String
)

/**
 * status values: "pending" (awaiting the customer's PIN entry), "processing" (callback arrived,
 * being matched), "applied" (success, credited), "suspense" (Safaricom confirmed payment but it
 * couldn't be auto-matched — likely succeeded, needs follow-up), "failed" (declined/cancelled/
 * wrong PIN/timeout).
 */
@Serializable
data class StkPushStatusDto(
    val id: String,
    val status: String,
    val amount: Double? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val description: String? = null,
    @SerialName("callback_status") val callbackStatus: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
