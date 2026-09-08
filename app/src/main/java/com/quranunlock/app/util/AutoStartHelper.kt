package com.quranunlock.app.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Different Android phone brands (Xiaomi, Vivo, Oppo, Huawei, Samsung, etc.) have their own
 * extra background-app killers on top of stock Android's battery optimization. There is no
 * single official Android API to disable all of them — each OEM exposes its own hidden
 * settings screen. This helper detects the phone's manufacturer and jumps straight to the
 * right screen, falling back gracefully if that screen isn't available on the device.
 */
object AutoStartHelper {

    /**
     * Tries each known OEM "autostart / allow background" screen for the current manufacturer.
     * Returns true if a screen was successfully opened.
     */
    fun openAutoStartSettings(context: Context): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()

        val intents: List<Intent> = when {
            manufacturer.contains("xiaomi") -> listOf(
                intentFor("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
                intentFor("com.miui.securitycenter", "com.miui.permcenter.autostart.MainActivity")
            )
            manufacturer.contains("oppo") -> listOf(
                intentFor("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"),
                intentFor("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"),
                intentFor("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")
            )
            manufacturer.contains("vivo") -> listOf(
                intentFor("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"),
                intentFor("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")
            )
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> listOf(
                intentFor("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"),
                intentFor("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
            )
            manufacturer.contains("oneplus") -> listOf(
                intentFor("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity")
            )
            manufacturer.contains("samsung") -> listOf(
                // Samsung has no single public autostart screen; send user to the app's
                // battery details page inside Device Care instead.
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            )
            manufacturer.contains("asus") -> listOf(
                intentFor("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")
            )
            manufacturer.contains("letv") -> listOf(
                intentFor("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")
            )
            else -> emptyList()
        }

        for (intent in intents) {
            if (tryStart(context, intent)) return true
        }

        // Universal fallback: app's own details screen, where the user can usually find
        // "Battery" -> "Unrestricted" or similar options manually.
        return tryStart(
            context,
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        )
    }

    private fun intentFor(pkg: String, cls: String): Intent =
        Intent().apply {
            component = ComponentName(pkg, cls)
            addCategory(Intent.CATEGORY_DEFAULT)
        }

    private fun tryStart(context: Context, intent: Intent): Boolean {
        return try {
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    /** Human-readable name of the manufacturer-specific setting, for UI copy. */
    fun autoStartSettingName(): String {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("xiaomi") -> "Autostart"
            manufacturer.contains("oppo") -> "Startup Manager"
            manufacturer.contains("vivo") -> "Background App Management"
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> "App Launch / Protected Apps"
            manufacturer.contains("oneplus") -> "Battery Optimization Chain Launch"
            manufacturer.contains("samsung") -> "Battery Usage (set to Unrestricted)"
            manufacturer.contains("asus") -> "Auto-start Manager"
            else -> "App Battery Settings"
        }
    }
}
