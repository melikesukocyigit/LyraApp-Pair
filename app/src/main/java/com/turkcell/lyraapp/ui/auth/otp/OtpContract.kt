package com.turkcell.lyraapp.ui.auth.otp

data class OtpUiState(
    val phoneNumber: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val isVerifyEnabled: Boolean = false,
    val resendCooldownSeconds: Int = 60,
)

sealed interface OtpIntent {
    data class CodeChanged(val code: String) : OtpIntent
    data object Submit : OtpIntent
    data object ResendOtp : OtpIntent
}

sealed interface OtpEffect {
    data object NavigateToHome : OtpEffect
    data object NavigateToCompleteProfile : OtpEffect
    data class ShowError(val message: String) : OtpEffect
}
