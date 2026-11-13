package com.jasiri.fieldofficer.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jasiri.fieldofficer.core.database.dao.OutboxDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Client-side analogue of the backend's job-queue pattern: periodically drains
 * the local outbox of pending writes (customer registrations, loan
 * applications, visit outcomes, repayments) created while offline.
 *
 * Not wired to real endpoints yet — this is the scaffold the next feature
 * work (Phase 1 offline sync, per the plan) builds on.
 */
@HiltWorker
class OutboxSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val outboxDao: OutboxDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pending = outboxDao.getPending()
        if (pending.isEmpty()) return Result.success()

        // TODO: dispatch each entry to the matching repository (customer/loan/
        // visit/repayment) by entityType, sending idempotencyKey as a request
        // header, then mark SYNCED or FAILED (incrementing attempts).
        return Result.success()
    }
}
