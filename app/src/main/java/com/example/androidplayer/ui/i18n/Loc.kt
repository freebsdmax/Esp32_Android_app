package com.example.androidplayer.ui.i18n

import java.util.Locale

/**
 * Lightweight runtime localization: Ukrainian when the system language is
 * Ukrainian, English for every other language.
 */
object Loc {
    private val isUkrainian: Boolean = Locale.getDefault().language == "uk"

    private val ukrainian = mapOf(
        "AP Настройки (создание сети)" to "AP Налаштування (створення мережі)",
        "SSID (название сети)" to "SSID (назва мережі)",
        "STA Настройки (подключение к сети)" to "STA Налаштування (підключення до мережі)",
        "Update over BLE" to "Оновити через BLE",
        "Works without Wi-Fi. Slow (1-5 min)." to "Працює без Wi-Fi. Повільно (1-5 хв).",
        "Master передає прошивку Slave по UART. Не вимикайте живлення." to "Master передає прошивку Slave по UART. Не вимикайте живлення.",
        "Выбрать и загрузить прошивку Master" to "Вибрати й завантажити прошивку Master",
        "Выбрать и загрузить прошивку Slave" to "Вибрати й завантажити прошивку Slave",
        "Від'єднати" to "Від'єднати",
        "Детальные логи (эмодзи и баннеры)" to "Детальні логи (емодзі та банери)",
        "ЗАРАЗ ГРАЄ" to "ЗАРАЗ ГРАЄ",
        "Завантаження..." to "Завантаження...",
        "Загрузить" to "Завантажити",
        "Загрузка информации о системе..." to "Завантаження інформації про систему...",
        "Загрузка статистики CAN..." to "Завантаження статистики CAN...",
        "Закрыть" to "Закрити",
        "Застосовується одразу" to "Застосовується одразу",
        "Зовнішній вигляд" to "Зовнішній вигляд",
        "Информация" to "Інформація",
        "Керування ESP32 Music Player через Bluetooth: плеєр, плейлист, OTA та моніторинг." to "Керування ESP32 Music Player через Bluetooth: плеєр, плейлист, OTA та моніторинг.",
        "Логи компонентов:" to "Логи компонентів:",
        "Логи отсутствуют" to "Логи відсутні",
        "Налаштування" to "Налаштування",
        "Обновить" to "Оновити",
        "Обновить логи" to "Оновити логи",
        "Обновление основной прошивки ESP32. Устройство будет перезагружено после обновления." to "Оновлення основної прошивки ESP32. Пристрій перезавантажиться після оновлення.",
        "Обновление прошивки Slave ESP32 через UART. Передача происходит блоками по 240 байт с проверкой CRC." to "Оновлення прошивки Slave ESP32 через UART. Передача блоками по 240 байт із перевіркою CRC.",
        "Огляд" to "Огляд",
        "Оновити Slave" to "Оновити Slave",
        "Отключить BLE и использовать Wi-Fi" to "Відключити BLE і використовувати Wi-Fi",
        "Оформлення інтерфейсу" to "Оформлення інтерфейсу",
        "Очистить логи" to "Очистити логи",
        "Ошибка" to "Помилка",
        "Пароль" to "Пароль",
        "Перезагрузить устройство" to "Перезавантажити пристрій",
        "Плейлист порожній" to "Плейлист порожній",
        "Предыдущий трек" to "Попередній трек",
        "Пристрої не знайдені" to "Пристрої не знайдені",
        "Про застосунок" to "Про застосунок",
        "Прогресс воспроизведения" to "Прогрес відтворення",
        "Підключ" to "Підключ",
        "Підключення" to "Підключення",
        "Підключити" to "Підключити",
        "Підключіть USB-флешку з MP3" to "Підключіть USB-флешку з MP3",
        "Режим Wi-Fi" to "Режим Wi-Fi",
        "Сканувати" to "Сканувати",
        "Следующий трек" to "Наступний трек",
        "Сохранить" to "Зберегти",
        "Сохранить настройки" to "Зберегти налаштування",
        "Темна тема" to "Темна тема",
        "Управление" to "Керування",
        "Уровень логов" to "Рівень логів",
        "Успех" to "Успіх",
        "Версія 1.0.0" to "Версія 1.0.0",
        "⚙️ Управление устройством" to "⚙️ Керування пристроєм",
        "⚠️ Внимание: Перезагрузка прервет текущее воспроизведение" to "⚠️ Увага: перезавантаження перерве відтворення",
        "🎵 Slave ESP32 (Bluetooth модуль через UART)" to "🎵 Slave ESP32 (Bluetooth модуль через UART)",
        "🎵 Slave ESP32:" to "🎵 Slave ESP32:",
        "💻 Системная информация" to "💻 Системна інформація",
        "📊 Прогресс OTA" to "📊 Прогрес OTA",
        "📋 Информация о версиях" to "📋 Інформація про версії",
        "📋 Системные логи" to "📋 Системні логи",
        "📝 Настройки логирования" to "📝 Налаштування логування",
        "📡 Статистика CAN" to "📡 Статистика CAN",
        "🖥️ Master ESP32 (этот прибор)" to "🖥️ Master ESP32 (цей пристрій)",
        "🖥️ Master ESP32:" to "🖥️ Master ESP32:",
        "Плеєр" to "Плеєр",
        "Плейлист" to "Плейлист",
        "Логи" to "Логи",
        "BLE пристрої" to "BLE пристрої",
        "ESP32 Music Player" to "ESP32 Music Player"
    )

