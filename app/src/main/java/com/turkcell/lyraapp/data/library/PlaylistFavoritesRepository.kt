package com.turkcell.lyraapp.data.library

import kotlinx.coroutines.flow.StateFlow

interface PlaylistFavoritesRepository {
    val favoritedPlaylistIds: StateFlow<Set<String>>
    fun togglePlaylistFavorite(playlistId: String)
    fun isPlaylistFavorited(playlistId: String): Boolean
}
