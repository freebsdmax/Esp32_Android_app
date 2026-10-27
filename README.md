# ESP32 Music Player Control (Android App)

Android-приложение для управления MP3-плеером на базе ESP32 (проект **Ford-DensO-mp3**).

## Возможности

- **Плеер** — управление воспроизведением (play/pause/next/prev), переключение треков из плейлиста, перемотка по таймлайну.
- **Режимы** — переключение между режимом USB/носителя и внешним I2S-входом.
- **CAN-Sniffer** — включение/выключение Wi-Fi CAN-Sniffer с отображением состояния.
- **Wi-Fi** — настройка подключения ESP32 в режимах AP/STA, сохранение SSID/пароля.
- **OTA обновления** — обновление прошивки Master ESP32 и Slave ESP32 через UART по блокам.
- **Системный монитор** — информация о системе, статистика CAN, просмотр и управление логами.
- **BLE** — поддержка подключения по BLE (при наличии на устройстве).

## Скриншоты

*Скриншоты будут добавлены позже.*

## Технологии

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Hilt** — внедрение зависимостей
- **Retrofit/OkHttp** — работа по HTTP
- **Coroutines/Flow** — асинхронщина
- **Navigation Compose** — навигация
- **Timber** — логирование
- **BLE** (Android Bluetooth API) — для подключения по BLE

## Сборка

### Требования

- **Android Studio** (рекомендуется последняя стабильная) или **Android SDK Command Line Tools**
- **JDK 17**
- **Android SDK** (compileSdk 34, minSdk 26)

### Сборка через Android Studio

1. Открыть `D:\emulators\Ford-DensO-mp3-master\AndroidPlayer` в Android Studio.
2. Дождаться синхронизации Gradle.
3. `Build > Build Bundle(s) / APK(s) > Build APK(s)` или `Run` на устройстве/эмуляторе.

### Сборка через консоль

```bash
cd D:\emulators\Ford-DensO-mp3-master\AndroidPlayer
./gradlew.bat assembleDebug
# или
./gradlew.bat installDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

### Настройка SDK (при необходимости)

Создай `local.properties` в корне модуля `AndroidPlayer/`:

```properties
sdk.dir=C:\\Users\\<ИмяПользователя>\\AppData\\Local\\Android\\Sdk
```

## Подключение к устройству

1. Подключи телефон к той же Wi-Fi-сети, что и ESP32, или подключись к AP ESP32 (`ESP32-MusicPlayer` по умолчанию).
2. В приложении в **Настройках** можно поменять IP-адрес ESP32 (по умолчtip http://192.168.4.1).
3. Либо используй **BLE** для подключения к устройству.

## Структура проекта

```text
AndroidPlayer/
├── app/src/main/java/com/example/androidplayer/
│   ├── data/        # Модели, сеть, репозиторий, BLE
│   ├── di/          # Hilt-модули
│   ├── ui/          # Compose-экраны, темы, навигация, ViewModel
│   └── MainActivity.kt
└── app/build.gradle
```

## Версия

- **App versionName:** 1.0.0
- **App versionCode:** 1

## Лицензия

Проект предназначен для личного использования. Авторские права на оригинальный проект см. в корневом репозитории **Ford-DensO-mp3**.
