package com.example.androidplayer.data.update

import android.content.Context
import androidx.core.content.edit
import com.example.androidplayer.data.github.GitHubReleaseClient
import com.example.androidplayer.ui.i18n.Loc
import com.example.androidplayer.ui.notify.AppNotify
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Фонова перевірка оновлень: при старті та раз на N годин (налаштування).
 *
 * Не використовує версії з плати (BLE може бути відключений) — стежить за появою
 * **нового тега релізу** у трьох репозиторіях і один раз показує сповіщення.
 */
@Singleton
class UpdateChecker @Inject constructor(
    private val github: GitHubReleaseClient,
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var started = false

    /** true = перевіряти автоматично (за замовчуванням). */
    var autoEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO, true)
        set(value) = prefs.edit { putBoolean(KEY_AUTO, value) }

    /** Інтервал у годинах: 6 / 12 / 24. */
    var intervalHours: Int
        get() = prefs.getInt(KEY_INTERVAL, 12).coerceIn(1, 168)
        set(value) = prefs.edit { putInt(KEY_INTERVAL, value.coerceIn(1, 168)) }

    /** stable = тільки звичайні релізи, beta = разом із prerelease. */
    var betaChannel: Boolean
        get() = prefs.getBoolean(KEY_BETA, false)
        set(value) = prefs.edit { putBoolean(KEY_BETA, value) }

    fun start() {
        if (started) return
        started = true
        scope.launch {
            delay(5000)          // не заважаємо старту UI
            while (true) {
                if (autoEnabled) {
                    runCatching { checkOnce(notify = true) }
                        .onFailure { Timber.w(it, "Background update check failed") }
                }
                delay(intervalHours * 60L * 60L * 1000L)
            }
        }
    }

    /**
     * Одна перевірка. @return список людських описів нових релізів.
     */
    suspend fun checkOnce(notify: Boolean): List<String> {
        val includePre = betaChannel
        val repos = listOf(
            Pair(REPO_APP, "App"),
            Pair(REPO_FW, Loc.updFirmware()),
            Pair(REPO_SLAVE, Loc.updSlave())
        )

        val fresh = mutableListOf<String>()
        for ((repo, label) in repos) {
            val release = github.latestRelease(repo, includePrereleases = includePre) ?: continue
            val tag = release.tag
            val seenKey = "seen_${repo.substringAfter('/')}"
            val seen = prefs.getString(seenKey, null)

            if (seen == null) {
                // перший запуск: просто запам'ятовуємо, без сповіщення
                prefs.edit { putString(seenKey, tag) }
                continue
            }
            if (seen != tag) {
                prefs.edit { putString(seenKey, tag) }
                fresh.add("$label: $tag")
            }
        }

        if (fresh.isNotEmpty() && notify) {
            AppNotify.info(Loc.updateAvailableText(fresh.joinToString(" · ")))
        }
        return fresh
    }

    /** Скидає «вже бачили», щоб наступна перевірка знову повідомила. */
    fun resetSeen() {
        prefs.edit { clear() }
    }

    companion object {
        const val PREFS = "update_checker"
        private const val KEY_AUTO = "auto"
        private const val KEY_INTERVAL = "interval_h"
        private const val KEY_BETA = "beta"

        const val REPO_APP = "freebsdmax/Esp32_Android_app"
        const val REPO_FW = "freebsdmax/Ford-DensO-mp3-master"
        const val REPO_SLAVE = "freebsdmax/Ford-DensO-bluetooth-slave"
    }
}
