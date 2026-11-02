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

    private val LINE_DTR = 0
    private val LINE_RTS = 1

    private val BLOCK_SIZE = 4096
    private val TIMEOUT_MS = 3000
    private val FLASH_TIMEOUT_MS = 15000

    private val rx = ByteArrayOutputStream()
    private val noise = ByteArrayOutputStream()

    /** Відкриває порт. Скидання в режим завантажувача робить [flash]. */
    fun open(baud: Int = 115200) {
        port.open(usbManager.openDevice(port.device))
        port.setParameters(baud, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        runCatching {
            port.dtr = false
            port.rts = false
        }
        Thread.sleep(150)
        runCatching { port.purgeHwBuffers(true, true) }
    }

    /**
     * Одна спроба скидання: тримаємо IO0 у низькому рівні, смикаємо EN.
     *
     * @param io0Line лінія, підключена до IO0 (DTR або RTS)
     * @param io0Inverted true — на платі інвертор по лінії IO0
     * @param enInverted  true — на платі інвертор по лінії EN
     */
    private fun pulseReset(io0Line: Int, io0Inverted: Boolean, enInverted: Boolean) {
        val enLine = if (io0Line == LINE_DTR) LINE_RTS else LINE_DTR

        fun set(line: Int, asserted: Boolean, inverted: Boolean) {
            val value = if (inverted) !asserted else asserted
            if (line == LINE_DTR) port.dtr = value else port.rts = value
        }

        // обидві лінії відпущені
        set(io0Line, false, io0Inverted)
        set(enLine, false, enInverted)
        Thread.sleep(60)
        // IO0 = LOW, EN = LOW → чіп у скиданні
        set(io0Line, true, io0Inverted)
        set(enLine, true, enInverted)
        Thread.sleep(150)
        // EN = HIGH → старт; IO0 ще LOW → download mode
        set(enLine, false, enInverted)
        Thread.sleep(80)
        set(io0Line, false, io0Inverted)
    }

    /** Скидання для вбудованого USB-Serial/JTAG (ESP32-S3/C3 без окремого моста). */
    private fun usbJtagReset() {
        port.rts = false
        port.dtr = false          // idle
        Thread.sleep(100)
        port.dtr = true           // тримаємо IO0
        port.rts = false
        Thread.sleep(100)
        port.rts = true           // reset
        port.dtr = false
        port.rts = true
        Thread.sleep(100)
        port.dtr = false
        port.rts = false
        Thread.sleep(60)
    }

    /**
     * Перебирає всі варіанти розводки DTR/RTS (нормальна/переставлена,
     * з інверторами й без) плюс послідовність USB-Serial/JTAG. Після кожного
     * скидання — SYNC: якщо чіп відповів, він уже в режимі завантажувача.
     */
    private suspend fun enterBootloader(listener: Listener): Boolean {
        // Плата могла вже бути в режимі завантажувача (після BOOT+RESET).
        if (runCatching { sync(listener, tries = 1, timeoutMs = 700) }.getOrDefault(false)) {
            listener.onLog("Чіп уже в режимі завантажувача")
            return true
        }

        data class Variant(val label: String, val run: () -> Unit)

        val variants = listOf(
            Variant("DTR→IO0, RTS→EN") { pulseReset(LINE_DTR, false, false) },
            Variant("RTS→IO0, DTR→EN") { pulseReset(LINE_RTS, false, false) },
            Variant("DTR→IO0, RTS→EN, IO0 інверт.") { pulseReset(LINE_DTR, true, false) },
            Variant("DTR→IO0, RTS→EN, EN інверт.") { pulseReset(LINE_DTR, false, true) },
            Variant("DTR→IO0, RTS→EN, обидві інверт.") { pulseReset(LINE_DTR, true, true) },
            Variant("RTS→IO0, DTR→EN, IO0 інверт.") { pulseReset(LINE_RTS, true, false) },
            Variant("RTS→IO0, DTR→EN, EN інверт.") { pulseReset(LINE_RTS, false, true) },
            Variant("RTS→IO0, DTR→EN, обидві інверт.") { pulseReset(LINE_RTS, true, true) },
            Variant("USB-Serial/JTAG") { usbJtagReset() }
        )

        for (v in variants) {
            listener.onLog("Авто-скидання: ${v.label}…")
            val err = runCatching { v.run() }.exceptionOrNull()
            if (err != null) {
                listener.onLog("  лінії DTR/RTS: ${err.message ?: err::class.java.simpleName}")
            }
            Thread.sleep(100)
            runCatching { port.purgeHwBuffers(true, true) }
            val ok = runCatching { sync(listener, tries = 2, timeoutMs = 1000) }
                .getOrDefault(false)
            if (ok) {
                listener.onLog("Режим завантажувача: ${v.label}")
                return true
            }
        }
        return false
    }

    /** Публічна перевірка авто-скидання (кнопка «Перевірити авто-скидання»). */
    suspend fun testBootloader(listener: Listener): Boolean = withContext(Dispatchers.IO) {
        enterBootloader(listener)
    }

    fun close() {
        runCatching { port.close() }
    }

    /** Синхронізація з ROM bootloader. */
    suspend fun sync(
        listener: Listener,
        tries: Int = 7,
        timeoutMs: Int = TIMEOUT_MS
    ): Boolean = withContext(Dispatchers.IO) {
        repeat(tries) { attempt ->
            rx.reset()
            val payload = byteArrayOf(0x07, 0x07, 0x12, 0x20)
            writePacket(OP_SYNC, payload, 0)
            val resp = readResponse(timeoutMs)
            if (resp != null && resp.status == 0) {
                listener.onLog("SYNC ok (спроба ${attempt + 1})")
                return@withContext true
            }
            listener.onLog("SYNC ${attempt + 1}/$tries — немає відповіді")
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
            listener.onLog(
                "Порт %04X:%04X, драйвер %s".format(
                    port.device.vendorId, port.device.productId,
                    port.javaClass.simpleName
                )
            )
            if (!enterBootloader(listener)) {
                return@withContext Result.Error(
                    "Чіп не вийшов у режим завантажувача автоматично. " +
                        "На цій платі лінії DTR/RTS не керують BOOT/EN — затисніть BOOT, " +
                        "натисніть RESET, або прошийте через BLE-OTA."
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
