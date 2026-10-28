package com.example.androidplayer.ui.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.androidplayer.MainActivity
import com.example.androidplayer.ui.i18n.Loc

/**
 * Posts application messages (errors, successes, connection changes) to the
 * notification shade, so nothing is missed while the app is in the background.
 */
object AppNotify {

    private const val CHANNEL_MESSAGES = "messages"
    private const val FIRST_ID = 5200
    private const val LAST_ID = 5299

    private var context: Context? = null
    private var nextId = FIRST_ID

    fun init(ctx: Context) {
        context = ctx.applicationContext
        ensureChannel(ctx)
    }

    private fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = ctx.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_MESSAGES) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_MESSAGES,
                    Loc.notifMessagesChannel(),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = Loc.notifMessagesChannelDesc() }
            )
        }
    }

    private fun post(title: String, text: String) {
        val ctx = context ?: return
        if (!NotificationManagerCompat.from(ctx).areNotificationsEnabled()) return

        val openApp = PendingIntent.getActivity(
            ctx, 0,
            Intent(ctx, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(ctx, CHANNEL_MESSAGES)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        runCatching {
            NotificationManagerCompat.from(ctx).notify(nextId, notification)
        }
        nextId = if (nextId >= LAST_ID) FIRST_ID else nextId + 1
    }

    fun error(text: String) = post(Loc.notifErrorTitle(), text)

    fun info(text: String) = post(Loc.notifInfoTitle(), text)
}
