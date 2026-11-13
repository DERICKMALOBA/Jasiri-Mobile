package com.jasiri.fieldofficer.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identifier: String, // email or phone — mirrors the web login's identifier field
    val password: String,
    val deviceType: String = "mobile",
    val deviceId: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String? = null,
    val role: String,
    val tenantId: String
)

@Serializable
data class RefreshRequest(val refreshToken: String)

@Serializable
data class RefreshResponse(val accessToken: String, val refreshToken: String)
