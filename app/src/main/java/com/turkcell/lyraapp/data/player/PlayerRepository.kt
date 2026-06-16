package com.turkcell.lyraapp.data.player


import kotlinx.coroutines.flow.StateFlow

interface PlayerRepository {
    val currentTrack: StateFlow<NowPlayingTrack?>
    val isPlaying: StateFlow<Boolean>
    fun play(track: NowPlayingTrack)
    fun playQueue(tracks: List<NowPlayingTrack>, startIndex: Int)
    fun skipNext()
    fun skipPrevious()
    fun togglePlayPause()
}
