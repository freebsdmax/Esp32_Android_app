package com.example.androidplayer.ota

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.androidplayer.MainActivity
import com.example.androidplayer.ui.i18n.Loc

/**
 * Notifications shown while a firmware update runs and after it finishes.
 *
 * They are useful when the user leaves the app (or locks the screen) during a
 * multi-minute BLE upload: progress stays visible in the notification shade and
 * the final result - including the exact failure reason - is not lost.
 */
object OtaNotifications {

    private const val CHANNEL_PROGRESS = "ota_progress"
    private const val CHANNEL_RESULT = "ota_result"

    /** Id of the ongoing progress notification (owned by [OtaUploadService]). */
    const val PROGRESS_ID = 4201
    const val RESULT_ID = 4202

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_PROGRESS) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_PROGRESS,
                    Loc.otaChannelProgress(),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = Loc.otaChannelProgressDesc()
                    setShowBadge(false)
                }
            )
        }
        if (manager.getNotificationChannel(CHANNEL_RESULT) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_RESULT,
                    Loc.otaChannelResult(),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = Loc.otaChannelResultDesc()
                }
            )
        }
    }

    fun canPost(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun openAppIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun buildProgress(context: Context, percent: Int, text: String): Notification {
        ensureChannels(context)
        val value = percent.coerceIn(0, 100)
        return NotificationCompat.Builder(context, CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(Loc.otaNotificationTitle())
            .setContentText(text)
            .setContentIntent(openAppIntent(context))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setProgress(100, value, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun updateProgress(context: Context, percent: Int, text: String) {
        if (!canPost(context)) return
        runCatching {
            NotificationManagerCompat.from(context)
                .notify(PROGRESS_ID, buildProgress(context, percent, text))
        }.onFailure { android.util.Log.w("OtaNotifications", "progress notify failed", it) }
    }

    fun showResult(context: Context, success: Boolean, text: String) {
        if (!canPost(context)) return
        ensureChannels(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_RESULT)
            .setSmallIcon(
                if (success) android.R.drawable.stat_sys_download_done
                else android.R.drawable.stat_notify_error
            )
            .setContentTitle(
                if (success) Loc.otaDoneTitle() else Loc.otaFailedTitle()
            )
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openAppIntent(context))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(RESULT_ID, notification)
        }.onFailure { android.util.Log.w("OtaNotifications", "result notify failed", it) }
    }

    fun cancelProgress(context: Context) {
        runCatching { NotificationManagerCompat.from(context).cancel(PROGRESS_ID) }
    }
}