    private val english = mapOf(
        "AP Настройки (создание сети)" to "AP settings (create network)",
        "SSID (название сети)" to "SSID (network name)",
        "STA Настройки (подключение к сети)" to "STA settings (connect to network)",
        "Update over BLE" to "Update over BLE",
        "Works without Wi-Fi. Slow (1-5 min)." to "Works without Wi-Fi. Slow (1-5 min).",
        "Master передає прошивку Slave по UART. Не вимикайте живлення." to "Master streams firmware to Slave over UART. Do not power off.",
        "Выбрать и загрузить прошивку Master" to "Select and upload Master firmware",
        "Выбрать и загрузить прошивку Slave" to "Select and upload Slave firmware",
        "Від'єднати" to "Disconnect",
        "Детальные логи (эмодзи и баннеры)" to "Verbose logs (emoji and banners)",
        "ЗАРАЗ ГРАЄ" to "NOW PLAYING",
        "Завантаження..." to "Loading...",
        "Загрузить" to "Load",
        "Загрузка информации о системе..." to "Loading system info...",
        "Загрузка статистики CAN..." to "Loading CAN statistics...",
        "Закрыть" to "Close",
        "Застосовується одразу" to "Applied immediately",
        "Зовнішній вигляд" to "Appearance",
        "Информация" to "Information",
        "Керування ESP32 Music Player через Bluetooth: плеєр, плейлист, OTA та моніторинг." to "Control ESP32 Music Player over Bluetooth: player, playlist, OTA and monitoring.",
        "Логи компонентов:" to "Component logs:",
        "Логи отсутствуют" to "No logs",
        "Налаштування" to "Settings",
        "Обновить" to "Refresh",
        "Обновить логи" to "Refresh logs",
        "Обновление основной прошивки ESP32. Устройство будет перезагружено после обновления." to "ESP32 main firmware update. The device will reboot afterwards.",
        "Обновление прошивки Slave ESP32 через UART. Передача происходит блоками по 240 байт с проверкой CRC." to "Slave ESP32 firmware update over UART, transferred in 240-byte blocks with CRC check.",
        "Огляд" to "Overview",
        "Оновити Slave" to "Update Slave",
        "Отключить BLE и использовать Wi-Fi" to "Disconnect BLE and use Wi-Fi",
        "Оформлення інтерфейсу" to "Interface appearance",
        "Очистить логи" to "Clear logs",
        "Ошибка" to "Error",
        "Пароль" to "Password",
        "Перезагрузить устройство" to "Restart device",
        "Плейлист порожній" to "Playlist is empty",
        "Предыдущий трек" to "Previous track",
        "Пристрої не знайдені" to "No devices found",
        "Про застосунок" to "About",
        "Прогресс воспроизведения" to "Playback progress",
        "Підключ" to "Connect",
        "Підключення" to "Connection",
        "Підключити" to "Connect",
        "Підключіть USB-флешку з MP3" to "Connect a USB flash drive with MP3",
        "Режим Wi-Fi" to "Wi-Fi mode",
        "Сканувати" to "Scan",
        "Следующий трек" to "Next track",
        "Сохранить" to "Save",
        "Сохранить настройки" to "Save settings",
        "Темна тема" to "Dark theme",
        "Управление" to "Controls",
        "Уровень логов" to "Log level",
        "Успех" to "Success",
        "Версія 1.0.0" to "Version 1.0.0",
        "⚙️ Управление устройством" to "⚙️ Device control",
        "⚠️ Внимание: Перезагрузка прервет текущее воспроизведение" to "⚠️ Warning: restart will interrupt playback",
        "🎵 Slave ESP32 (Bluetooth модуль через UART)" to "🎵 Slave ESP32 (Bluetooth module via UART)",
        "🎵 Slave ESP32:" to "🎵 Slave ESP32:",
        "💻 Системная информация" to "💻 System information",
        "📊 Прогресс OTA" to "📊 OTA progress",
        "📋 Информация о версиях" to "📋 Version information",
        "📋 Системные логи" to "📋 System logs",
        "📝 Настройки логирования" to "📝 Logging settings",
        "📡 Статистика CAN" to "📡 CAN statistics",
        "🖥️ Master ESP32 (этот прибор)" to "🖥️ Master ESP32 (this device)",
        "🖥️ Master ESP32:" to "🖥️ Master ESP32:",
        "Плеєр" to "Player",
        "Плейлист" to "Playlist",
        "Логи" to "Logs",
        "BLE пристрої" to "BLE devices",
        "ESP32 Music Player" to "ESP32 Music Player",
        // ---- screens / errors added during full localisation ----
        "CAN: увімк." to "CAN: on",
        "CAN: вимк." to "CAN: off",
        "Відтворити" to "Play",
        "Пауза" to "Pause",
        "CAN-Sniffer: увімк." to "CAN sniffer: on",
        "Ціль: %s" to "Target: %s",
        "Блок: %d" to "Block: %d",
        "Підключено, сервіс не готовий" to "Connected, service not ready",
        "Підключено, плеєр готовий" to "Connected, player ready",
        "Підключено, вмикаю сповіщення…" to "Connected, enabling notifications…",
        "BLE підключено, шукаю сервіс плеєра…" to "BLE connected, looking for player service…",
        "Не підключено" to "Not connected",
        "🔍 Сканування..." to "🔍 Scanning...",
        "🔍 Сканувати" to "🔍 Scan",
        "📱 Знайдено пристроїв: %d" to "📱 Devices found: %d",
        "Підключено по BLE" to "Connected over BLE",
        "Немає з'єднання" to "No connection",
        "Не вибрано" to "Not selected",
        "Прошивку Master ESP32 завантажено" to "Master ESP32 firmware uploaded",
        "Slave ESP32 (через UART)" to "Slave ESP32 (over UART)",
        "Прошивку Slave ESP32 завантажено" to "Slave ESP32 firmware uploaded",
        "Пристрій перезавантажується…" to "Device is restarting…",
        "Помилка перезавантаження: %s" to "Restart failed: %s",
        "Помилка завантаження статусу: %s" to "Failed to load status: %s",
        "Помилка відтворення: %s" to "Playback failed: %s",
        "Помилка керування відтворенням: %s" to "Playback control failed: %s",
        "Помилка перемикання треку: %s" to "Track switch failed: %s",
        "Помилка перемикання режиму: %s" to "Mode switch failed: %s",
        "Помилка перемотування: %s" to "Seek failed: %s",
        "Помилка керування CAN-Sniffer: %s" to "CAN sniffer control failed: %s",
        "Помилка завантаження логів: %s" to "Failed to load logs: %s",
        "Помилка збереження налаштувань логування" to "Failed to save logging settings",
        "Немає даних" to "No data",
        "=== ІНФОРМАЦІЯ ПРО СИСТЕМУ ===" to "=== SYSTEM INFO ===",
        "Версія прошивки: %s" to "Firmware version: %s",
        "Дата збірки: %s %s" to "Build date: %s %s",
        "=== ПАМ'ЯТЬ ===" to "=== MEMORY ===",
        "Heap: %s вільно з %s" to "Heap: %s free of %s",
        "PSRAM: %s вільно з %s" to "PSRAM: %s free of %s",
        "=== ЧАС РОБОТИ ===" to "=== UPTIME ===",
        "Час роботи: %s" to "Uptime: %s",
        "=== CAN СТАТИСТИКА ===" to "=== CAN STATS ===",
        "Кадрів за секунду: %d" to "Frames per second: %d",
        "Байт за секунду: %s" to "Bytes per second: %s",
        "Всього кадрів: %d" to "Total frames: %d",
        "Всього байт: %s" to "Total bytes: %s",
        "Швидкість CAN: %d кбіт/с" to "CAN bitrate: %d kbit/s",
        "%d Б" to "%d B",
        "%d КБ" to "%d KB",
        "%d МБ" to "%d MB",
        "%d дн %d ч" to "%d d %d h",
        "%d ч %d хв" to "%d h %d min",
        "%d хв %d с" to "%d min %d s",
        "%d с" to "%d s",
        "Ford Player" to "Ford Player",
        "Керування Ford Player через Bluetooth: плеєр, плейлист, OTA та моніторинг." to "Control Ford Player over Bluetooth: player, playlist, OTA and monitoring.",
        "Про Ford Player" to "About Ford Player",
        "Сон та енергозбереження" to "Sleep & power",
        "Пристрій засинає після простою" to "The device sleeps when idle",
        "Режим сну" to "Sleep mode",
        "Вимкнено" to "Off",
        "Таймаут простою (с)" to "Idle timeout (s)",
        "Зберегти" to "Save",
        "Налаштування сну збережено" to "Sleep settings saved",
        "AP (Access Point) — ESP32 створює власну Wi-Fi мережу" to "AP (Access Point) — ESP32 creates its own Wi-Fi network",
        "STA (Station) — ESP32 підключається до наявної Wi-Fi мережі" to "STA (Station) — ESP32 joins an existing Wi-Fi network",
        "Невідомий режим" to "Unknown mode",
        "Приховати пароль" to "Hide password",
        "Показати пароль" to "Show password",
        "IP-адресу збережено: %s" to "IP address saved: %s",
        "Помилка завантаження налаштувань Wi-Fi: %s" to "Failed to load Wi-Fi settings: %s",
        "Налаштування збережено. Перезавантажте пристрій для застосування." to "Settings saved. Restart the device to apply.",
        "Помилка збереження налаштувань: %s" to "Failed to save settings: %s",
    )

