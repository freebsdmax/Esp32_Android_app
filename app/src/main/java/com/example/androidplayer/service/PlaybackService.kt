package com.example.androidplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.androidplayer.MainActivity
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import com.example.androidplayer.ui.i18n.Loc
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Foreground service that shows a "now playing" media notification
 * (mini player) and forwards the transport buttons to the ESP32 over BLE.
 */
@AndroidEntryPoint
class PlaybackService : Service() {

    @Inject
    lateinit var repository: Esp32PlayerRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var session: MediaSessionCompat

    private var lastTitle: String = ""
    private var lastPaused: Boolean = true

    override fun onCreate() {
        super.onCreate()
        instance = this
        ensureChannel()
        session = MediaSessionCompat(this, "FordPlayer").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() = sendPlayPause(true)
                override fun onPause() = sendPlayPause(false)
                override fun onSkipToNext() = runCommand("next")
                override fun onSkipToPrevious() = runCommand("prev")
            })
            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> sendPlayPause(true)
            ACTION_PAUSE -> sendPlayPause(false)
            ACTION_NEXT -> runCommand("next")
            ACTION_PREV -> runCommand("prev")
        }
        startForegroundCompat(buildNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        instance = null
        runCatching { session.release() }
        scope.coroutineContext.cancel()
        super.onDestroy()
    }

    /** Called from the UI whenever the device reports a new track/state. */
    fun updateNowPlaying(title: String, paused: Boolean, positionMs: Long, durationMs: Long) {
        if (title == lastTitle && paused == lastPaused) return
        lastTitle = title
        lastPaused = paused

        session.setMetadata(
            android.support.v4.media.MediaMetadataCompat.Builder()
                .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST, "Ford Player")
                .putLong(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION, durationMs)
                .build()
        )
        session.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
                .setState(
                    if (paused) PlaybackStateCompat.STATE_PAUSED else PlaybackStateCompat.STATE_PLAYING,
                    positionMs,
                    1f
                )
                .build()
        )

        runCatching {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, buildNotification())
        }
    }

    private fun sendPlayPause(play: Boolean) {
        scope.launch {
            runCatching {
                if (play) repository.resumePlayback() else repository.pausePlayback()
            }.onFailure { Timber.w(it, "Transport command failed") }
        }
    }

    private fun runCommand(cmd: String) {
        scope.launch {
            runCatching {
                when (cmd) {
                    "next" -> repository.nextTrack()
                    "prev" -> repository.previousTrack()
                    else -> Unit
                }
            }.onFailure { Timber.w(it, "Transport command failed") }
        }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    Loc.playbackChannel(),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = Loc.playbackChannelDesc()
                    setShowBadge(false)
                }
            )
        }
    }

    private fun buildNotification(): Notification {
        val openApp = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prev = actionIntent(ACTION_PREV, 31)
        val next = actionIntent(ACTION_NEXT, 32)
        val playPause = actionIntent(if (lastPaused) ACTION_PLAY else ACTION_PAUSE, 33)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(lastTitle.ifBlank { Loc.notifTitleFallback() })
            .setContentText(if (lastPaused) Loc.playbackPaused() else Loc.playbackPlaying())
            .setContentIntent(openApp)
            .setOnlyAlertOnce(true)
            .setOngoing(!lastPaused)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                android.R.drawable.ic_media_previous,
                Loc.playbackPrevious(),
                prev
            )
            .addAction(
                if (lastPaused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause,
                if (lastPaused) Loc.playbackPlay() else Loc.playbackPause(),
                playPause
            )
            .addAction(
                android.R.drawable.ic_media_next,
                Loc.playbackNext(),
                next
            )
            .setStyle(
                MediaStyle()
                    .setMediaSession(session.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .build()
    }

    private fun actionIntent(action: String, requestCode: Int): PendingIntent =
        PendingIntent.getService(
            this,
            requestCode,
            Intent(this, PlaybackService::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        private const val CHANNEL_ID = "playback"
        private const val NOTIFICATION_ID = 4301

        private const val ACTION_PLAY = "com.example.androidplayer.PLAY"
        private const val ACTION_PAUSE = "com.example.androidplayer.PAUSE"
        private const val ACTION_NEXT = "com.example.androidplayer.NEXT"
        private const val ACTION_PREV = "com.example.androidplayer.PREV"

        @Volatile
        private var instance: PlaybackService? = null

        fun start(context: Context) {
            runCatching {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, PlaybackService::class.java)
                )
            }.onFailure { Timber.w(it, "Could not start playback service") }
        }

        fun stop(context: Context) {
            runCatching { context.stopService(Intent(context, PlaybackService::class.java)) }
        }

        fun update(title: String, paused: Boolean, positionMs: Long, durationMs: Long) {
            instance?.updateNowPlaying(title, paused, positionMs, durationMs)
        }
    }
}
