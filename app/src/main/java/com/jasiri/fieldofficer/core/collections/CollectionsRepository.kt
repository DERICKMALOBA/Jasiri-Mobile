package com.jasiri.fieldofficer.core.collections

import com.jasiri.fieldofficer.core.network.StkPushApi
import com.jasiri.fieldofficer.core.network.dto.StkPushInitiateRequest
import com.jasiri.fieldofficer.core.network.dto.StkPushStatusDto
import javax.inject.Inject
import javax.inject.Singleton

/** Terminal statuses the backend's mpesa_c2b_transactions row can land on — see StkPushService#getStatus. */
val STK_PUSH_TERMINAL_STATUSES = setOf("applied", "suspense", "failed")

@Singleton
class CollectionsRepository @Inject constructor(
    private val api: StkPushApi
) {
    suspend fun initiateStkPush(phone: String, amount: Double): Result<String> = runCatching {
        api.initiate(StkPushInitiateRequest(amount = amount, phone = normalizePhone(phone))).pendingTxId
    }

    suspend fun getStatus(pendingTxId: String): Result<StkPushStatusDto> = runCatching {
        api.getStatus(pendingTxId)
    }

    /** Safaricom requires 2547XXXXXXXX (12 digits, no "+", no leading "0") — normalize common input forms. */
    fun normalizePhone(input: String): String {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.startsWith("254") && digits.length == 12 -> digits
            digits.startsWith("0") && digits.length == 10 -> "254" + digits.substring(1)
            digits.length == 9 -> "254$digits"
            else -> digits
        }
    }

    fun isValidPhone(input: String): Boolean = normalizePhone(input).matches(Regex("^254[17][0-9]{8}$"))
}