    fun tr(text: String): String =
        if (isUkrainian) ukrainian[text] ?: text else english[text] ?: text

    fun notifMessagesChannel(): String =
        if (isUkrainian) "Повідомлення" else "Messages"
    fun notifMessagesChannelDesc(): String =
        if (isUkrainian) "Помилки та статуси Ford Player" else "Ford Player errors and statuses"
    fun notifErrorTitle(): String = if (isUkrainian) "Помилка" else "Error"
    fun notifInfoTitle(): String = "Ford Player"
    fun notifConnected(): String =
        if (isUkrainian) "Підключено до Ford Player" else "Connected to Ford Player"
    fun notifDisconnected(): String =
        if (isUkrainian) "З'єднання з Ford Player втрачено" else "Ford Player connection lost"
    fun playbackChannel(): String = if (isUkrainian) "Відтворення" else "Playback"
    fun playbackChannelDesc(): String =
        if (isUkrainian) "Міні-плеєр Ford Player" else "Ford Player mini player"
    fun playbackPlay(): String = if (isUkrainian) "Відтворити" else "Play"
    fun playbackPause(): String = if (isUkrainian) "Пауза" else "Pause"
    fun playbackNext(): String = if (isUkrainian) "Далі" else "Next"
    fun playbackPrevious(): String = if (isUkrainian) "Назад" else "Previous"
    fun playbackPlaying(): String = if (isUkrainian) "Відтворюється" else "Playing"
    fun playbackPaused(): String = if (isUkrainian) "Пауза" else "Paused"
    fun notifTitleFallback(): String = "Ford Player"
    fun trackTitle(n: Int): String = if (isUkrainian) "Трек $n" else "Track $n"

