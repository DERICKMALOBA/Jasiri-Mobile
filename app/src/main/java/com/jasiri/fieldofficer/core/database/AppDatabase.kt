package com.jasiri.fieldofficer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jasiri.fieldofficer.core.database.dao.CustomerDao
import com.jasiri.fieldofficer.core.database.dao.OutboxDao
import com.jasiri.fieldofficer.core.database.entity.CustomerEntity
import com.jasiri.fieldofficer.core.database.entity.OutboxEntryEntity

@Database(
    entities = [CustomerEntity::class, OutboxEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun outboxDao(): OutboxDao
}
