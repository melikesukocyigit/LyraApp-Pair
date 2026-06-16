package com.turkcell.lyraapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.turkcell.lyraapp.MainActivity
import com.turkcell.lyraapp.R
import com.turkcell.lyraapp.data.player.NowPlayingTrack
import com.turkcell.lyraapp.data.player.PlayerRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlayerService : Service() {

    @Inject
    lateinit var playerRepository: PlayerRepository

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        mediaSession = MediaSessionCompat(this, "LyraMediaSession").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() { playerRepository.togglePlayPause() }
                override fun onPause() { playerRepository.togglePlayPause() }
                override fun onSkipToNext() { playerRepository.skipNext() }
                override fun onSkipToPrevious() { playerRepository.skipPrevious() }
            })
            isActive = true
        }

        observePlayerState()
    }

    private fun observePlayerState() {
        serviceScope.launch {
            playerRepository.currentTrack.collect { track ->
                updateNotification(track, playerRepository.isPlaying.value)
            }
        }
        serviceScope.launch {
            playerRepository.isPlaying.collect { playing ->
                updateNotification(playerRepository.currentTrack.value, playing)
            }
        }
    }

    private fun updateNotification(track: NowPlayingTrack?, isPlaying: Boolean) {
        track ?: return
        updateMediaSession(track, isPlaying)
        val notification = buildNotification(track, isPlaying)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateMediaSession(track: NowPlayingTrack, isPlaying: Boolean) {
        val artwork = createArtworkBitmap(track.startColor, track.endColor)
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.subtitle)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, artwork)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.durationMs)
                .build()
        )
        val state = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
                .build()
        )
    }

    private fun buildNotification(track: NowPlayingTrack, isPlaying: Boolean): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseTitle = if (isPlaying) "Duraklat" else "Oynat"

        val artwork = createArtworkBitmap(track.startColor, track.endColor)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(track.title)
            .setContentText(track.subtitle)
            .setLargeIcon(artwork)
            .setContentIntent(openAppIntent)
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(android.R.drawable.ic_media_previous, "Onceki", buildActionIntent(ACTION_SKIP_PREVIOUS, 1))
            .addAction(playPauseIcon, playPauseTitle, buildActionIntent(ACTION_PLAY_PAUSE, 2))
            .addAction(android.R.drawable.ic_media_next, "Sonraki", buildActionIntent(ACTION_SKIP_NEXT, 3))
            .setOngoing(isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun buildActionIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, MediaPlayerService::class.java).setAction(action)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, requestCode, intent, flags)
        } else {
            PendingIntent.getService(this, requestCode, intent, flags)
        }
    }

    private fun createArtworkBitmap(startColor: Long, endColor: Long): Bitmap {
        val size = 256
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, size.toFloat(), size.toFloat(),
                startColor.toInt(), endColor.toInt(),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        return bitmap
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medya Oynatıcı",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "LyraApp medya kontrolü"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> playerRepository.togglePlayPause()
            ACTION_SKIP_NEXT -> playerRepository.skipNext()
            ACTION_SKIP_PREVIOUS -> playerRepository.skipPrevious()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        mediaSession.release()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "lyra_media_channel"
        const val ACTION_PLAY_PAUSE = "com.turkcell.lyraapp.ACTION_PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = "com.turkcell.lyraapp.ACTION_SKIP_NEXT"
        const val ACTION_SKIP_PREVIOUS = "com.turkcell.lyraapp.ACTION_SKIP_PREVIOUS"
    }
}