    fun logGroupsTitle(): String =
        if (isUkrainian) "Логи за модулями:" else "Logs by module:"
    fun logGroupPlayer(): String =
        if (isUkrainian) "🎵 Плеєр (аудіо, плейлист)" else "🎵 Player (audio, playlist)"
    fun logGroupConnect(): String =
        if (isUkrainian) "📡 Зв'язок (BLE, Wi-Fi, Slave)" else "📡 Connectivity (BLE, Wi-Fi, slave)"
    fun logGroupOta(): String =
        if (isUkrainian) "💾 OTA (веб, UART)" else "💾 OTA (web, UART)"
    fun logGroupCar(): String =
        if (isUkrainian) "🚗 Авто (CAN, ELM327)" else "🚗 Car (CAN, ELM327)"
    fun logGroupSystem(): String =
        if (isUkrainian) "⚙️ Система (USB, сон, консоль, NVS)" else "⚙️ System (USB, sleep, console, NVS)"

    fun otaChannelProgress(): String =
        if (isUkrainian) "Оновлення прошивки" else "Firmware update"
    fun otaChannelProgressDesc(): String =
        if (isUkrainian) "Прогрес оновлення ESP32" else "ESP32 update progress"
    fun otaChannelResult(): String =
        if (isUkrainian) "Результат оновлення" else "Update result"
    fun otaChannelResultDesc(): String =
        if (isUkrainian) "Завершення або помилка оновлення" else "Update finished or failed"
    fun otaNotificationTitle(): String =
        if (isUkrainian) "Оновлення ESP32" else "ESP32 update"
    fun otaPreparing(): String =
        if (isUkrainian) "Підготовка…" else "Preparing…"
    fun otaProgressText(label: String, percent: Int, speed: Long, eta: Long): String {
        val text = StringBuilder("$label · $percent%")
        if (speed > 0) text.append(" · ").append(speedText(speed))
        if (eta in 1..86399) text.append(" · ").append(etaText(eta))
        return text.toString()
    }
    fun sizeText(bytes: Long): String = humanAmount(bytes)
    fun speedText(bytesPerSec: Long): String =
        if (isUkrainian) "${humanAmount(bytesPerSec)}/с" else "${humanAmount(bytesPerSec)}/s"
    fun etaText(seconds: Long): String =
        if (isUkrainian) "залишилось ~${durationText(seconds)}"
        else "~${durationText(seconds)} left"
    fun otaPhaseName(apply: Boolean): String =
        if (isUkrainian) (if (apply) "запис у flash" else "передача")
        else (if (apply) "writing flash" else "sending")
    fun durationText(seconds: Long): String {
        val total = seconds.coerceAtLeast(0)
        val minutes = total / 60
        val rest = total % 60
        return if (isUkrainian) {
            if (minutes > 0) "$minutes хв $rest с" else "$rest с"
        } else {
            if (minutes > 0) "$minutes min $rest s" else "$rest s"
        }
    }
    fun humanAmount(bytes: Long): String {
        val value = bytes.coerceAtLeast(0)
        val kb = value / 1024.0
        return when {
            kb < 1.0 -> "$value ${if (isUkrainian) "Б" else "B"}"
            kb < 1024.0 -> String.format(Locale.US, "%.1f %s", kb, if (isUkrainian) "КБ" else "KB")
            else -> String.format(Locale.US, "%.2f %s", kb / 1024.0, if (isUkrainian) "МБ" else "MB")
        }
    }
    fun otaDoneTitle(): String =
        if (isUkrainian) "Оновлення завершено" else "Update complete"
    fun otaFailedTitle(): String =
        if (isUkrainian) "Оновлення не вдалося" else "Update failed"
    fun otaDoneMaster(): String =
        if (isUkrainian) "Прошивку Master оновлено, пристрій перезавантажується"
        else "Master firmware flashed, device is restarting"
    fun otaDoneSlave(): String =
        if (isUkrainian) "Прошивку Slave оновлено по UART"
        else "Slave firmware updated over UART"

