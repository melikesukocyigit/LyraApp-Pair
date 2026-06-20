package com.turkcell.lyraapp.ui.auth.completeprofile

data class CompleteProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val day: String = "",
    val month: String = "",
    val year: String = "",
    val isLoading: Boolean = false,
    val isSubmitEnabled: Boolean = false,
)

sealed interface CompleteProfileIntent {
    data class FirstNameChanged(val value: String) : CompleteProfileIntent
    data class LastNameChanged(val value: String) : CompleteProfileIntent
    data class DayChanged(val value: String) : CompleteProfileIntent
    data class MonthChanged(val value: String) : CompleteProfileIntent
    data class YearChanged(val value: String) : CompleteProfileIntent
    data object Submit : CompleteProfileIntent
}

sealed interface CompleteProfileEffect {
    data object NavigateToHome : CompleteProfileEffect
    data class ShowError(val message: String) : CompleteProfileEffect
}
