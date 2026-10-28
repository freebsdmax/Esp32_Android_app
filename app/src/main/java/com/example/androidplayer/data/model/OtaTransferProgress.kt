package com.example.androidplayer.data.model

/**
 * Live transfer statistics for a firmware update.
 *
 * [percent] is the overall progress (0-90 upload, 90-99 device-side apply),
 * [bytesDone]/[bytesTotal] describe the current phase, [bytesPerSecond] and
 * [etaSeconds] are smoothed estimates (-1 s when not known yet).
 */
data class OtaTransferProgress(
    val percent: Int,
    val bytesDone: Long,
    val bytesTotal: Long,
    val bytesPerSecond: Long,
    val etaSeconds: Long,
    val phase: String
) {
    val isApplyPhase: Boolean get() = phase == "apply"
}
