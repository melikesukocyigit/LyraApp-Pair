package com.turkcell.lyraapp.ui.auth.register

/**
 * Register ("Hesap oluştur") ekranının MVI sözleşmesi.
 */
data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isTermsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val passwordStrength: Int = 0,
    val isRegisterEnabled: Boolean = false,
)

sealed interface RegisterIntent {
    data class FirstNameChanged(val value: String) : RegisterIntent
    data class LastNameChanged(val value: String) : RegisterIntent
    data class PhoneNumberChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class ConfirmPasswordChanged(val value: String) : RegisterIntent // EKLENDİ
    data class TermsAcceptedChanged(val value: Boolean) : RegisterIntent
    data object TogglePasswordVisibility : RegisterIntent
    data object Submit : RegisterIntent
    data object LoginClicked : RegisterIntent
}

sealed interface RegisterEffect {
    data object NavigateToLogin : RegisterEffect
    data class ShowError(val message: String) : RegisterEffect
}