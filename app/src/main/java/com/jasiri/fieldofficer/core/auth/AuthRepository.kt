package com.jasiri.fieldofficer.core.auth

import android.content.Context
import android.provider.Settings
import com.jasiri.fieldofficer.BuildConfig
import com.jasiri.fieldofficer.core.network.AuthApi
import com.jasiri.fieldofficer.core.network.dto.LoginRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) {
    suspend fun login(identifier: String, password: String): Result<Unit> = runCatching {
        // Backend DB isn't reachable yet — skip the real auth call in debug builds so the
        // rest of the app can be exercised. Never applies to release (BuildConfig.DEBUG is
        // compiled to false there).
        if (BuildConfig.DEBUG) {
            tokenManager.saveTokens("dev-access-token", "dev-refresh-token")
            return@runCatching
        }
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown-device"
        val response = authApi.login(
            LoginRequest(identifier = identifier, password = password, deviceId = deviceId)
        )
        tokenManager.saveTokens(response.accessToken, response.refreshToken)
    }

    fun logout() = tokenManager.clear()

    fun isLoggedIn(): Boolean = tokenManager.hasSession()
}
