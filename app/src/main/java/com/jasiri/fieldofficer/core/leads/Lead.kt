package com.jasiri.fieldofficer.core.leads

data class Lead(
    val id: Long,
    val mobile: String,
    val firstname: String,
    val surname: String,
    val businessName: String,
    val industry: String,
    val businessType: String,
    val county: String,
    val leadSource: String = "Field Agent",
    val status: String = "Cold"
) {
    val fullName: String get() = "$firstname $surname".trim()
}
