package com.jasiri.fieldofficer.core.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Access/refresh tokens, Keystore-backed via EncryptedSharedPreferences — never
 * plain SharedPreferences or localStorage-equivalent. Matches the plan's
 * non-negotiable token-storage rule.
 */
@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "jasiri_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _accessToken = MutableStateFlow(prefs.getString(KEY_ACCESS, null))
    val accessToken = _accessToken.asStateFlow()

    private val _refreshToken = MutableStateFlow(prefs.getString(KEY_REFRESH, null))
    val refreshToken = _refreshToken.asStateFlow()

    fun saveTokens(access: String, refresh: String) {
        prefs.edit().putString(KEY_ACCESS, access).putString(KEY_REFRESH, refresh).apply()
        _accessToken.value = access
        _refreshToken.value = refresh
    }

    fun clear() {
        prefs.edit().clear().apply()
        _accessToken.value = null
        _refreshToken.value = null
    }

    fun hasSession(): Boolean = _accessToken.value != null

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
    }
}
