package com.quranunlock.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.quranunlock.app.R
import com.quranunlock.app.receiver.UnlockReceiver
import com.quranunlock.app.ui.MainActivity

/**
 * Keeps running in the background (even after the app is swiped away from Recents)
 * so the phone-unlock listener (UnlockReceiver) stays registered and Quran Unlock
 * keeps working at all times. Shows a low-priority, silent, ongoing notification
 * because Android requires a visible notification for any foreground service.
 */
class UnlockMonitorService : Service() {

    private var unlockReceiver: UnlockReceiver? = null

    companion object {
        const val CHANNEL_ID = "quran_unlock_monitor_channel"
        const val NOTIFICATION_ID = 787

        const val ACTION_START = "com.quranunlock.app.ACTION_START_MONITOR"
        const val ACTION_STOP = "com.quranunlock.app.ACTION_STOP_MONITOR"

        fun start(context: Context) {
            val intent = Intent(context, UnlockMonitorService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, UnlockMonitorService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                unregisterUnlockReceiver()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_quran_notification)
                    .setContentTitle("Quran Unlock is active")
                    .setContentText("Listening for phone unlock in the background")
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setOngoing(true)
                    .setContentIntent(openAppPendingIntent())
                    .build()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }

                registerUnlockReceiver()
            }
        }
        // START_STICKY: if Android kills the process due to low memory, it will try to restart it.
        return START_STICKY
    }

    private fun registerUnlockReceiver() {
        if (unlockReceiver == null) {
            unlockReceiver = UnlockReceiver()
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_USER_PRESENT)
                addAction(Intent.ACTION_SCREEN_ON)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(unlockReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                registerReceiver(unlockReceiver, filter)
            }
        }
    }

    private fun unregisterUnlockReceiver() {
        unlockReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (_: Exception) {}
        }
        unlockReceiver = null
    }

    private fun openAppPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quran Unlock Background Listener",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Keeps Quran Unlock listening for phone unlock events"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    // Do NOT override onTaskRemoved to stop the service — leaving it unimplemented
    // (default no-op) is what allows the service to keep running after the app
    // is swiped away from Recents.

    override fun onDestroy() {
        unregisterUnlockReceiver()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
