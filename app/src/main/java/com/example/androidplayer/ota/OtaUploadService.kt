package com.example.androidplayer.ota

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.example.androidplayer.ui.i18n.Loc

/**
 * Keeps the process in the foreground while a firmware update is running.
 *
 * A BLE upload of a ~1.2 MB image takes several minutes; without a foreground
 * service Android is free to freeze the process when the app goes to the
 * background or the screen is locked, which aborts the transfer. The service
 * also hosts the progress notification (progress is updated by the view model
 * through [OtaNotifications.updateProgress] with the same notification id).
 */
class OtaUploadService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = OtaNotifications.buildProgress(this, 0, Loc.otaPreparing())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                OtaNotifications.PROGRESS_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(OtaNotifications.PROGRESS_ID, notification)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        OtaNotifications.cancelProgress(this)
        super.onDestroy()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OtaUploadService::class.java)
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }.onFailure {
                android.util.Log.w("OtaUploadService", "could not start foreground service", it)
            }
        }

        fun stop(context: Context) {
            runCatching { context.stopService(Intent(context, OtaUploadService::class.java)) }
            OtaNotifications.cancelProgress(context)
        }
    }
}
