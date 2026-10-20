# Product and Developer Guide

[README](../README.md)

**Start here:** [download the Android evaluation APK](https://github.com/moka992/zhifou-fortune/releases/latest), [watch real-device demos](showcase.md#english), or [try the no-key local skill demo](https://github.com/moka992/zhifou-oracle-skills#try-it-in-a-minute).

The Android interface is primarily Simplified Chinese. Core draws, calendars, schedules, and tools do not require AI configuration. The optional host model may charge for API use. The APK remains debug-signed, and the application source is public for inspection, not licensed as open source.

### About

Zhifou Fortune is a native Android app that brings together the traditional Chinese calendar, I Ching coin divination, Rider-Waite-Smith tarot, a Book of Answers, schedules, and several tactile utility tools. Calendar calculations, baseline readings, divination mechanics, schedules, and utilities work offline. AI interpretation and cloud speech transcription are optional and require a service configured by the user.

The project aims to present traditional material with modern product standards: sources should remain identifiable, conclusions should stay measured, and cultural references should never be framed as guaranteed predictions or fear-based advice.

> The current version is `v1.1.1`. Fortune, almanac, and divination content is intended for cultural study, personal reflection, and entertainment. It is not medical, legal, financial, or professional advice. The downloadable APK remains debug-signed; see the distribution notes below.

### What's new in v1.1.1

Four-card browsing now uses a card-center ratchet rather than sub-card distance pulses. Position captions stay beneath flying cards, fade out during reveals, and return after settling. Draws, references and AI behavior are unchanged. See the [v1.1.1 notes](releases/v1.1.1.md).

### What's new in v1.1.0

- Added four-card tarot with manual selection, offline references, a slower reveal, an independent AI interpretation flow, and documented method sources.
- Refined original 3D coins, casting-coin artwork and shadows, rigid-body dice motion, strength-sensitive shaking, and tool layouts.
- Added distinct tactile cues for cards, coins, dice, wheel detents, pickers, toggles, voice cancellation, and confirmations, selected according to device capabilities.
- Added AI and cloud-transcription provider/model choices alongside custom configuration and connection tests; refined interpretation wording and long-text input.
- Improved landscape, compact windows, larger fonts, system insets, Back navigation, and scroll retention. Animated icons share their static geometry at resting endpoints.
- Expanded geometry, motion, tarot, input, configuration, and haptic tests. See the [v1.1.0 notes](releases/v1.1.0.md) for verification and limitations.

See [Interface Design](ui-redesign.md) for the visual baseline and [Haptic Design](haptic-design.md) for current tactile behavior.

### Features

#### Daily reading and offline almanac

- Stable daily guidance based on date, nickname, birthday, and optional focus keywords.
- Daily rhythm is calculated locally from everyday-almanac and seasonal rules. Category guidance separates classical excerpts from modern advice. Data Sources & Notes in settings brings together public sources, calculation principles, privacy information, and disclaimers. The index is a cultural reference, not a probability of good fortune. See [daily rhythm and guidance](daily-rhythm.md) for details.
- Offline Gregorian and lunar dates, sexagenary cycle, zodiac, NaYin, solar terms, and traditional festivals.
- Directions for the Joy, Fortune, Wealth, Yang Noble, and Yin Noble deities, alongside full almanac entries.
- Detailed day view with day officers, Peng Zu taboos, clashes, fetal deity positions, void branches, and nine-star information.
- Career, relationship, and finance summaries use a curated modern-life allowlist. Missing or unsuitable entries fall back to neutral guidance.

#### Divination and interpretation

- **Three-coin I Ching**: six lines generated from individual three-coin casts, with full cast records, moving lines, primary hexagram, and transformed hexagram.
- **Offline I Ching corpus**: all 64 hexagrams, 384 line texts, Judgments, Tuan, and Xiang material.
- **Book of Answers**: 100 offline responses.
- **Rider-Waite tarot**: the complete 78-card public-domain Rider-Waite-Smith deck, upright and reversed references, and a ten-card Celtic spread. Drawing, card flips, and baseline spread interpretation work offline.
- **AI interpretation**: optional OpenAI-compatible Chat Completions support for question-aware readings.
- **Traditional culture chat**: conversations about the I Ching, Dao De Jing, and related Chinese religious and intellectual history.
- Daily home insights and divination analysis use separate prompts, caches, and output limits.

#### Speech input

- Offline recognition with Sherpa-ONNX and an INT8 Whisper Tiny model.
- Chinese, English, and mixed-language input.
- Hold the question field to record, release to transcribe, and slide upward to cancel.
- Optional cloud transcription through SiliconFlow, Groq, OpenAI, Qwen, Mistral, and ElevenLabs, with provider/model presets, custom configuration, and a manual connection test. Disabled by default. See [speech provider configuration](speech-providers.md) for free-tier conditions and endpoints.

#### Calendar and schedules

- Month views show only dates from the current month and automatically use four, five, or six rows.
- Horizontal swipes change month; vertical swipes change year. Wheel pickers support both fast travel and precise selection.
- Double-tap a date to open its full offline almanac and schedule list.
- Schedule fields include notes, start and end time, location, participants, color, background image, and pinning.
- Schedule Memorial provides countdowns, elapsed-day tracking, and multiple sort orders.

#### Utility tools

- **Four-card tarot**: manually pick four cards from the complete RWS deck for situation, resistance, resources, and conditional direction. Optional questions and reversals, offline meanings, and explicit opt-in AI interpretation. [Method and sources](four-card-tarot.md).
- **Rider-Waite tarot Wiki**: an offline, searchable reference for all 78 cards, organized by Major Arcana and suit, with artwork notes, spread usage, upright and reversed meanings, historical context, and comparisons with related cards.
- **Polyhedral dice**: one to six D4, D6, D8, D10, D12, or D20 dice with Bullet rigid-body motion, strength-sensitive shake input, sound, and collision haptics.
- **Wheel**: custom segments, result history, shake-to-spin, and segmented haptic feedback.
- **Coin toss**: one to ten original double-sided 3D coins, shake-to-toss, and independent 50:50 outcomes.

### Technology

| Area | Stack |
| --- | --- |
| Language and build | Kotlin, Java 17, Gradle |
| UI | Jetpack Compose, Material 3 |
| App structure | Single Activity, ViewModel, Kotlin Coroutines, local Repository |
| Calendar engine | `lunar-java` |
| Offline speech | Sherpa-ONNX, Whisper Tiny INT8 |
| Networking | OkHttp, OpenAI-compatible Chat Completions |
| Local storage | SharedPreferences, JSON, Android document URIs |
| Minimum Android | Android 8.0 / API 26 |
| Target Android | API 36 |
| Current ABI | `arm64-v8a` |

### Download

Test APKs are available from [GitHub Releases](https://github.com/moka992/zhifou-fortune/releases). Release APKs are signed with the Android debug key and are intended for evaluation and development testing, not production store distribution.

The APK is larger than a typical Android app because it contains the offline speech model. The current build targets `arm64-v8a` only.

### Build from source

You need the current stable Android Studio, JDK 17, Android SDK Platform 36, Git, and `curl`.

Whisper model binaries are intentionally excluded from Git. Download and verify them before building:

```bash
git clone https://github.com/moka992/zhifou-fortune.git
cd zhifou-fortune

./scripts/fetch_whisper_model.sh
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

The debug APK will be written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install while preserving existing app data:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### AI configuration

Under **My > Settings**, choose a provider and model, enter the corresponding API key, and save. Presets cover DeepSeek, Qwen, Zhipu GLM, Kimi, MiniMax, SiliconFlow, OpenAI, Gemini, and Claude. **Custom** retains editable OpenAI-compatible endpoint and model ID fields. Existing configurations remain unchanged as custom settings. See [AI provider configuration](ai-providers.md) for endpoints, regional choices, models, and official references.

After a changed configuration is saved, the app runs one low-cost connection check and stores an irreversible configuration fingerprint.

- Unchanged settings are not retested whenever the app opens.
- Missing connectivity, invalid settings, and incomplete configuration are reported separately.
- A manual **Connection test** remains available at any time.
- Home-screen AI insights are cached by date and category, so reopening them does not call the API again.

No API key is bundled with the source or APK. Data handling by an external AI service is governed by the provider selected by the user.

For Oracle's ten-card AI interpretation, the app sends the current question, the ten drawn cards, and their orientations to the configured model service. User-entered nickname, birthday, focus keywords, and the current offline almanac may be included as secondary context. Schedules, history, recordings, and unrelated local data are not included in tarot requests.

The separate four-card utility uses its own prompt. Only an explicit AI request sends that reading's question, four cards and orientations, and optional nickname and focus keywords. It does not send birthday or almanac data. Successful text is saved with the reading; reopening it does not make another request.

### Permissions and privacy

| Permission | Purpose | Required for offline core features |
| --- | --- | --- |
| `INTERNET` | Optional AI, chat, and cloud transcription | No |
| `ACCESS_NETWORK_STATE` | Distinguish offline state from configuration errors | No |
| `RECORD_AUDIO` | Hold-to-talk speech input | No |
| `VIBRATE` | Navigation, selection, divination, and tool haptics; switchable in settings | No |

- No advertising SDK, analytics SDK, or third-party user tracking is included.
- Profiles, schedules, history, chat, and service settings remain on the device and are excluded from system cloud backup.
- Audio is not uploaded in offline speech mode.
- Cloud AI and speech features run only after explicit user configuration.
- Schedule images are granted through Android's document picker; the app does not request broad media-library access.
- `local.properties`, signing files, API keys, offline speech-model binaries, and APKs must not be committed to Git. The original coin's runtime GLB is included. Blender authoring files can retain workstation paths, so they stay local and can be recreated using the modeling scripts.

Remove personal data, chat content, credentials, and device identifiers before posting logs or opening an issue.

### Project layout

```text
app/src/main/java/com/zhifou/fortune/
├── MainActivity.kt              # App shell, Compose screens, and network clients
├── DailyFortune.kt              # Daily reading and offline almanac summary
├── DailyInsights.kt             # Safe offline rules for three home insight areas
├── AlmanacDayDetail.kt          # Detailed day almanac
├── ZhouyiDivination.kt          # Hexagram and changing-line rules
├── ZhouyiClassics.kt            # Offline I Ching corpus
├── AnswerBook.kt                # Book of Answers data
├── TarotDeck.kt                 # 78-card RWS deck, spread rules, and offline meanings
├── OfflineSpeechRecognizer.kt   # Offline speech recognition
├── CloudSpeechTranscriber.kt    # Optional cloud transcription
├── ScheduleModels.kt            # Schedule models and sorting
└── ScheduleScreens.kt           # Schedule editor, memorial, and detail screens

scripts/fetch_whisper_model.sh    # Downloads and verifies the speech model
tools/generate_zhouyi_corpus.py  # Generates and validates the I Ching corpus
```

### Tests

The JVM test suite covers coin-cast structure, changing hexagrams, the complete 78-card tarot deck and unique draws, corpus integrity, almanac rules, daily summaries, sensitive-entry isolation, AI prompt boundaries, configuration fingerprints, schedules, recommendations, timeline state, and coin animation geometry.

```bash
./gradlew :app:testDebugUnitTest
```

### Third-party software

Dependency, model, artwork, source-text, and license details are documented in [THIRD_PARTY_NOTICES.md](../THIRD_PARTY_NOTICES.md). Major components include AndroidX, Jetpack Compose, `lunar-java`, Sherpa-ONNX, OpenAI Whisper Tiny, the Kanseki Repository I Ching text, and the public-domain Rider-Waite-Smith deck and *The Pictorial Key to the Tarot*.

### Contributing

Reproducible bug reports and focused improvements are welcome. Pull requests should explain behavioral changes and verification, include relevant tests, and must not contain models, APKs, secrets, or machine-specific configuration.

### License

No open-source license is currently granted for this repository. Except where third-party licenses apply, project code and original assets remain all rights reserved. Public source visibility does not grant permission to copy, modify, or redistribute the project.
