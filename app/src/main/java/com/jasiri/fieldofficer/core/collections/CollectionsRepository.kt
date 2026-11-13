package com.jasiri.fieldofficer.core.collections

import com.jasiri.fieldofficer.BuildConfig
import com.jasiri.fieldofficer.core.network.CollectionsApi
import com.jasiri.fieldofficer.core.network.StkPushApi
import com.jasiri.fieldofficer.core.network.dto.CollectionItemDto
import com.jasiri.fieldofficer.core.network.dto.StkPushInitiateRequest
import com.jasiri.fieldofficer.core.network.dto.StkPushStatusDto
import javax.inject.Inject
import javax.inject.Singleton

/** Terminal statuses the backend's mpesa_c2b_transactions row can land on — see StkPushService#getStatus. */
val STK_PUSH_TERMINAL_STATUSES = setOf("applied", "suspense", "failed")

@Singleton
class CollectionsRepository @Inject constructor(
    private val stkPushApi: StkPushApi,
    private val collectionsApi: CollectionsApi
) {
    suspend fun initiateStkPush(phone: String, amount: Double): Result<String> = runCatching {
        stkPushApi.initiate(StkPushInitiateRequest(amount = amount, phone = normalizePhone(phone))).pendingTxId
    }

    suspend fun getStatus(pendingTxId: String): Result<StkPushStatusDto> = runCatching {
        stkPushApi.getStatus(pendingTxId)
    }

    suspend fun listCollections(bucket: String): Result<List<CollectionItemDto>> = runCatching {
        collectionsApi.list(bucket).data?.items ?: emptyList()
    }.recoverIfDebugAndFailed { mockItems(bucket) }

    // Backend DB isn't reliably reachable yet — in debug builds, fall back to clearly-fake local
    // data instead of surfacing a DB error, so the list stays populated for UI testing. Never
    // applies to release builds, and never used for the STK push initiate/status calls above —
    // those move real money and must never be faked.
    private inline fun <T> Result<T>.recoverIfDebugAndFailed(fallback: () -> T): Result<T> =
        if (isFailure && BuildConfig.DEBUG) Result.success(fallback()) else this

    private fun mockItems(bucket: String): List<CollectionItemDto> = when (bucket) {
        "overdue" -> listOf(
            CollectionItemDto("mock-1", 2001L, "LN-2001", 1001L, "Amina Otieno", "254712345001", "2026-11-05", 5000.0, 0.0, "overdue", 8, null),
            CollectionItemDto("mock-2", 2002L, "LN-2002", 1002L, "Brian Mutiso", "254712345002", "2026-10-29", 8000.0, 2000.0, "overdue", 15, null)
        )
        "due_today" -> listOf(
            CollectionItemDto("mock-3", 2003L, "LN-2003", 1003L, "Cynthia Wanjiru", "254712345003", "2026-11-13", 4500.0, 0.0, "pending", 0, null)
        )
        "due_month" -> listOf(
            CollectionItemDto("mock-3", 2003L, "LN-2003", 1003L, "Cynthia Wanjiru", "254712345003", "2026-11-13", 4500.0, 0.0, "pending", 0, null),
            CollectionItemDto("mock-4", 2004L, "LN-2004", 1004L, "David Kimani", "254712345004", "2026-11-25", 6200.0, 0.0, "pending", 0, null)
        )
        else -> listOf(
            CollectionItemDto("mock-5", 2005L, "LN-2005", 1005L, "Esther Njeri", "254712345005", "2026-11-10", 3000.0, 3000.0, "paid", 0, "2026-11-10")
        )
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
