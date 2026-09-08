package com.quranunlock.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.quranunlock.app.data.PreferencesManager
import com.quranunlock.app.service.UnlockMonitorService

class QuranUnlockApp : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "quran_unlock_audio_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Quran Audio Playback"
        lateinit var instance: QuranUnlockApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        createNotificationChannels()

        val prefs = PreferencesManager.getInstance(this)
        if (prefs.isEnabled) {
            // Start the persistent background service instead of registering the
            // receiver directly here — a plain Application-scoped receiver dies
            // with the process when the app is swiped from Recents, but a
            // foreground service (with stopWithTask="false") survives.
            UnlockMonitorService.start(this)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows currently playing Quran verse and media controls"
                setShowBadge(false)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
