package com.jasiri.fieldofficer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jasiri.fieldofficer.core.database.entity.OutboxEntryEntity

@Dao
interface OutboxDao {
    @Query("SELECT * FROM outbox WHERE status IN ('PENDING','FAILED') ORDER BY createdAt ASC")
    suspend fun getPending(): List<OutboxEntryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun enqueue(entry: OutboxEntryEntity)

    @Update
    suspend fun update(entry: OutboxEntryEntity)
}
