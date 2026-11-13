package com.jasiri.fieldofficer.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One pending offline write. idempotencyKey is generated client-side and sent
 * as a header on the eventual request, so a retried sync after a dropped
 * connection never creates a duplicate customer/application/payment record.
 */
@Entity(tableName = "outbox")
data class OutboxEntryEntity(
    @PrimaryKey val idempotencyKey: String,
    val entityType: String, // CUSTOMER, LOAN_APPLICATION, VISIT, REPAYMENT
    val payloadJson: String,
    val status: String = "PENDING", // PENDING, SYNCING, SYNCED, FAILED
    val attempts: Int = 0,
    val createdAt: Long,
    val lastError: String? = null
)
