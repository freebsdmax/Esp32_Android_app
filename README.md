# ESP32 Music Player Control (Android-додаток)

Android-додаток для керування MP3-плеєром на базі ESP32 (проєкт **Ford-DensO-mp3**).

## Можливості

- **Плеєр** — керування відтворенням (play/pause/наступний/попередній), вибір треку з плейлиста, перемотка по треку.
- **Режими** — перемикання між режимом USB/носія та зовнішнім I2S-входом.
- **CAN-Sniffer** — увімкнення/вимкнення Wi-Fi CAN-Sniffer із відображенням стану.
- **Wi-Fi** — налаштування підключення ESP32 у режимах AP/STA, збереження SSID та пароля.
- **OTA-оновлення** — оновлення прошивки Master ESP32 та Slave ESP32 через UART блоками по 240 байт.
- **Системний монітор** — інформація про систему, статистика CAN, перегляд та керування логами.
- **BLE** — підтримка підключення по BLE (за наявності на пристрої).

## Технології

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Hilt** — впровадження залежностей
- **Retrofit/OkHttp** — робота по HTTP
- **Coroutines/Flow** — асинхронна робота
- **Navigation Compose** — навігація
- **Timber** — логування
- **BLE** (Android Bluetooth API) — підключення по BLE

## Збірка

### Вимоги

- **Android Studio** (рекомендовано останню стабільну версію) або **Android SDK Command Line Tools**
- **JDK 17**
- **Android SDK** (compileSdk 34, minSdk 26)

### Збірка через Android Studio

1. Відкрити `D:\emulators\Ford-DensO-mp3-master\AndroidPlayer` в Android Studio.
2. Дочекатись синхронізації Gradle.
3. `Build > Build Bundle(s) / APK(s) > Build APK(s)` або запустити `Run` на пристрої/емуляторі.

### Збірка через консоль

```bash
cd D:\emulators\Ford-DensO-mp3-master\AndroidPlayer
.\gradlew.bat assembleDebug
# або
.\gradlew.bat installDebug
```

Готовий APK: `app/build/outputs/apk/debug/app-debug.apk`

### Налаштування SDK (за потреби)

Створи файл `local.properties` в корені модуля `AndroidPlayer/`:

```properties
sdk.dir=C:\\Users\\<Ім'яКористувача>\\AppData\\Local\\Android\\Sdk
```

## Підключення до пристрою

1. Підключи телефон до тієї ж Wi-Fi-мережі, що й ESP32, або підключись до AP ESP32 (за замовчуванням `ESP32-MusicPlayer`).
2. У додатку в розділі **Налаштування** можна змінити IP-адресу ESP32 (за замовчуванням `http://192.168.4.1`).
3. Також можна використати **BLE** для підключення до пристрою.

## Структура проєкту

```text
AndroidPlayer/
├── app/src/main/java/com/example/androidplayer/
│   ├── data/        # Моделі, мережа, репозиторій, BLE
│   ├── di/          # Hilt-модулі
│   ├── ui/          # Compose-екрани, теми, навігація, ViewModel
│   └── MainActivity.kt
└── app/build.gradle
```

## Версія

- **App versionName:** 1.0.0
- **App versionCode:** 1

## Ліцензія

Проєкт призначений для особистого використання. Авторські права на оригінальний проєкт дивись у кореневому репозиторії **Ford-DensO-mp3**.
