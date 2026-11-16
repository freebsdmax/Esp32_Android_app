package com.example.androidplayer.util

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.androidplayer.MainActivity
import com.example.androidplayer.R
import android.os.Build
import timber.log.Timber

/**
 * Назва застосунку, яку користувач задає в Налаштуваннях.
 *
 * ВАЖЛИВО: Android не дозволяє змінити підпис іконки на робочому столі під час
 * роботи (він береться з manifest при встановленні). Але ActivityManager
 * .setApplicationLabel() змінює назву, яку показує система: у списку недавніх
 * застосунків, у заголовку вікна та в системних діалогах.
 */
object AppLabel {
    const val PREFS = "esp32_player_prefs"
    const val KEY = "app_label"

    fun current(context: Context): String {
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null)
        if (!saved.isNullOrBlank()) return saved
        return context.applicationInfo.loadLabel(context.packageManager).toString()
    }

    fun save(context: Context, label: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, label.trim()).apply()
        apply(context)
    }

    /**
     * Android не дає змінити підпис іконки на робочому столі під час роботи.
     * Тому назва застосовується до заголовка вікна/списку недавніх застосунків
     * (MainActivity.title) і показується в інтерфейсі.
     */
    /**
     * Android не дозволяє змінити підпис іконки самої програми, але дозволяє
     * створити ярлик на робочому столі з БУДЬ-ЯКОЮ назвою (ShortcutManager).
     * Це те, чим користуються «перейменувачі» застосунків.
     */
    fun pinShortcut(context: Context, label: String) {
        val name = label.trim().ifBlank { current(context) }
        runCatching {
            val shortcut = ShortcutInfoCompat.Builder(context, "custom_name")
                .setShortLabel(name.take(40))
                .setLongLabel(name)
                .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
                .setIntent(
                    Intent(context, MainActivity::class.java)
                        .setAction(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_LAUNCHER)
                )
                .build()
            ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
        }.onFailure { Timber.w(it, "pin shortcut failed") }
    }

    fun apply(context: Context) {
        // нічого: заголовок виставляє MainActivity
    }
}
