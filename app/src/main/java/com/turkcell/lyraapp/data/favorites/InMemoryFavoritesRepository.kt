package com.turkcell.lyraapp.data.favorites

import com.turkcell.lyraapp.data.local.FavoritedSongDao
import com.turkcell.lyraapp.data.local.FavoritedSongEntity
import com.turkcell.lyraapp.data.player.NowPlayingTrack
import com.turkcell.lyraapp.data.network.LyraApiService
import com.turkcell.lyraapp.data.network.CreatePlaylistRequest
import com.turkcell.lyraapp.data.network.AddTrackRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryFavoritesRepository @Inject constructor(
    private val favoritedSongDao: FavoritedSongDao,
    private val apiService: LyraApiService
) : FavoritesRepository {

    private val _favorites = MutableStateFlow<List<NowPlayingTrack>>(emptyList())
    override val favorites: StateFlow<List<NowPlayingTrack>> = _favorites.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var favoritesPlaylistId: String? = null

    init {
        // Observe Room DB for reactive updates in UI
        scope.launch {
            favoritedSongDao.observeAll().collect { entities ->
                val tracks = entities.map { it.toNowPlayingTrack() }
                _favorites.value = tracks
            }
        }

        // Trigger synchronization with server
        scope.launch {
            try {
                syncWithServer()
            } catch (e: Exception) {
                android.util.Log.e("FavoritesRepository", "Initial sync failed", e)
            }
        }
    }

    override fun toggleFavorite(track: NowPlayingTrack) {
        scope.launch {
            val current = _favorites.value
            val isAlreadyFavorite = current.any { it.id == track.id }
            
            if (isAlreadyFavorite) {
                // Delete locally first
                favoritedSongDao.deleteById(track.id)
                // Remove remotely in background
                removeRemoteFavorite(track.id)
            } else {
                // Insert locally first (isSynced = false)
                favoritedSongDao.insert(track.toEntity(synced = false))
                // Add remotely in background
                addRemoteFavorite(track)
            }
        }
    }

    override fun isFavorite(trackId: String): Boolean =
        _favorites.value.any { it.id == trackId }

    private suspend fun addRemoteFavorite(track: NowPlayingTrack) {
        try {
            val playlistId = getOrCreateFavoritesPlaylistId() ?: return
            apiService.addTrackToPlaylist(playlistId, AddTrackRequest(songId = track.id))
            favoritedSongDao.updateSyncStatus(track.id, true)
        } catch (e: Exception) {
            android.util.Log.e("FavoritesRepository", "Failed to add remote favorite for ${track.id}", e)
        }
    }

    private suspend fun removeRemoteFavorite(trackId: String) {
        try {
            val playlistId = getOrCreateFavoritesPlaylistId() ?: return
            apiService.removeTrackFromPlaylist(playlistId, trackId)
        } catch (e: Exception) {
            android.util.Log.e("FavoritesRepository", "Failed to remove remote favorite for $trackId", e)
        }
    }

    private suspend fun getOrCreateFavoritesPlaylistId(): String? {
        favoritesPlaylistId?.let { return it }
        
        try {
            val playlistsResponse = apiService.getUserPlaylists()
            val existing = playlistsResponse.data.find { it.name == "Beğenilen Şarkılar" }
            if (existing != null) {
                favoritesPlaylistId = existing.id
                return existing.id
            }
            
            // Create "Beğenilen Şarkılar" playlist on the server
            val createResponse = apiService.createPlaylist(
                CreatePlaylistRequest(
                    name = "Beğenilen Şarkılar",
                    description = "Beğendiğiniz tüm şarkılar"
                )
            )
            favoritesPlaylistId = createResponse.data.id
            return createResponse.data.id
        } catch (e: Exception) {
            android.util.Log.e("FavoritesRepository", "Failed to get or create favorites playlist", e)
            return null
        }
    }

    private suspend fun syncWithServer() = withContext(Dispatchers.IO) {
        val playlistId = getOrCreateFavoritesPlaylistId() ?: return@withContext
        
        // 1. Fetch remote tracks
        val remoteDetail = apiService.getPlaylistDetail(playlistId)
        val remoteTracks = remoteDetail.data.songs
        val remoteTrackIds = remoteTracks.map { it.id }.toSet()
        
        // 2. Fetch local tracks
        val localEntities = favoritedSongDao.getAll()
        val localTrackIds = localEntities.map { it.id }.toSet()
        
        // 3. Process local unsynced tracks (add to remote)
        val unsyncedLocal = localEntities.filter { !it.isSynced }
        unsyncedLocal.forEach { localSong ->
            if (!remoteTrackIds.contains(localSong.id)) {
                try {
                    apiService.addTrackToPlaylist(playlistId, AddTrackRequest(songId = localSong.id))
                } catch (ignored: Exception) {}
            }
            favoritedSongDao.updateSyncStatus(localSong.id, true)
        }
        
        // Refresh local track IDs and entities list after unsynced changes
        val updatedLocalEntities = favoritedSongDao.getAll()
        val updatedLocalTrackIds = updatedLocalEntities.map { it.id }.toSet()
        
        // 4. Process remote tracks that are missing locally (insert to local Room)
        remoteTracks.forEach { remoteSong ->
            if (!updatedLocalTrackIds.contains(remoteSong.id)) {
                val track = NowPlayingTrack(
                    id = remoteSong.id,
                    title = remoteSong.title,
                    subtitle = remoteSong.artist,
                    startColor = NowPlayingTrack.getColorsForId(remoteSong.id).first,
                    endColor = NowPlayingTrack.getColorsForId(remoteSong.id).second,
                    durationMs = remoteSong.durationMs
                )
                favoritedSongDao.insert(track.toEntity(synced = true))
            }
        }
        
        // 5. Process local synced tracks that are missing remotely (deleted on another device -> delete locally)
        updatedLocalEntities.forEach { localSong ->
            if (localSong.isSynced && !remoteTrackIds.contains(localSong.id)) {
                favoritedSongDao.deleteById(localSong.id)
            }
        }
    }
}

private fun FavoritedSongEntity.toNowPlayingTrack() = NowPlayingTrack(
    id = id,
    title = title,
    subtitle = subtitle,
    startColor = startColor,
    endColor = endColor,
    durationMs = durationMs
)

private fun NowPlayingTrack.toEntity(synced: Boolean) = FavoritedSongEntity(
    id = id,
    title = title,
    subtitle = subtitle,
    startColor = startColor,
    endColor = endColor,
    durationMs = durationMs,
    isSynced = synced
)
