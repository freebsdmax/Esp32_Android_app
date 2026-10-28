package com.example.androidplayer.data.github

import com.example.androidplayer.data.model.ReleaseAsset
import com.example.androidplayer.data.model.ReleaseInfo
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

/**
 * Мінімальний клієнт GitHub Releases (без авторизації, ліміт 60 запитів/год).
 */
@Singleton
class GitHubReleaseClient @Inject constructor(
    baseClient: OkHttpClient
) {
    // Для завантаження файлів логування тіла не потрібне (10 МБ у Logcat = OOM)
    private val apiClient = baseClient.newBuilder()
        .apply { interceptors().clear() }
        .build()

    suspend fun latestRelease(repo: String): ReleaseInfo? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://api.github.com/repos/$repo/releases/latest")
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", "Ford-Player-Android")
            .build()

        try {
            apiClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Timber.w("GitHub: $repo -> HTTP ${response.code}")
                    return@withContext null
                }
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                val tag = json.optString("tag_name")
                if (tag.isBlank()) return@withContext null

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
            }
        } catch (e: Exception) {
            Timber.w(e, "GitHub: failed to load $repo")
            null
        }
    }

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
}
