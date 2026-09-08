package com.quranunlock.app.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.quranunlock.app.R
import com.quranunlock.app.audio.QuranAudioService
import com.quranunlock.app.data.PreferencesManager
import com.quranunlock.app.data.QuranRepository
import com.quranunlock.app.service.UnlockMonitorService
import com.quranunlock.app.util.AutoStartHelper

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: PreferencesManager

    // Views - Navigation Containers
    private lateinit var viewHome: LinearLayout
    private lateinit var viewProgress: LinearLayout
    private lateinit var viewSettings: LinearLayout
    private lateinit var viewAbout: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    // Home Views
    private lateinit var switchUnlockEnabled: SwitchCompat
    private lateinit var tvPlaybackStatus: TextView
    private lateinit var tvCurrentAyahBadge: TextView
    private lateinit var tvSurahArabic: TextView
    private lateinit var tvSurahEnglishUrdu: TextView
    private lateinit var tvProgressAyahCount: TextView
    private lateinit var tvProgressPercentage: TextView
    private lateinit var pbQuranProgress: ProgressBar
    private lateinit var tvNextVersePreview: TextView
    private lateinit var btnTestAudio: MaterialButton
    private lateinit var btnStopAudio: MaterialButton

    // Progress Views
    private lateinit var etJumpSurah: EditText
    private lateinit var etJumpAyah: EditText
    private lateinit var btnJumpApply: MaterialButton
    private lateinit var btnResetProgress: MaterialButton

    // Settings Views
    private lateinit var switchArabicAudio: SwitchCompat
    private lateinit var switchUrduAudio: SwitchCompat
    private lateinit var btnBatteryOptimization: MaterialButton

    private val playbackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isPlaying = intent?.getBooleanExtra(QuranAudioService.EXTRA_IS_PLAYING, false) ?: false
            val status = intent?.getStringExtra(QuranAudioService.EXTRA_STATUS_MESSAGE) ?: "Ready"
            updatePlaybackStatus(isPlaying, status)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PreferencesManager.getInstance(this)

        initViews()
        setupListeners()
        requestNotificationPermission()
        refreshUi()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(QuranAudioService.BROADCAST_PLAYBACK_STATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(playbackReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(playbackReceiver, filter)
        }
        refreshUi()
        updateBatteryButtonState()
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(playbackReceiver)
        } catch (_: Exception) {}
    }

    private fun initViews() {
        viewHome = findViewById(R.id.view_home)
        viewProgress = findViewById(R.id.view_progress)
        viewSettings = findViewById(R.id.view_settings)
        viewAbout = findViewById(R.id.view_about)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        switchUnlockEnabled = findViewById(R.id.switch_unlock_enabled)
        tvPlaybackStatus = findViewById(R.id.tv_playback_status)
        tvCurrentAyahBadge = findViewById(R.id.tv_current_ayah_badge)
        tvSurahArabic = findViewById(R.id.tv_surah_arabic)
        tvSurahEnglishUrdu = findViewById(R.id.tv_surah_english_urdu)
        tvProgressAyahCount = findViewById(R.id.tv_progress_ayah_count)
        tvProgressPercentage = findViewById(R.id.tv_progress_percentage)
        pbQuranProgress = findViewById(R.id.pb_quran_progress)
        tvNextVersePreview = findViewById(R.id.tv_next_verse_preview)
        btnTestAudio = findViewById(R.id.btn_test_audio)
        btnStopAudio = findViewById(R.id.btn_stop_audio)

        etJumpSurah = findViewById(R.id.et_jump_surah)
        etJumpAyah = findViewById(R.id.et_jump_ayah)
        btnJumpApply = findViewById(R.id.btn_jump_apply)
        btnResetProgress = findViewById(R.id.btn_reset_progress)

        switchArabicAudio = findViewById(R.id.switch_arabic_audio)
        switchUrduAudio = findViewById(R.id.switch_urdu_audio)
        btnBatteryOptimization = findViewById(R.id.btn_battery_optimization)
    }

    private fun setupListeners() {
        // Bottom Navigation Tab Switching
        bottomNavigation.setOnItemSelectedListener { item ->
            viewHome.visibility = View.GONE
            viewProgress.visibility = View.GONE
            viewSettings.visibility = View.GONE
            viewAbout.visibility = View.GONE

            when (item.itemId) {
                R.id.nav_home -> viewHome.visibility = View.VISIBLE
                R.id.nav_progress -> viewProgress.visibility = View.VISIBLE
                R.id.nav_settings -> viewSettings.visibility = View.VISIBLE
                R.id.nav_about -> viewAbout.visibility = View.VISIBLE
            }
            true
        }

        // Master Unlock Switch
        switchUnlockEnabled.setOnCheckedChangeListener { _, isChecked ->
            prefs.isEnabled = isChecked
            if (isChecked) {
                UnlockMonitorService.start(this)
            } else {
                UnlockMonitorService.stop(this)
            }
            Toast.makeText(
                this,
                if (isChecked) "Quran Unlock Enabled" else "Quran Unlock Paused",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Audio Settings Switches
        switchArabicAudio.setOnCheckedChangeListener { _, isChecked ->
            prefs.arabicAudioEnabled = isChecked
        }
        switchUrduAudio.setOnCheckedChangeListener { _, isChecked ->
            prefs.urduAudioEnabled = isChecked
        }

        // Test Audio Button (Allows testing without locking phone)
        btnTestAudio.setOnClickListener {
            val serviceIntent = Intent(this, QuranAudioService::class.java).apply {
                action = QuranAudioService.ACTION_PLAY_TEST
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            Toast.makeText(this, "Testing Quran Audio...", Toast.LENGTH_SHORT).show()
        }

        // Stop Audio Button
        btnStopAudio.setOnClickListener {
            val serviceIntent = Intent(this, QuranAudioService::class.java).apply {
                action = QuranAudioService.ACTION_STOP_AUDIO
            }
            startService(serviceIntent)
        }

        // Jump to Surah / Ayah
        btnJumpApply.setOnClickListener {
            val sStr = etJumpSurah.text.toString().trim()
            val aStr = etJumpAyah.text.toString().trim()
            val sNum = sStr.toIntOrNull() ?: 1
            val aNum = aStr.toIntOrNull() ?: 1

            if (sNum in 1..114) {
                val surah = QuranRepository.getSurah(sNum)
                val validAyah = aNum.coerceIn(1, surah.ayahCount)
                prefs.jumpToVerse(sNum, validAyah)
                refreshUi()
                Toast.makeText(this, "Jumped to ${surah.name}, Ayah $validAyah", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter Surah between 1 and 114", Toast.LENGTH_SHORT).show()
            }
        }

        // Reset Progress Dialog
        btnResetProgress.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_reset_title)
                .setMessage(R.string.dialog_reset_message)
                .setPositiveButton(R.string.dialog_confirm) { _, _ ->
                    prefs.resetProgress()
                    refreshUi()
                    Toast.makeText(this, "Progress reset to Surah Al-Fatihah", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.dialog_cancel, null)
                .show()
        }

        // Battery Optimization Settings
        btnBatteryOptimization.setOnClickListener {
            openBatteryOptimizationSettings()
        }
        updateBatteryButtonState()
    }

    private fun updateBatteryButtonState() {
        val pm = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val isIgnoring = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pm.isIgnoringBatteryOptimizations(packageName)
        } else {
            true
        }
        btnBatteryOptimization.text = if (isIgnoring) {
            getString(R.string.btn_battery_settings_enabled)
        } else {
            getString(R.string.btn_battery_settings)
        }

    private fun refreshUi() {
        val currentSurahNum = prefs.currentSurah
        val currentAyahNum = prefs.currentAyah
        val surah = QuranRepository.getSurah(currentSurahNum)

        switchUnlockEnabled.isChecked = prefs.isEnabled
        switchArabicAudio.isChecked = prefs.arabicAudioEnabled
        switchUrduAudio.isChecked = prefs.urduAudioEnabled

        tvCurrentAyahBadge.text = "Ayah $currentAyahNum"
        tvSurahArabic.text = surah.arabicName
        tvSurahEnglishUrdu.text = "Surah ${surah.name} — ${surah.urduName}"

        val globalAyahIndex = surah.startAyahGlobal + currentAyahNum - 1
        val percent = (globalAyahIndex.toDouble() / QuranRepository.TOTAL_QURAN_VERSES) * 100.0

        tvProgressAyahCount.text = "$globalAyahIndex / ${QuranRepository.TOTAL_QURAN_VERSES} Ayahs"
        tvProgressPercentage.text = String.format("%.2f%%", percent)
        pbQuranProgress.progress = globalAyahIndex

        val (nextSurahNum, nextAyahNum) = QuranRepository.getNextVerseCoordinates(currentSurahNum, currentAyahNum)
        val nextSurah = QuranRepository.getSurah(nextSurahNum)
        tvNextVersePreview.text = "Next Unlock: Surah ${nextSurah.name}, Ayah $nextAyahNum"

        updatePlaybackStatus(QuranAudioService.isCurrentlyPlaying, if (QuranAudioService.isCurrentlyPlaying) "Playing" else "Ready")
    }

    private fun updatePlaybackStatus(isPlaying: Boolean, status: String) {
        tvPlaybackStatus.text = status
        if (isPlaying) {
            btnStopAudio.visibility = View.VISIBLE
            tvPlaybackStatus.setTextColor(ContextCompat.getColor(this, R.color.gold_accent))
        } else {
            btnStopAudio.visibility = View.GONE
            tvPlaybackStatus.setTextColor(ContextCompat.getColor(this, R.color.emerald_primary))
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun openBatteryOptimizationSettings() {
        // Step 1: standard Android "ignore battery optimizations" request.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (_: Exception) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        }

        // Step 2: also guide the user to the phone brand's own Autostart /
        // Background-allow screen (Xiaomi, Vivo, Oppo, Huawei, etc.), since
        // stock Android's battery setting alone isn't enough on these phones.
        AlertDialog.Builder(this)
            .setTitle("One More Step")
            .setMessage(
                "For your phone, please also open \"${AutoStartHelper.autoStartSettingName()}\" " +
                "and allow Quran Unlock to run in the background / autostart. " +
                "Tap \"Open Setting\" below."
            )
            .setPositiveButton("Open Setting") { _, _ ->
                AutoStartHelper.openAutoStartSettings(this)
            }
            .setNegativeButton("Later", null)
            .show()
    }
}
