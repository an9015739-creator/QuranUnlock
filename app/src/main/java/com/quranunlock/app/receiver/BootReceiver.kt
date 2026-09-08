package com.quranunlock.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.quranunlock.app.QuranUnlockApp
import com.quranunlock.app.data.PreferencesManager
import com.quranunlock.app.service.UnlockMonitorService

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(TAG, "Device booted with action: $action")

        val prefs = PreferencesManager.getInstance(context)
        if (prefs.isEnabled) {
            // Start the persistent background service so unlock detection
            // survives reboots as well as being swiped from Recents.
            UnlockMonitorService.start(context)
            Log.d(TAG, "Quran Unlock restored after device reboot.")
        }
    }
}
