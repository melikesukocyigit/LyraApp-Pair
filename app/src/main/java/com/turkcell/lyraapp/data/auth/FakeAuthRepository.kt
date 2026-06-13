package com.turkcell.lyraapp.data.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [AuthRepository]'nin SharedPreferences destekli sahte implementasyonu.
 * Oturum ve tema tercihlerini cihaz hafızasında saklar.
 */
@Singleton
class FakeAuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs = context.getSharedPreferences("lyra_prefs", Context.MODE_PRIVATE)

    override suspend fun login(phoneNumber: String, password: String): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        
        val cleanPhone = phoneNumber.trim()
        val cleanPassword = password.trim()

        // Sabit test kullanıcısı
        if (cleanPhone == "5551234567" && cleanPassword == "123456") {
            saveSession(cleanPhone, "Zeynep Kalaycı")
            return Result.success(Unit)
        }

        // Kayıtlı kullanıcı kontrolü
        val savedPassword = prefs.getString("user_pass_$cleanPhone", null)
        val savedName = prefs.getString("user_name_$cleanPhone", null)

        return if (savedPassword == cleanPassword && savedName != null) {
            saveSession(cleanPhone, savedName)
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Telefon numarası veya şifre hatalı."))
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        password: String,
    ): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        val cleanPhone = phoneNumber.trim()
        val fullName = "${firstName.trim()} ${lastName.trim()}"
        
        prefs.edit()
            .putString("user_pass_$cleanPhone", password.trim())
            .putString("user_name_$cleanPhone", fullName)
            .apply()
            
        return Result.success(Unit)
    }

    override suspend fun logout(): Result<Unit> {
        delay(NETWORK_DELAY_MS / 2)
        prefs.edit()
            .remove(KEY_CURRENT_PHONE)
            .remove(KEY_CURRENT_NAME)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
        return Result.success(Unit)
    }

    override fun getLoggedInUserName(): String? = prefs.getString(KEY_CURRENT_NAME, null)

    override fun isUserLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    override fun setThemeDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_THEME_DARK, isDark).apply()
    }

    override fun isDarkModePreferred(): Boolean = prefs.getBoolean(KEY_THEME_DARK, true)

    private fun saveSession(phone: String, name: String) {
        prefs.edit()
            .putString(KEY_CURRENT_PHONE, phone)
            .putString(KEY_CURRENT_NAME, name)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    private companion object {
        const val NETWORK_DELAY_MS = 1_000L
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_CURRENT_PHONE = "current_phone"
        const val KEY_CURRENT_NAME = "current_name"
        const val KEY_THEME_DARK = "theme_dark"
    }
}
