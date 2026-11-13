package com.jasiri.fieldofficer.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val kycStatus: String,
    val arrearsAmount: Double,
    val updatedAt: Long
)
