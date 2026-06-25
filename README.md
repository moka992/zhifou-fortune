# 知否运势

知否运势是一款面向个人用户的 Android 运势与占卜应用。应用提供每日运势、三枚铜钱占卜、答案之书、历史记录和本地日程事项管理，核心功能可在手机端独立完成。

## Features

- Daily fortune reading based on date and optional user profile keywords
- Three-coin I Ching divination with main hexagram, changed hexagram, moving lines and advice
- Answer Book style short oracle prompts
- Optional AI interpretation for divination results through an OpenAI-compatible chat completion endpoint
- Voice question input through Android system speech recognition
- Local history for previous fortune and divination records
- Local schedule and task notes with complete/delete actions
- Basic personalization through nickname and long-term keywords

## Privacy

- Core records are stored locally on the device.
- The current version does not require account sign-in.
- Microphone permission is requested only when using voice input.
- Internet access is used only when AI interpretation is configured and requested.
- The current version does not request Bluetooth, location or contacts permissions.
- API keys, signing keys, local SDK paths and build outputs are excluded from version control.
- AI provider settings are stored locally on the device.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Gradle Plugin 8.6.1
- minSdk 26 / targetSdk 35
