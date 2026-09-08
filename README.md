# Quran Unlock — Native Android Application

**Quran Unlock** (`com.quranunlock.app`) is a 100% native Android application built with Kotlin, Android Jetpack, Media3, and Android Foreground Services. 

Whenever the user unlocks their smartphone, Quran Unlock automatically plays the **Arabic recitation** of the current verse, pauses briefly, plays the **Urdu translation audio**, and advances to the subsequent verse in strict chronological Quranic order (from Surah 1:1 through Surah 114:6).

---

## 📱 App Highlights & Architecture

- **100% Native Kotlin & Android SDK**: Not a website, no WebView wrapper.
- **Sequential Quran Engine**: Exactly tracks Surah 1:1 to 114:6 (6,236 Ayahs total). Never randomizes.
- **Dual-Audio Playback**: 
  - **Arabic Recitation**: Mishary Rashid Alafasy.
  - **Urdu Translation**: Shamshad Ali Khan (Fateh Muhammad Jalandhari translation).
- **Persistent Storage**: Saves progress to `SharedPreferences` immediately upon verse progression. Survives app termination and device reboot.
- **Smart Debounce Lock**: 10-second debounce prevents duplicate audio triggers when user rapidly unlocks the phone.
- **Audio Focus & Call Handling**: Ducks/pauses during incoming phone calls and navigation alerts.
- **Test Quran Audio Mode**: Allows instant testing and verification without locking/unlocking the phone.

---

## 🛠 Project Structure

```
android/
├── build.gradle.kts           # Root Gradle build script
├── settings.gradle.kts        # Module configuration (rootProject: QuranUnlock)
├── gradle.properties          # JVM memory and AndroidX config
├── README.md                  # This build & deployment guide
└── app/
    ├── build.gradle.kts       # App module (compileSdk 36, targetSdk 36, minSdk 24)
    ├── proguard-rules.pro     # R8/ProGuard obfuscation and shrink rules
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/quranunlock/app/
        │   ├── QuranUnlockApp.kt           # Application class & Notification channel setup
        │   ├── data/
        │   │   ├── Surah.kt                # Quran data models
        │   │   ├── QuranRepository.kt      # Index of 114 Surahs & progression logic
        │   │   └── PreferencesManager.kt   # Persistent settings and progress
        │   ├── audio/
        │   │   ├── QuranAudioService.kt    # Foreground audio playback engine
        │   │   └── AudioNotificationHelper.kt # Media controls notification
        │   ├── receiver/
        │   │   ├── UnlockReceiver.kt       # ACTION_USER_PRESENT broadcast receiver
        │   │   └── BootReceiver.kt         # ACTION_BOOT_COMPLETED receiver
        │   └── ui/
        │       └── MainActivity.kt         # Islamic dark interface & tab navigation
        └── res/
            ├── drawable/                   # Vector icons and card backgrounds
            ├── layout/activity_main.xml    # Islamic layout
            ├── menu/bottom_nav_menu.xml    # Bottom navigation menu
            ├── values/
            │   ├── colors.xml              # Emerald, gold & stone dark palette
            │   ├── strings.xml             # UI strings & translations
            │   └── themes.xml              # Material dark theme
            └── xml/
                ├── backup_rules.xml
                └── data_extraction_rules.xml
```

---

## 🚀 How to Build in Android Studio

### Step 1: Open the Project
1. Download or extract the project folder.
2. Open **Android Studio** (Giraffe, Hedgehog, Iguana, Koala, Ladybug, or newer).
3. Click **File > Open...** and select the `android/` directory (where `settings.gradle.kts` is located).
4. Wait for Gradle Sync to complete.

### Step 2: Build Debug APK
1. In Android Studio, select the build variant **debug**.
2. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
3. Once completed, click **locate** in the popup notification.
4. The file `app-debug.apk` is located in `app/build/outputs/apk/debug/`.
5. Transfer this APK to any Android phone via USB, WhatsApp, or Google Drive and install it.

---

## 🔑 How to Create a Signed Release APK / AAB

For direct public distribution (website, MediaFire, Telegram) or Google Play Store:

### Option A: Using Android Studio UI
1. Click **Build > Generate Signed Bundle / APK...**
2. Choose **APK** (for direct installation) or **Android App Bundle** (for Google Play).
3. Click **Next**.
4. Under **Key store path**, click **Create new...** if you don't have one:
   - Choose a path on your PC (e.g., `quran-unlock.jks`).
   - Enter a secure password.
   - Fill in Alias (`quranunlock`) and Validity (25+ years).
