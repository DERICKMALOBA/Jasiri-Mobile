package com.jasiri.fieldofficer.core.leads

data class Lead(
    val id: Long,
    val name: String,
    val mobile: String,
    val businessIdea: String,
    val notes: String = ""
)
