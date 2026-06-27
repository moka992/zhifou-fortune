# 知否运势

知否运势是一款 Android 运势与占卜应用，提供每日运势、三枚铜钱占卜、答案之书、AI 解读、语音输入、历史记录和日程事项管理等功能。

## Features

- Daily fortune readings
- Three-coin I Ching divination
- Answer Book oracle prompts
- AI-powered interpretation through an OpenAI-compatible endpoint
- Offline Chinese-English voice input for divination questions
- Dice cup roller with touch reveal, shake-to-roll support, local sound feedback, and selectable D4/D6/D8/D10/D12/D20 dice
- Local reading history
- Offline solar/lunar calendar with swipe navigation, solar terms, traditional festivals, and date-specific schedules
- Local profile area with reading history and app settings

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- lunar-java for offline Chinese calendar calculations
- Sherpa-ONNX with an INT8 bilingual Zipformer model
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
- `RECORD_AUDIO`: used for on-device voice input. Microphone audio is processed locally and is not uploaded by the speech recognition feature.

## Offline Speech Recognition

Voice input runs locally with a bundled Chinese-English streaming model. Hold the microphone in the question field to speak, release to insert the recognized text, or slide up before releasing to cancel. No speech service account or network connection is required.

The current native package targets `arm64-v8a`, which covers the supported Android phone range while keeping the APK smaller than a universal build.

Third-party licenses and model attribution are listed in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## License

No license has been specified yet.
