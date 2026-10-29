# Ford Player — Android

Застосунок керування системою **Ford Player** (ESP32-S3 + Bluetooth-модуль).
Працює **тільки через BLE** — Wi-Fi у застосунку не використовується.

<p align="center">
  <b>Плеєр · Плейлист · OTA · Логи</b><br>
  <i>UK / EN інтерфейс, міні-плеєр у шторці, OTA Master і Slave</i>
</p>


## Довідка та пов'язані репозиторії

| Компонент | Репозиторій |
|---|---|
| Master (ESP32-S3) | https://github.com/freebsdmax/Ford-DensO-mp3-master |
| Slave (ESP32 Bluetooth) | https://github.com/freebsdmax/Ford-DensO-bluetooth-slave |
| Android-застосунок | https://github.com/freebsdmax/Esp32_Android_app |

- Повна довідка по системі: [HELP.md](https://github.com/freebsdmax/Ford-DensO-mp3-master/blob/main/HELP.md)
- Довідка цього компонента: [HELP.md](HELP.md)

---

## Вимоги

| | |
|---|---|
| Android | **8.0 (API 26)** і вище |
| Дозволи | Bluetooth (Scan/Connect), **сповіщення** (Android 13+) |
| Пристрій | Ford Player Master з прошивкою, що підтримує BLE-RPC |

При першому запуску застосунок попросить дозвіл на сповіщення — без нього
не буде ні міні-плеєра, ні повідомлень про помилки.

## Що вміє

### 🎵 Плеєр
- відтворення / пауза, наступний / попередній трек, перемотування;
- назва треку, позиція, режим (USB / external-Bluetooth);
- індикатор з'єднання у верхній панелі.

### 📜 Плейлист
- список треків з флешки, вибір треку тапом, поточна позиція виділена.

### 💾 OTA
- **Master**: вибір `.bin` → завантаження по BLE → пристрій перезавантажується;
- **Slave**: вибір `.bin` → образ іде на Master, далі по UART у Slave;
- прогрес, **швидкість (КБ/с)** і **приблизний залишок часу**;
- фази окремо: *передача* та *запис у flash*;
- при збої — **конкретна причина** (не просто «помилка»):
  `Device failed: incomplete upload`, `File OTA rejected at 223200: …`,
  `BLE MTU could not be restored`, `Device lost the update session`.

### 📋 Логи
- системна інформація (прошивка, heap, PSRAM, uptime);
- статистика CAN;
- **логи за модулями** (5 перемикачів): 🎵 Плеєр · 📡 Зв'язок · 💾 OTA · 🚗 Авто · ⚙️ Система;
- рівень логування та verbose-режим;
- перегляд логів пристрою.

### 🔔 Сповіщення
- **міні-плеєр** у шторці (як у YouTube Music): назва треку, ⏮ / ⏯ / ⏭,
  керує платою по BLE; живе, поки є з'єднання;
- усі повідомлення застосунку дублюються в шторку (помилки, успіх, стан з'єднання);
- прогрес і результат OTA — окремими сповіщеннями.

## Збірка

Потрібні **JDK 17** і **Android SDK 34**.

```powershell
cd D:\emulators\Ford-DensO-mp3-master\AndroidPlayer

# SDK path (один раз)
# local.properties: sdk.dir=C\:\\Users\\<user>\\AppData\\Local\\Android\\Sdk

.\gradlew.bat assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

Встановлення:

```powershell
adb uninstall com.example.androidplayer
adb install app\build\outputs\apk\debug\app-debug.apk
```

> `versionCode` завжди `1`, тому при оновленні краще робити `uninstall` —
> інакше Android може залишити стару версію.

## Архітектура

```
ui/
  screens/      Player, Playlist, Ota, SystemMonitor (логи), Settings, BleDiscovery
  viewmodel/    PlayerVM, OtaVM, SystemMonitorVM, SettingsVM
  i18n/Loc.kt   словник UK/EN (рядки + форматери)
  notify/       AppNotify (повідомлення в шторку)
service/
  PlaybackService.kt   MediaSession + MediaStyle (міні-плеєр)
ota/
  OtaNotifications.kt, OtaUploadService.kt   прогрес/результат OTA
data/
  ble/BlePlayerClient.kt        GATT-клієнт, RPC, OTA-кадри, MTU
  repository/Esp32PlayerRepository.kt
  model/                        моделі стану й налаштувань
```

- **MVVM + Hilt**, Kotlin Coroutines + Flow, Jetpack Compose (Material 3).
- Транспорт — `BlePlayerClient` (синглтон), репозиторій серіалізує OTA м'ютексом.

## BLE-протокол (що використовує застосунок)

Сервіс `11000000-0000-1000-8000-00805f9b34fb`:

| Характеристика | UUID | Призначення |
|---|---|---|
| Command | `11010100-…` | запити (write / write-no-response) |
| Status | `11010200-…` | стан плеєра (notify) |
| Playlist | `11010300-…` | плейлист (notify) |
| Logs | `11010400-…` | відповіді RPC (notify) |

- Текстові запити: `rpc:<команда>[:<json>]`, відповідь — JSON.
- MTU: запитується 512 (корисно 509 Б).
- **OTA Master/Slave**: `file.begin` → бінарні кадри
  `[0xA5][0x01][offset:4][len:2][data]` через Write Without Response → `file.finish`.
- Застосунок сам відновлює MTU/з'єднання, якщо телефон почав різати записи.

## Мова

Інтерфейс — **українська**, якщо системна мова `uk`, інакше **англійська**.
Усі рядки проходять через `Loc.tr()`; дати/розміри/час форматуються окремими
хелперами (`sizeText`, `speedText`, `etaText`, `durationText`).

## Релізи

GitHub Actions (`.github/workflows/release.yml`) збирає APK і публікує реліз
при пуші тега:

```bash
git add .
git commit -m "release: v1.1.0"
git push
git tag v1.1.0
git push origin v1.1.0
```

Артефакт: `Ford-Player-vX.Y.Z.apk` (debug-підпис).

```bash
adb uninstall com.example.androidplayer
adb install Ford-Player-*.apk
```

> `local.properties` у CI видаляється, щоб Gradle використав `ANDROID_HOME`
> раннера. Для підписаного релізу потрібен keystore у GitHub Secrets.


## Відомі обмеження

- Wi-Fi-режим у застосунку відсутній (керування лише BLE).
- Гучність і seek у external-режимі (BT зі Slave) не працюють — це керує телефон.
- OTA йде lock-step кадрами: швидкість залежить від інтервалу з'єднання
  (типово 20–50 КБ/с).
- Потрібен дозвіл на сповіщення, інакше міні-плеєр не покажеться.
