package com.quranunlock.app.audio

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.quranunlock.app.data.PreferencesManager
import com.quranunlock.app.data.QuranRepository
import kotlinx.coroutines.*

class QuranAudioService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    private lateinit var prefs: PreferencesManager

    companion object {
        private const val TAG = "QuranAudioService"
        const val ACTION_TRIGGER_UNLOCK = "com.quranunlock.app.ACTION_TRIGGER_UNLOCK"
        const val ACTION_PLAY_TEST = "com.quranunlock.app.ACTION_PLAY_TEST"
        const val ACTION_STOP_AUDIO = "com.quranunlock.app.ACTION_STOP_AUDIO"

        const val BROADCAST_PLAYBACK_STATE = "com.quranunlock.app.PLAYBACK_STATE"
        const val EXTRA_IS_PLAYING = "is_playing"
        const val EXTRA_STATUS_MESSAGE = "status_message"

        @Volatile
        var isCurrentlyPlaying: Boolean = false
            private set

        private var lastTriggerTimestamp: Long = 0L
        private const val DEBOUNCE_INTERVAL_MS = 10_000L // 10 seconds anti-duplicate lock
    }

    override fun onCreate() {
        super.onCreate()
        prefs = PreferencesManager.getInstance(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "QuranUnlock::AudioPlaybackWakeLock"
        ).apply {
            setReferenceCounted(false)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_TRIGGER_UNLOCK -> handleUnlockTrigger()
            ACTION_PLAY_TEST -> handlePlayTest()
            ACTION_STOP_AUDIO -> stopPlaybackAndFinish()
        }

        return START_NOT_STICKY
    }

    private fun handleUnlockTrigger() {
        if (!prefs.isEnabled) {
            Log.d(TAG, "Quran Unlock is disabled by user. Ignoring unlock event.")
            return
        }

        val currentTime = System.currentTimeMillis()
        if (isCurrentlyPlaying) {
            Log.d(TAG, "Audio is already playing. Skipping duplicate unlock event.")
            return
        }

        if (currentTime - lastTriggerTimestamp < DEBOUNCE_INTERVAL_MS) {
            Log.d(TAG, "Duplicate unlock event throttled (within 10s debounce).")
            return
        }

        lastTriggerTimestamp = currentTime
        startSequencePlayback()
    }

    private fun handlePlayTest() {
        if (isCurrentlyPlaying) {
            stopCurrentAudio()
        }
        startSequencePlayback()
    }

    private fun startSequencePlayback() {
        val surahNum = prefs.currentSurah
        val ayahNum = prefs.currentAyah

        val verse = QuranRepository.getVerse(
            surahNumber = surahNum,
            ayahNumber = ayahNum,
            reciterFolder = prefs.reciterFolder,
            urduFolder = prefs.urduFolder
        )

        // Promote to Foreground Service
        val initialNotification = AudioNotificationHelper.buildNotification(
            context = this,
            surahName = verse.surahName,
            ayahNumber = verse.ayahNumber,
            isPlayingArabic = true,
            isPlayingUrdu = false
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    AudioNotificationHelper.NOTIFICATION_ID,
                    initialNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(AudioNotificationHelper.NOTIFICATION_ID, initialNotification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Foreground service start failed: ${e.message}")
        }

        wakeLock?.acquire(3 * 60 * 1000L /* 3 minutes timeout */)
        isCurrentlyPlaying = true
        broadcastState(true, "Playing Surah ${verse.surahName} Ayah ${verse.ayahNumber}")

        if (!requestAudioFocus()) {
            Log.w(TAG, "Could not acquire audio focus. Playing anyway or aborting.")
        }

        serviceScope.launch {
            try {
                // 1. Play Arabic Audio if enabled
                if (prefs.arabicAudioEnabled) {
                    updateNotification(verse.surahName, verse.ayahNumber, isArabic = true, isUrdu = false)
                    playSingleAudioFile(verse.arabicAudioUrl)
                    delay(prefs.pauseBetweenMs)
                }

                // 2. Play Urdu Audio if enabled
                if (prefs.urduAudioEnabled) {
                    updateNotification(verse.surahName, verse.ayahNumber, isArabic = false, isUrdu = true)
                    playSingleAudioFile(verse.urduAudioUrl)
                }

                // 3. Sequential Progress: advance to next Ayah for subsequent unlock
                val (nextSurah, nextAyah) = QuranRepository.getNextVerseCoordinates(surahNum, ayahNum)
                prefs.advanceToNextVerse(nextSurah, nextAyah)
                Log.d(TAG, "Advanced sequence to Surah $nextSurah, Ayah $nextAyah")

            } catch (e: Exception) {
                Log.e(TAG, "Playback sequence error: ${e.message}")
            } finally {
                stopPlaybackAndFinish()
            }
        }
    }

    private suspend fun playSingleAudioFile(url: String) = suspendCancellableCoroutine<Unit> { continuation ->
        stopCurrentAudio()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

            setOnPreparedListener { mp ->
                mp.start()
            }

            setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
                if (continuation.isActive) {
                    continuation.resume(Unit) {}
                }
            }

            setOnErrorListener { mp, what, extra ->
                Log.e(TAG, "MediaPlayer error ($what, $extra) on URL: $url")
                mp.release()
                mediaPlayer = null
                if (continuation.isActive) {
                    continuation.resume(Unit) {}
                }
                true
            }

            try {
                setDataSource(url)
                prepareAsync()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to setDataSource: ${e.message}")
                if (continuation.isActive) {
                    continuation.resume(Unit) {}
                }
            }
        }

        continuation.invokeOnCancellation {
            stopCurrentAudio()
        }
    }

    private fun updateNotification(surahName: String, ayahNum: Int, isArabic: Boolean, isUrdu: Boolean) {
        val notification = AudioNotificationHelper.buildNotification(
            context = this,
            surahName = surahName,
            ayahNumber = ayahNum,
            isPlayingArabic = isArabic,
            isPlayingUrdu = isUrdu
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(AudioNotificationHelper.NOTIFICATION_ID, notification)
    }

    private fun requestAudioFocus(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                .setOnAudioFocusChangeListener { focusChange ->
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        stopPlaybackAndFinish()
                    }
                }
                .build()
            return audioManager?.requestAudioFocus(audioFocusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            return audioManager?.requestAudioFocus(
                { focusChange ->
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) stopPlaybackAndFinish()
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager?.abandonAudioFocus(null)
        }
    }

    private fun stopCurrentAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio: ${e.message}")
        } finally {
            mediaPlayer = null
        }
    }

    private fun stopPlaybackAndFinish() {
        stopCurrentAudio()
        abandonAudioFocus()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        isCurrentlyPlaying = false
        broadcastState(false, "Idle")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun broadcastState(isPlaying: Boolean, message: String) {
        val intent = Intent(BROADCAST_PLAYBACK_STATE).apply {
            putExtra(EXTRA_IS_PLAYING, isPlaying)
            putExtra(EXTRA_STATUS_MESSAGE, message)
            setPackage(packageName)
        }
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        stopPlaybackAndFinish()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
