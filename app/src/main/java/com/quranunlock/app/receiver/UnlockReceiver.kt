package com.quranunlock.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.quranunlock.app.audio.QuranAudioService
import com.quranunlock.app.data.PreferencesManager

class UnlockReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "UnlockReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(TAG, "Received broadcast action: $action")

        val prefs = PreferencesManager.getInstance(context)
        if (!prefs.isEnabled) {
            Log.d(TAG, "Quran Unlock is disabled. Skipping audio trigger.")
            return
        }

        if (action == Intent.ACTION_USER_PRESENT || action == Intent.ACTION_SCREEN_ON) {
            val serviceIntent = Intent(context, QuranAudioService::class.java).apply {
                this.action = QuranAudioService.ACTION_TRIGGER_UNLOCK
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting QuranAudioService: ${e.message}")
            }
        }
    }
}
