package com.example.androidplayer.data.model

/** Один файл у GitHub Release. */
data class ReleaseAsset(
    val name: String,
    val url: String,
    val size: Long
)

/** Реліз, отриманий з GitHub API. */
data class ReleaseInfo(
    val tag: String,
    val name: String,
    val notes: String,
    val assets: List<ReleaseAsset>
) {
    fun asset(extensions: List<String>, exclude: List<String> = emptyList()): ReleaseAsset? {
        val lower = extensions.map { it.lowercase() }
        val excl = exclude.map { it.lowercase() }
        return assets.firstOrNull { a ->
            val n = a.name.lowercase()
            lower.any { n.endsWith(it) } && excl.none { n.contains(it) }
        }
    }
}

/** Порівняння версій: "v1.0.2-3-gabc" -> [1,0,2] */
object VersionCompare {

    fun parts(version: String?): List<Int> {
        if (version.isNullOrBlank()) return emptyList()
        val cleaned = version.trim().removePrefix("v").substringBefore('-')
        return cleaned.split('.').mapNotNull { it.toIntOrNull() }
    }

    /** true, якщо [latest] новіша за [current]. */
    fun isNewer(latest: String?, current: String?): Boolean {
        val a = parts(latest)
        val b = parts(current)
        if (a.isEmpty()) return false
        if (b.isEmpty()) return true
        val size = maxOf(a.size, b.size)
        for (i in 0 until size) {
            val x = a.getOrElse(i) { 0 }
            val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }

    fun short(version: String?): String {
        if (version.isNullOrBlank()) return "—"
        return version.trim().removePrefix("v").substringBefore('-')
    }
}
