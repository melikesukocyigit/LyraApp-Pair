package com.turkcell.lyraapp.data.library

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryPlaylistFavoritesRepository @Inject constructor() : PlaylistFavoritesRepository {

    private val _favoritedPlaylistIds = MutableStateFlow<Set<String>>(emptySet())
    override val favoritedPlaylistIds: StateFlow<Set<String>> = _favoritedPlaylistIds.asStateFlow()

    override fun togglePlaylistFavorite(playlistId: String) {
        val current = _favoritedPlaylistIds.value
        _favoritedPlaylistIds.value = if (current.contains(playlistId)) {
            current - playlistId
        } else {
            current + playlistId
        }
    }

    override fun isPlaylistFavorited(playlistId: String): Boolean =
        _favoritedPlaylistIds.value.contains(playlistId)
}
