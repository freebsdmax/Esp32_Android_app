package com.example.androidplayer.data.github

import android.content.Context
import com.example.androidplayer.data.model.ReleaseAsset
import com.example.androidplayer.data.model.ReleaseInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** Why a release check failed (unauthenticated GitHub allows only 60 requests/hour). */
sealed class GitHubError {
    /** API quota is exhausted; [resetAt] is a local timestamp (ms). */
    data class RateLimited(val resetAt: Long) : GitHubError()

    /** No network / DNS / TLS problem. */
    data class Network(val message: String) : GitHubError()

    /** Any other HTTP status. */
    data class Http(val code: Int) : GitHubError()
}

/**
 * Мінімальний клієнт GitHub Releases (без авторизації, ліміт 60 запитів/год).
 *
 * Кешує відповіді на диск і використовує умовні запити (ETag -> 304), бо один
 * невдалий "перевірити оновлення" інакше з'їдає три запити з ліміту.
 */
@Singleton
class GitHubReleaseClient @Inject constructor(
    baseClient: OkHttpClient,
    @ApplicationContext context: Context
) {
    // Для завантаження файлів логування тіла не потрібне (10 МБ у Logcat = OOM)
    private val apiClient = baseClient.newBuilder()
        .apply { interceptors().clear() }
        .build()

    private val prefs = context.getSharedPreferences("github_releases", Context.MODE_PRIVATE)

    /** Last failure, used by the UI to explain what went wrong. */
    @Volatile
    var lastError: GitHubError? = null
        private set

    suspend fun latestRelease(
        repo: String,
        maxAgeMs: Long = CACHE_TTL_MS
    ): ReleaseInfo? = withContext(Dispatchers.IO) {
        val cached = readCache(repo)
        val cachedAt = prefs.getLong(keyTime(repo), 0L)
        if (cached != null && System.currentTimeMillis() - cachedAt < maxAgeMs) {
            lastError = null
            return@withContext cached
        }

        val builder = Request.Builder()
            .url("https://api.github.com/repos/$repo/releases/latest")
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", "Ford-Player-Android")
        prefs.getString(keyEtag(repo), null)?.let { builder.header("If-None-Match", it) }

        try {
            apiClient.newCall(builder.build()).execute().use { response ->
                when {
                    response.code == 304 && cached != null -> {
                        Timber.d("GitHub: $repo not modified (304, not counted against the limit)")
                        prefs.edit().putLong(keyTime(repo), System.currentTimeMillis()).apply()
                        lastError = null
                        return@withContext cached
                    }
                    response.isSuccessful -> {
                        val body = response.body?.string() ?: return@withContext cached
                        val parsed = parse(repo, body) ?: return@withContext cached
                        val etag = response.header("ETag")
                        prefs.edit()
                            .putString(keyBody(repo), body)
                            .putLong(keyTime(repo), System.currentTimeMillis())
                            .apply {
                                if (etag != null) putString(keyEtag(repo), etag)
                            }
                            .apply()
                        lastError = null
                        return@withContext parsed
                    }
                    response.code == 403 || response.code == 429 -> {
                        val reset = response.header("X-RateLimit-Reset")?.toLongOrNull()
                        val remaining = response.header("X-RateLimit-Remaining")
                        if (remaining == "0" || reset != null) {
                            lastError = GitHubError.RateLimited(reset?.times(1000) ?: 0L)
                            Timber.w("GitHub: rate limit exhausted, reset at $reset")
                        } else {
                            lastError = GitHubError.Http(response.code)
                            Timber.w("GitHub: $repo -> HTTP ${response.code}")
                        }
                        return@withContext cached
                    }
                    else -> {
                        lastError = GitHubError.Http(response.code)
                        Timber.w("GitHub: $repo -> HTTP ${response.code}")
                        return@withContext cached
                    }
                }
            }
        } catch (e: Exception) {
            lastError = GitHubError.Network(e.message ?: e.javaClass.simpleName)
            Timber.w(e, "GitHub: failed to load $repo")
            cached
        }
    }

    private fun parse(repo: String, body: String): ReleaseInfo? {
        return try {
            val json = JSONObject(body)
            val tag = json.optString("tag_name")
            if (tag.isBlank()) return null

            val assetsJson = json.optJSONArray("assets")
            val assets = buildList {
                if (assetsJson != null) {
                    for (i in 0 until assetsJson.length()) {
                        val a = assetsJson.optJSONObject(i) ?: continue
                        add(
                            ReleaseAsset(
                                name = a.optString("name"),
                                url = a.optString("browser_download_url"),
                                size = a.optLong("size")
                            )
                        )
                    }
                }
            }

            ReleaseInfo(
                tag = tag,
                name = json.optString("name", tag),
                notes = json.optString("body"),
                assets = assets
            )
        } catch (e: Exception) {
            Timber.w(e, "GitHub: cannot parse release of $repo")
            null
        }
    }

    private fun readCache(repo: String): ReleaseInfo? {
        val body = prefs.getString(keyBody(repo), null) ?: return null
        return parse(repo, body)
    }

    private fun keyBody(repo: String) = "body_$repo"
    private fun keyEtag(repo: String) = "etag_$repo"
    private fun keyTime(repo: String) = "ts_$repo"

    /** Завантажує файл, повідомляючи прогрес 0..100. */
    suspend fun download(
        url: String,
        destination: File,
        onProgress: (Int) -> Unit
    ): File = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Ford-Player-Android")
            .build()

        apiClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            val body = response.body ?: throw IOException("Empty response body")
            val total = body.contentLength()

            destination.parentFile?.mkdirs()
            body.byteStream().use { input ->
                destination.outputStream().use { output ->
                    val buffer = ByteArray(16 * 1024)
                    var done = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        done += read
                        if (total > 0) onProgress(((done * 100) / total).toInt().coerceIn(0, 100))
                    }
                }
            }
        }
        onProgress(100)
        destination
    }

    companion object {
        /** Repeated checks within this window are served from the local cache. */
        const val CACHE_TTL_MS = 15 * 60 * 1000L
    }
}
