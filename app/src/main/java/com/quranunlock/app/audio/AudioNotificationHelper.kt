package com.quranunlock.app.audio

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.quranunlock.app.QuranUnlockApp
import com.quranunlock.app.R
import com.quranunlock.app.ui.MainActivity

object AudioNotificationHelper {

    const val NOTIFICATION_ID = 786

    fun buildNotification(
        context: Context,
        surahName: String,
        ayahNumber: Int,
        isPlayingArabic: Boolean,
        isPlayingUrdu: Boolean
    ): Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val stopServiceIntent = Intent(context, QuranAudioService::class.java).apply {
            action = QuranAudioService.ACTION_STOP_AUDIO
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            1,
            stopServiceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val statusText = when {
            isPlayingArabic -> "Reciting Arabic Ayah..."
            isPlayingUrdu -> "Reciting Urdu Translation..."
            else -> "Quran Unlock Active"
        }

        return NotificationCompat.Builder(context, QuranUnlockApp.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_quran_notification)
            .setContentTitle("Surah $surahName — Ayah $ayahNumber")
            .setContentText(statusText)
            .setSubText("Quran Unlock")
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_stop,
                "Stop",
                stopPendingIntent
            )
            .build()
    }
}
