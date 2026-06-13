package com.turkcell.lyraapp.ui.home

import com.turkcell.lyraapp.data.home.PlaylistForYou
import com.turkcell.lyraapp.data.home.QuickPick
import com.turkcell.lyraapp.data.home.RecentlyPlayed


data class HomeUiState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = true,
    val greeting: String = "",
    val userInitials: String = "",
    val quickPicks: List<QuickPick> = emptyList(),
    val recentlyPlayed: List<RecentlyPlayed> = emptyList(),
    val playlistsForYou: List<PlaylistForYou> = emptyList(),
)

sealed interface HomeIntent {
    data object Retry : HomeIntent
    data object Logout : HomeIntent
    data object ToggleTheme : HomeIntent
}

sealed interface HomeEffect {
    data class ShowError(val message: String) : HomeEffect
    data object NavigateToLogin : HomeEffect
}