5. Select **release** build variant and check **V1 (Jar Signature)** and **V2 (Full APK Signature)**.
6. Click **Finish**.
7. Your optimized, signed release APK is output in `app/release/app-release.apk`.

### Option B: Using Command Line (Terminal)
```bash
# Generate Debug APK
./gradlew assembleDebug

# Generate Signed Release APK
./gradlew assembleRelease

# Generate Android App Bundle (.aab)
./gradlew bundleRelease
```

---

## 📱 How to Build in AIDE (On Android Phone)

If you do not have a computer and want to build directly on your Android phone using **AIDE (Android IDE)**:

1. Copy the `android/` folder to your phone's internal storage: `/sdcard/AppProjects/QuranUnlock/`.
2. Open **AIDE**.
3. Tap **Open an existing Android project** and browse to `QuranUnlock`.
4. Tap the **Play / Run** icon at the top right.
5. AIDE will compile the Java/Kotlin resources and present an **Install** prompt directly on your device.

---

## 🔋 Android Background Playback & Unlock Restrictions (Crucial)

Modern Android versions (Android 10, 11, 12, 13, 14, 15, and 16) enforce aggressive battery optimization policies (Doze mode, App Standby buckets, OEM battery managers).

### 1. The Limitation
On some manufacturer skins (MIUI/HyperOS by Xiaomi, ColorOS by Oppo, FuntouchOS by Vivo, OneUI by Samsung):
- The system kills background processes after several minutes of screen inactivity.
- Broadcast receivers for `ACTION_USER_PRESENT` may be delayed or silenced if the app is marked as "Optimized" or put to sleep.

### 2. The Solution Provided in Quran Unlock
Quran Unlock includes:
1. **Dynamic Receiver in Application Class**: Registers `ACTION_USER_PRESENT` dynamically in memory for instant firing.
2. **Foreground Service with MediaPlayback Type**: Promotes playback to a high-priority foreground service with lock-screen notification controls.
3. **One-Tap Battery Optimization Button**: Built directly into the **Settings** tab. Tapping it opens Android's system dialog allowing the user to select **"Unrestricted" / "Don't Optimize"**.
4. **Boot Receiver (`RECEIVE_BOOT_COMPLETED`)**: Automatically reactivates Quran Unlock as soon as the phone reboots.

---

## 🔒 Permissions Used and Why

| Permission | Reason |
|---|---|
| `INTERNET` | Streams authentic audio files on demand and caches them locally. |
| `ACCESS_NETWORK_STATE` | Checks network connection state before initiating downloads. |
| `WAKE_LOCK` | Keeps CPU awake while reciting verse audio even if screen turns off. |
| `FOREGROUND_SERVICE` | Required to play audio in the background without being killed. |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Required by Android 14+ (API 34+) for foreground media services. |
| `POST_NOTIFICATIONS` | Required by Android 13+ (API 33+) to display playback controls in notification tray. |
| `RECEIVE_BOOT_COMPLETED` | Restores unlock listener after the phone restarts. |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Prompts user to exempt app from aggressive OEM battery killing. |

*Note: No intrusive permissions (Contacts, Location, Camera, Microphone, SMS, Storage) are requested.*

---

## 📦 External Dependencies Used

- **`androidx.core:core-ktx:1.13.1`**: Core Kotlin extensions.
- **`androidx.appcompat:appcompat:1.7.0`**: Backward-compatible UI components.
- **`com.google.android.material:material:1.12.0`**: Google Material 3 Design components.
- **`androidx.media3:media3-exoplayer:1.4.0`**: Modern Google Media3 ExoPlayer audio playback engine.
- **`androidx.media3:media3-session:1.4.0`**: Lock screen and Bluetooth media session controls.
- **`org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1`**: Asynchronous background sequence handling.

---

## 🌐 Audio Sources

- **Arabic Audio**: EveryAyah authentic high-definition MP3 verse recitations by Sheikh Mishary Rashid Alafasy (`https://everyayah.com/data/Alafasy_128kbps/`).
- **Urdu Translation Audio**: Shamshad Ali Khan reciting the renowned Urdu translation by Fateh Muhammad Jalandhari (`https://everyayah.com/data/translations/urdu_shamshad_ali_khan_46kbps/`).
