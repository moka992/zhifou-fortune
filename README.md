# 知否运势

知否运势是一款 Android 运势与占卜应用，提供每日运势、三枚铜钱占卜、答案之书、AI 解读、语音输入、历史记录和日程事项管理等功能。

## Features

- Daily fortune readings
- Three-coin I Ching divination
- Answer Book oracle prompts
- AI-powered interpretation through an OpenAI-compatible endpoint
- Voice input for divination questions
- Animated dice game with standard dice expressions such as `d6`, `2d6`, `d20` and `d%`
- Local reading history
- Local schedule and task notes
- Basic user profile settings

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Gradle Plugin 8.6.1
- minSdk 26
- targetSdk 35

## Getting Started

### Prerequisites

- Android Studio
- JDK 17
- Android SDK Platform 35

### Build

```bash
./gradlew :app:assembleDebug
```

The debug APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Configuration

AI interpretation can be configured inside the app settings with:

- API key
- Model name
- OpenAI-compatible chat completions endpoint

## Permissions

The app uses the following Android permissions:

- `INTERNET`: used for optional AI interpretation.
- `RECORD_AUDIO`: used for voice input.

## License

No license has been specified yet.
