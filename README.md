# 知否运势

知否运势是一款 Android 运势与占卜应用，提供每日运势、三枚铜钱占卜、答案之书、AI 解读、语音输入、历史记录和日程事项管理等功能。

## Features

- Daily fortune readings
- Three-coin I Ching divination
- Answer Book oracle prompts
- AI-powered interpretation through an OpenAI-compatible endpoint
- Offline multilingual voice input with Chinese-English mixed-language support
- Optional OpenAI-compatible audio transcription backend, disabled by default
- Dice cup roller with touch reveal, shake-to-roll support, local sound feedback, and selectable D4/D6/D8/D10/D12/D20 dice
- Local reading history
- Offline solar/lunar calendar with swipe navigation, solar terms, traditional festivals, and date-specific schedules
- Local profile area with reading history and app settings

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- lunar-java for offline Chinese calendar calculations
- Sherpa-ONNX with an INT8 Whisper tiny multilingual model
- Android Gradle Plugin 8.6.1
- minSdk 26
- targetSdk 35

## Getting Started

### Prerequisites

- Android Studio
- JDK 17
- Android SDK Platform 35

### Build

Download the speech model assets, which are intentionally excluded from Git:

```bash
./scripts/fetch_whisper_model.sh
```

Then build the application:

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

An optional OpenAI-compatible audio transcription endpoint can be configured separately. It is disabled by default and no API key is included in the source tree or APK.

## Permissions

The app uses the following Android permissions:

- `INTERNET`: used for optional AI interpretation and optional cloud speech transcription.
- `RECORD_AUDIO`: used for voice input. Audio remains on the device in the default offline mode; it is uploaded only when the user explicitly enables and configures cloud speech transcription.

## Offline Speech Recognition

Voice input runs locally by default with an INT8 Whisper tiny model bundled into the APK at build time. The model binaries are excluded from Git because of their size and can be restored with `scripts/fetch_whisper_model.sh`. Long-press the question field to record, release to transcribe in the background, or slide up before releasing to cancel and return to text editing. No speech service account or network connection is required for offline mode after installation.

The current native package targets `arm64-v8a`, which covers the supported Android phone range while keeping the APK smaller than a universal build.

Third-party licenses and model attribution are listed in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## License

No license has been specified yet.
