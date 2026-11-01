package com.example.androidplayer.data.usb

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import kotlin.math.min

/**
 * Прошивка ESP32 через USB з телефона (ROM bootloader).
 *
 * Реалізує мінімальний набір команд esptool:
 * SYNC, FLASH_BEGIN, FLASH_DATA, FLASH_END, SPI_FLASH_MD5 — достатньо,
 * щоб залити злитий образ (`-full.bin`) на 0x0.
 */
class EspFlasher(
    private val usbManager: UsbManager,
    private val port: UsbSerialPort
) {
    sealed class Result {
        data object Ok : Result()
        data class Error(val message: String) : Result()
    }

    interface Listener {
        fun onProgress(percent: Int, detail: String)
        fun onLog(line: String)
    }

    private val SLIP_END = 0xC0
    private val SLIP_ESC = 0xDB
    private val SLIP_ESC_END = 0xDC
    private val SLIP_ESC_ESC = 0xDD

    private val OP_FLASH_BEGIN = 0x02
    private val OP_FLASH_DATA = 0x03
    private val OP_FLASH_END = 0x04
    private val OP_SYNC = 0x08
    private val OP_MD5 = 0x13

    private val BLOCK_SIZE = 4096
    private val TIMEOUT_MS = 3000
    private val FLASH_TIMEOUT_MS = 15000

    private val rx = ByteArrayOutputStream()
    private val noise = ByteArrayOutputStream()

    /** Відкриває порт і переводить чіп у режим завантажувача. */
    fun open(baud: Int = 115200, autoReset: Boolean = true) {
        port.open(usbManager.openDevice(port.device))
        port.setParameters(baud, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        port.dtr = false
        port.rts = false
        if (autoReset) {
            runCatching {
                port.rts = true          // EN = 0 (reset)
                port.dtr = true          // IO0 = 0 (boot)
                Thread.sleep(120)
                port.rts = false
                Thread.sleep(100)
                port.dtr = false
                Thread.sleep(100)
            }
        }
        Thread.sleep(200)
        port.purgeHwBuffers(true, true)
    }

    fun close() {
        runCatching { port.close() }
    }

    /** Синхронізація з ROM bootloader. */
    suspend fun sync(listener: Listener): Boolean = withContext(Dispatchers.IO) {
        repeat(7) { attempt ->
            rx.reset()
            val payload = byteArrayOf(0x07, 0x07, 0x12, 0x20)
            writePacket(OP_SYNC, payload, 0)
            val resp = readResponse(TIMEOUT_MS)
            if (resp != null && resp.status == 0) {
                listener.onLog("SYNC ok (спроба ${attempt + 1})")
                return@withContext true
            }
            listener.onLog("SYNC ${attempt + 1}/7 — немає відповіді")
            delay(150)
        }
        false
    }

    /** Прошиває [image] за адресою [offset] (0x0 для злитого образу). */
    suspend fun flash(
        image: ByteArray,
        offset: Int = 0,
        listener: Listener
    ): Result = withContext(Dispatchers.IO) {
        try {
            if (!sync(listener)) {
                return@withContext Result.Error(
                    "Чіп не відповідає. Затисніть BOOT, натисніть RESET і спробуйте ще раз."
                )
            }

            val blocks = (image.size + BLOCK_SIZE - 1) / BLOCK_SIZE
            listener.onLog("FLASH_BEGIN: ${image.size} байт, $blocks блоків, offset=0x${offset.toString(16)}")
            writePacket(
                OP_FLASH_BEGIN,
                buildFlashBegin(image.size, blocks, BLOCK_SIZE, offset),
                0
            )
            val begin = readResponse(TIMEOUT_MS)
                ?: return@withContext Result.Error("Немає відповіді на FLASH_BEGIN")
            if (begin.status != 0) {
                return@withContext Result.Error("FLASH_BEGIN відхилено (status=${begin.status})")
            }

            var seq = 0
            var written = 0
            while (written < image.size) {
                val len = min(BLOCK_SIZE, image.size - written)
                val chunk = image.copyOfRange(written, written + len)

                var attempt = 0
                while (true) {
                    rx.reset()
                    writePacket(OP_FLASH_DATA, buildFlashData(len, seq, chunk), checksum(chunk))
                    val resp = readResponse(FLASH_TIMEOUT_MS)
                    if (resp == null) {
                        if (attempt++ >= 3) {
                            return@withContext Result.Error("Немає відповіді на блок ${seq + 1}")
                        }
                        listener.onLog("Блок ${seq + 1}: таймаут, повтор")
                        continue
                    }
                    if (resp.status != 0) {
                        if (attempt++ >= 3) {
                            return@withContext Result.Error("Блок ${seq + 1} відхилено (status=${resp.status})")
                        }
                        listener.onLog("Блок ${seq + 1}: status=${resp.status}, повтор")
                        continue
                    }
                    break
                }

                written += len
                seq++
                val percent = (written * 100) / image.size
                listener.onProgress(percent, "Записано ${written / 1024} КБ з ${image.size / 1024} КБ")
            }

            // MD5 перевірка
            val md = MessageDigest.getInstance("MD5").digest(image)
            listener.onLog("MD5 перевірка…")
            writePacket(OP_MD5, buildMd5Payload(offset, image.size, md), 0)
            val md5resp = readResponse(TIMEOUT_MS)
            if (md5resp != null && md5resp.payload != null) {
                val expected = md.joinToString("") { "%02x".format(it) }
                val actual = String(md5resp.payload).trim().lowercase()
                if (actual.isNotEmpty() && actual != expected) {
                    return@withContext Result.Error("MD5 не збігається: $actual != $expected")
                }
                listener.onLog("MD5 ok")
            }

            // Завершення + перезавантаження
            writePacket(OP_FLASH_END, byteArrayOf(0, 0, 0, 0), 0)
            readResponse(TIMEOUT_MS)
            listener.onProgress(100, "Готово, пристрій перезавантажується")
            Result.Ok
        } catch (e: Exception) {
            Timber.e(e, "USB flash failed")
            Result.Error(e.message ?: "USB помилка")
        }
    }

    // ---------------- протокол ----------------

    private fun checksum(data: ByteArray): Int {
        var crc = 0xEF
        for (b in data) crc = crc xor (b.toInt() and 0xFF)
        return crc
    }

    private fun buildFlashBegin(size: Int, blocks: Int, blockSize: Int, offset: Int): ByteArray {
        val out = ByteArray(16)
        putU32(out, 0, size)
        putU32(out, 4, blocks)
        putU32(out, 8, blockSize)
        putU32(out, 12, offset)
        return out
    }

    private fun buildFlashData(size: Int, seq: Int, data: ByteArray): ByteArray {
        val out = ByteArray(16 + data.size)
        putU32(out, 0, size)
        putU32(out, 4, seq)
        putU32(out, 8, 0)
        putU32(out, 12, 0)
        System.arraycopy(data, 0, out, 16, data.size)
        return out
    }

    private fun buildMd5Payload(offset: Int, size: Int, md5: ByteArray): ByteArray {
        val out = ByteArray(32)
        putU32(out, 0, offset)
        putU32(out, 4, size)
        putU32(out, 8, 0)
        putU32(out, 12, 0)
        System.arraycopy(md5, 0, out, 16, 16)
        return out
    }

    private fun putU32(buf: ByteArray, pos: Int, value: Int) {
        buf[pos] = (value and 0xFF).toByte()
        buf[pos + 1] = ((value ushr 8) and 0xFF).toByte()
        buf[pos + 2] = ((value ushr 16) and 0xFF).toByte()
        buf[pos + 3] = ((value ushr 24) and 0xFF).toByte()
    }

    private data class Response(val opcode: Int, val status: Int, val payload: ByteArray?)

    private fun writePacket(opcode: Int, data: ByteArray, crc: Int) {
        val packet = ByteArray(8 + data.size)
        packet[0] = 0x00                       // direction: request
        packet[1] = opcode.toByte()
        packet[2] = (data.size and 0xFF).toByte()
        packet[3] = ((data.size ushr 8) and 0xFF).toByte()
        putU32(packet, 4, crc)
        System.arraycopy(data, 0, packet, 8, data.size)
        port.write(slipEncode(packet), 2000)
    }

    private fun slipEncode(data: ByteArray): ByteArray {
        val out = ByteArrayOutputStream()
        out.write(SLIP_END)
        for (b in data) {
            val v = b.toInt() and 0xFF
            when (v) {
                SLIP_END -> { out.write(SLIP_ESC); out.write(SLIP_ESC_END) }
                SLIP_ESC -> { out.write(SLIP_ESC); out.write(SLIP_ESC_ESC) }
                else -> out.write(v)
            }
        }
        out.write(SLIP_END)
        return out.toByteArray()
    }

    /** Читає один SLIP-пакет і повертає розібрану відповідь. */
    private fun readResponse(timeoutMs: Int): Response? {
        val deadline = System.currentTimeMillis() + timeoutMs
        val buf = ByteArray(1024)

        while (System.currentTimeMillis() < deadline) {
            val n = runCatching {
                port.read(buf, min(200, (deadline - System.currentTimeMillis()).coerceAtLeast(1).toInt()))
            }.getOrNull() ?: 0
            if (n > 0) {
                for (i in 0 until n) rx.write(buf[i].toInt())
                val frame = extractFrame()
                if (frame != null) return parseResponse(frame)
            } else {
                Thread.sleep(5)
            }
        }
        return null
    }

    /** Витягує один SLIP-кадр з накопиченого потоку. */
    private fun extractFrame(): ByteArray? {
        val data = rx.toByteArray()

        var start = -1
        for (i in data.indices) {
            if ((data[i].toInt() and 0xFF) == SLIP_END) { start = i; break }
        }
        if (start < 0) return null

        val out = ByteArrayOutputStream()
        var escaped = false
        for (i in start + 1 until data.size) {
            val v = data[i].toInt() and 0xFF
            when {
                v == SLIP_END -> {
                    rx.reset()
                    for (j in i + 1 until data.size) rx.write(data[j].toInt())
                    return out.toByteArray()
                }
                v == SLIP_ESC -> escaped = true
                escaped -> {
                    out.write(if (v == SLIP_ESC_END) SLIP_END else SLIP_ESC)
                    escaped = false
                }
                else -> out.write(v)
            }
        }
        return null
    }

    private fun parseResponse(frame: ByteArray): Response? {
        if (frame.size < 8) return null
        val opcode = frame[1].toInt() and 0xFF
        val size = (frame[2].toInt() and 0xFF) or ((frame[3].toInt() and 0xFF) shl 8)

        var status = 0
        var payloadStart = 8
        if (frame.size >= 12) {
            status = frame[8].toInt() and 0xFF
            payloadStart = 12
        }

        val payload = if (frame.size > payloadStart) {
            frame.copyOfRange(payloadStart, min(frame.size, payloadStart + size))
        } else null

        return Response(opcode, status, payload)
    }
}
