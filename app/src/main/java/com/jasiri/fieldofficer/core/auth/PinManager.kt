package com.jasiri.fieldofficer.core.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local PIN gate for quick re-entry after the first online login (biometric
 * unlock is the other option, per the plan — not yet wired up here).
 *
 * The PIN itself is never sent to the backend; it only unlocks the locally
 * cached session. Stored as a salted SHA-256 hash inside Keystore-backed
 * EncryptedSharedPreferences — sufficient for an MVP, revisit with a proper
 * Keystore-bound key if this needs to resist a rooted-device attacker.
 */
@Singleton
class PinManager @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "jasiri_pin_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun isPinSet(): Boolean = prefs.contains(KEY_HASH)

    fun setPin(pin: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        prefs.edit()
            .putString(KEY_SALT, salt.joinToString(","))
            .putString(KEY_HASH, hash(pin, salt))
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val saltString = prefs.getString(KEY_SALT, null) ?: return false
        val salt = saltString.split(",").map { it.toInt().toByte() }.toByteArray()
        val expected = prefs.getString(KEY_HASH, null) ?: return false
        return hash(pin, salt) == expected
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun hash(pin: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val bytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val KEY_SALT = "pin_salt"
        const val KEY_HASH = "pin_hash"
    }
}