    fun otaErrorMaster(detail: String): String =
        if (isUkrainian) "\u041f\u043e\u043c\u0438\u043b\u043a\u0430 \u043e\u043d\u043e\u0432\u043b\u0435\u043d\u043d\u044f Master: $detail"
        else "Master update failed: $detail"
    fun otaErrorSlave(detail: String): String =
        if (isUkrainian) "\u041f\u043e\u043c\u0438\u043b\u043a\u0430 \u043e\u043d\u043e\u0432\u043b\u0435\u043d\u043d\u044f Slave: $detail"
        else "Slave update failed: $detail"
    fun otaErrorBle(detail: String): String =
        if (isUkrainian) "\u041f\u043e\u043c\u0438\u043b\u043a\u0430 \u043e\u043d\u043e\u0432\u043b\u0435\u043d\u043d\u044f \u043f\u043e BLE: $detail"
        else "BLE update failed: $detail"
    fun fileReadError(detail: String): String =
        if (isUkrainian) "\u041d\u0435 \u0432\u0434\u0430\u043b\u043e\u0441\u044f \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u0438 \u0444\u0430\u0439\u043b: $detail"
        else "Could not read file: $detail"

    /** Version strings reported by the device when no slave/peer is present. */
    fun isDisconnectedText(text: String): Boolean =
        text.contains("Не підключ", true) ||
            text.contains("Не подключ", true) ||
            text.contains("Not connected", true)

    fun trackName(n: Int): String = if (isUkrainian) "Трек $n" else "Track $n"
    fun trackOf(n: Int, total: Int): String =
        if (isUkrainian) "Трек $n з $total" else "Track $n of $total"
    fun playlistTitle(count: Int): String =
        if (isUkrainian) "Плейлист · $count треків" else "Playlist · $count tracks"
    fun modeText(mode: String): String =
        if (isUkrainian) "Режим: $mode" else "Mode: $mode"
}

fun tr(text: String): String = Loc.tr(text)
