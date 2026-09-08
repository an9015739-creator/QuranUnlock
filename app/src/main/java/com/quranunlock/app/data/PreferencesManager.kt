package com.quranunlock.app.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "quran_unlock_prefs"

        private const val KEY_IS_ENABLED = "is_enabled"
        private const val KEY_CURRENT_SURAH = "current_surah"
        private const val KEY_CURRENT_AYAH = "current_ayah"
        private const val KEY_TOTAL_COMPLETED = "total_completed"
        private const val KEY_ARABIC_ENABLED = "arabic_audio_enabled"
        private const val KEY_URDU_ENABLED = "urdu_audio_enabled"
        private const val KEY_PAUSE_BETWEEN_MS = "pause_between_ms"
        private const val KEY_LAST_PLAYED_TIME = "last_played_time"
        private const val KEY_RECITER_FOLDER = "reciter_folder"
        private const val KEY_URDU_FOLDER = "urdu_folder"
        private const val KEY_AUTOPLAY_AFTER_BOOT = "autoplay_after_boot"

        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    var isEnabled: Boolean
        get() = prefs.getBoolean(KEY_IS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_ENABLED, value).apply()

    var currentSurah: Int
        get() = prefs.getInt(KEY_CURRENT_SURAH, 1)
        set(value) = prefs.edit().putInt(KEY_CURRENT_SURAH, value).apply()

    var currentAyah: Int
        get() = prefs.getInt(KEY_CURRENT_AYAH, 1)
        set(value) = prefs.edit().putInt(KEY_CURRENT_AYAH, value).apply()

    var totalCompleted: Int
        get() = prefs.getInt(KEY_TOTAL_COMPLETED, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_COMPLETED, value).apply()

    var arabicAudioEnabled: Boolean
        get() = prefs.getBoolean(KEY_ARABIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ARABIC_ENABLED, value).apply()

    var urduAudioEnabled: Boolean
        get() = prefs.getBoolean(KEY_URDU_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_URDU_ENABLED, value).apply()

    var pauseBetweenMs: Long
        get() = prefs.getLong(KEY_PAUSE_BETWEEN_MS, 1500L)
        set(value) = prefs.edit().putLong(KEY_PAUSE_BETWEEN_MS, value).apply()

    var lastPlayedTime: Long
        get() = prefs.getLong(KEY_LAST_PLAYED_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_PLAYED_TIME, value).apply()

    var reciterFolder: String
        get() = prefs.getString(KEY_RECITER_FOLDER, "Alafasy_128kbps") ?: "Alafasy_128kbps"
        set(value) = prefs.edit().putString(KEY_RECITER_FOLDER, value).apply()

    var urduFolder: String
        get() = prefs.getString(KEY_URDU_FOLDER, "translations/urdu_shamshad_ali_khan_46kbps")
            ?: "translations/urdu_shamshad_ali_khan_46kbps"
        set(value) = prefs.edit().putString(KEY_URDU_FOLDER, value).apply()

    fun advanceToNextVerse(nextSurah: Int, nextAyah: Int) {
        prefs.edit()
            .putInt(KEY_CURRENT_SURAH, nextSurah)
            .putInt(KEY_CURRENT_AYAH, nextAyah)
            .putInt(KEY_TOTAL_COMPLETED, totalCompleted + 1)
            .putLong(KEY_LAST_PLAYED_TIME, System.currentTimeMillis())
            .apply()
    }

    fun resetProgress() {
        prefs.edit()
            .putInt(KEY_CURRENT_SURAH, 1)
            .putInt(KEY_CURRENT_AYAH, 1)
            .putInt(KEY_TOTAL_COMPLETED, 0)
            .putLong(KEY_LAST_PLAYED_TIME, 0L)
            .apply()
    }

    fun jumpToVerse(surah: Int, ayah: Int) {
        prefs.edit()
            .putInt(KEY_CURRENT_SURAH, surah)
            .putInt(KEY_CURRENT_AYAH, ayah)
            .apply()
    }
}
