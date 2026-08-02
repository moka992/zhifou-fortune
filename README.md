# 知否运势 / Zhifou Fortune

[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Version](https://img.shields.io/badge/version-0.9.4-D6AA43)](https://github.com/moka992/zhifou-fortune/releases)

一款离线优先的 Android 运势、传统历法、周易占卜与日程应用。

An offline-first Android app for daily reflections, the traditional Chinese calendar, I Ching divination, schedules, and tactile utility tools.

[中文](#中文) · [English](#english)

---

## 中文

### 项目简介

知否运势把传统历法、周易铜钱卦、韦特塔罗、答案之书、日历日程和几款轻量互动工具整合在一个原生 Android 应用中。历法计算、基础运势、占卜流程、日程和小工具均可离线使用；AI 解读与云端语音转写属于用户自行配置的可选功能。

项目希望在传统文化表达和现代移动体验之间取得平衡：来源明确，结论克制，不把黄历或占卜描述为确定预测，也不以恐惧性措辞推动用户作出决定。

> 当前版本为 `v0.9.4` 预览版。应用中的运势、黄历和占卜内容仅用于传统文化研究、个人反思与休闲参考，不构成医疗、法律、投资或其他专业建议。

### v0.9.4 更新

- 调整韦特塔罗抽牌节奏，将单张翻牌动画延长至 900 毫秒，并放缓逐张揭示间隔，使十张牌的抽取过程更从容、完整。
- 十字牌阵结果改为每行五张、共两行的自适应总览，十张牌可在同一区域直接查看，不再依赖横向滚动。
- 铜钱卦增加铜钱翻入、爻线展开和结果淡入动画，并适度延长六次投掷间隔，使起卦过程更清晰。

### 功能概览

#### 今日运势与黄历

- 根据日期、昵称、生日和关注关键词生成稳定的每日提示。
- 离线计算公历、农历、干支、生肖、纳音、二十四节气和传统节日。
- 展示喜神、福神、财神、阴阳贵神方位以及完整宜忌信息。
- 日期详情包含值神、十二神、彭祖百忌、冲煞、胎神、空亡和九宫飞星等内容。
- 事业、关系和财务结论使用面向现代生活的安全条目白名单；无明确条目时给出中性参考。

#### 占卜与 AI 解读

- **铜钱卦**：按真实三枚铜钱起卦流程生成六爻，展示每次投掷、阴阳、本卦、动爻和变卦。
- **周易经典**：内置 64 卦、384 爻、卦辞、爻辞、《彖》与《象》等离线语料。
- **答案之书**：内置 100 条本地答案。
- **韦特塔罗**：内置完整 78 张 Rider-Waite-Smith 公版牌面、正逆位释义与十张十字牌阵；抽牌、翻牌和基础牌阵解读均可离线完成。
- **AI 解读**：支持 OpenAI 兼容的 Chat Completions 接口，可根据用户问题和卦象生成进一步分析。
- **传统文化对话**：支持围绕《周易》《道德经》、佛道思想史等主题进行对话。
- 首页每日 AI 提示与占卜 AI 解读使用相互独立的提示词、缓存和输出限制。

#### 语音输入

- 使用 Sherpa-ONNX 与 INT8 Whisper Tiny 模型进行离线识别。
- 支持中文、英文及中英混合语音输入。
- 长按问题输入框录音，松手转写，上滑进入取消状态。
- 可选配置 OpenAI 兼容的云端音频转写接口；默认关闭。

#### 日历与日程

- 月历只显示当月日期，并根据月份自动采用 4、5 或 6 行布局。
- 左右滑动切换月份，上下滑动切换年份；年、月滚动选择器支持快速和精确选择。
- 双击日期查看完整离线黄历和当日日程。
- 日程支持事项、备注、开始与结束时间、位置、参与者、颜色、背景图和置顶状态。
- “日程纪念”提供未来倒数、过去累计和多种排序方式。

#### 小工具

- **韦特塔罗 Wiki**：可离线检索完整 78 张牌，按大阿尔卡那和四组花色分类，查阅牌面象征、牌阵用途、正逆位释义、历史背景及相似牌区别。
- **多面骰子**：支持 1 至 6 枚 D4、D6、D8、D10、D12、D20，包含轻量 3D 运动、摇晃触发、音效和触觉反馈。
- **转盘**：支持自定义选项、结果历史、摇晃启动和分段振动。
- **抛硬币**：支持 1 至 10 枚双面硬币动画、摇晃启动和独立的 50:50 随机结果。

### 技术栈

| 类别 | 技术 |
| --- | --- |
| 语言与工具链 | Kotlin、Java 17、Gradle |
| UI | Jetpack Compose、Material 3 |
| 应用结构 | Single Activity、ViewModel、Kotlin Coroutines、本地 Repository |
| 历法 | `lunar-java` |
| 离线语音 | Sherpa-ONNX、Whisper Tiny INT8 |
| 网络 | OkHttp、OpenAI-compatible Chat Completions |
| 本地数据 | SharedPreferences、JSON、Android 文档 URI |
| 最低系统 | Android 8.0 / API 26 |
| 目标系统 | Android API 35 |
| 当前 ABI | `arm64-v8a` |

### 获取 APK

测试版本发布在 [GitHub Releases](https://github.com/moka992/zhifou-fortune/releases)。Release 中的 APK 使用 Android Debug Key 签名，适合功能体验和开发测试，不应用作应用商店生产签名包。

当前 APK 包含离线语音模型，因此体积明显大于普通 Android 应用，并且暂时只提供 `arm64-v8a` 架构。

### 从源码构建

需要 Android Studio 当前稳定版、JDK 17、Android SDK Platform 35、Git 和 `curl`。

仓库不提交 Whisper 模型二进制文件。构建前请使用校验脚本下载模型：

```bash
git clone https://github.com/moka992/zhifou-fortune.git
cd zhifou-fortune

./scripts/fetch_whisper_model.sh
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

APK 输出位置：

```text
app/build/outputs/apk/debug/app-debug.apk
```

通过 ADB 覆盖安装并保留本地数据：

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### AI 配置

在“我的 > 设置”中填写 API Key、模型名称和 OpenAI 兼容的 HTTPS Chat Completions 地址。应用会在配置修改完成后进行一次低成本连接验证，并保存不可逆配置指纹：

- 配置未变化时，重新打开应用不会重复测试模型。
- 断网、验证失败和配置缺失会显示不同状态。
- 用户可随时点击“连接测试”手动重试。
- 首页三项 AI 解读按日期和类别缓存，重复打开不会再次调用接口。

仓库和 APK 不包含预置 API Key。第三方 AI 服务如何处理请求数据，取决于用户自行选择的服务提供商及其隐私政策。

用户主动执行 AI 塔罗解读时，应用会向其配置的模型服务发送本次问题、十张牌及正逆位；用户已填写的昵称、生日、关注词和当日离线黄历只作为可选的次要语境发送。日程、历史记录、录音和其他本地内容不会加入塔罗请求。

### 权限与隐私

| 权限 | 用途 | 是否影响离线核心功能 |
| --- | --- | --- |
| `INTERNET` | 可选 AI 解读、对话和云端语音转写 | 否 |
| `ACCESS_NETWORK_STATE` | 在调用 AI 前区分无网络与配置错误 | 否 |
| `RECORD_AUDIO` | 长按语音输入 | 否 |
| `VIBRATE` | 骰子、转盘和硬币触觉反馈 | 否 |

- 不包含广告 SDK、统计 SDK 或第三方用户追踪组件。
- 个人资料、日程、历史、聊天和接口配置保存在设备本地，且不参与系统云备份。
- 离线语音模式下，录音不会上传到网络。
- 云端 AI 和语音功能仅在用户主动配置后使用。
- 日程背景图片通过 Android 系统文档选择器授权，应用不请求读取整个媒体库。
- `local.properties`、签名文件、API Key、模型文件和 APK 均不应进入 Git 历史。

提交 Issue 或日志前，请移除个人资料、聊天内容、接口凭据和设备标识。

### 项目结构

```text
app/src/main/java/com/zhifou/fortune/
├── MainActivity.kt              # 应用入口、主导航、Compose 页面与网络客户端
├── DailyFortune.kt              # 今日运势与离线黄历摘要
├── DailyInsights.kt             # 事业、关系、财务的离线规则与安全过滤
├── AlmanacDayDetail.kt          # 日期黄历详情
├── ZhouyiDivination.kt          # 起卦、变卦与取用规则
├── ZhouyiClassics.kt            # 离线周易经典语料
├── AnswerBook.kt                # 答案之书数据
├── TarotDeck.kt                 # 78 张韦特牌库、牌阵、正逆位与本地解读
├── OfflineSpeechRecognizer.kt   # 离线语音识别
├── CloudSpeechTranscriber.kt    # 可选云端语音转写
├── ScheduleModels.kt            # 日程模型与排序规则
└── ScheduleScreens.kt           # 日程编辑、纪念与详情页面

scripts/fetch_whisper_model.sh    # 下载并校验离线语音模型
tools/generate_zhouyi_corpus.py  # 生成并校验周易语料
```

### 测试

JVM 单元测试覆盖铜钱卦与变卦规则、78 张塔罗牌库和无重复抽牌、周易语料完整性、黄历和每日运势、三项离线结论、敏感条目隔离、AI 提示词边界、配置指纹、日程逻辑、推荐内容和硬币结算。

```bash
./gradlew :app:testDebugUnitTest
```

### 第三方组件

依赖、模型、牌面、原始文本来源和许可证见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)，主要包括 AndroidX、Jetpack Compose、`lunar-java`、Sherpa-ONNX、OpenAI Whisper Tiny、Kanseki Repository 周易文本，以及公版 Rider-Waite-Smith 牌面与《The Pictorial Key to the Tarot》。

### 参与贡献

欢迎提交能够复现的问题和范围明确的改进。Pull Request 请说明行为变化与验证方式，补充必要测试，并确认没有提交模型、APK、密钥或本机配置。

### 许可证

本仓库当前未授予开源许可证。除第三方组件按各自许可证使用外，项目代码与原创资源保留全部权利。公开仓库表示源代码可查看，但不自动授予复制、修改或再分发许可。

---

## English

### About

Zhifou Fortune is a native Android app that brings together the traditional Chinese calendar, I Ching coin divination, Rider-Waite-Smith tarot, a Book of Answers, schedules, and several tactile utility tools. Calendar calculations, baseline readings, divination mechanics, schedules, and utilities work offline. AI interpretation and cloud speech transcription are optional and require a service configured by the user.

The project aims to present traditional material with modern product standards: sources should remain identifiable, conclusions should stay measured, and cultural references should never be framed as guaranteed predictions or fear-based advice.

> `v0.9.4` is a preview release. Fortune, almanac, and divination content is intended for cultural study, personal reflection, and entertainment. It is not medical, legal, financial, or professional advice.

### What's new in v0.9.4

- Refined Rider-Waite tarot pacing with a 900 ms flip for each card and calmer reveal intervals across the ten-card draw.
- Replaced the horizontally scrolling Celtic spread result with a responsive five-column, two-row overview so all ten cards remain visible together.
- Added coin-entry, line-expansion, and result-fade transitions to three-coin I Ching casting, with slightly longer intervals between the six casts.

### Features

#### Daily reading and offline almanac

- Stable daily guidance based on date, nickname, birthday, and optional focus keywords.
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
- Optional OpenAI-compatible cloud transcription, disabled by default.

#### Calendar and schedules

- Month views show only dates from the current month and automatically use four, five, or six rows.
- Horizontal swipes change month; vertical swipes change year. Wheel pickers support both fast travel and precise selection.
- Double-tap a date to open its full offline almanac and schedule list.
- Schedule fields include notes, start and end time, location, participants, color, background image, and pinning.
- Schedule Memorial provides countdowns, elapsed-day tracking, and multiple sort orders.

#### Utility tools

- **Rider-Waite tarot Wiki**: an offline, searchable reference for all 78 cards, organized by Major Arcana and suit, with artwork notes, spread usage, upright and reversed meanings, historical context, and comparisons with related cards.
- **Polyhedral dice**: one to six D4, D6, D8, D10, D12, or D20 dice with lightweight 3D motion, shake-to-roll, sound, and haptics.
- **Wheel**: custom segments, result history, shake-to-spin, and segmented haptic feedback.
- **Coin toss**: one to ten double-sided animated coins, shake-to-toss, and independent 50:50 outcomes.

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
| Target Android | API 35 |
| Current ABI | `arm64-v8a` |

### Download

Test APKs are available from [GitHub Releases](https://github.com/moka992/zhifou-fortune/releases). Release APKs are signed with the Android debug key and are intended for evaluation and development testing, not production store distribution.

The APK is larger than a typical Android app because it contains the offline speech model. The current build targets `arm64-v8a` only.

### Build from source

You need the current stable Android Studio, JDK 17, Android SDK Platform 35, Git, and `curl`.

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

Under **My > Settings**, enter an API key, model name, and an OpenAI-compatible HTTPS Chat Completions endpoint. After a configuration change, the app runs one low-cost connection check and stores an irreversible configuration fingerprint.

- Unchanged settings are not retested whenever the app opens.
- Missing connectivity, invalid settings, and incomplete configuration are reported separately.
- A manual **Connection test** remains available at any time.
- Home-screen AI insights are cached by date and category, so reopening them does not call the API again.

No API key is bundled with the source or APK. Data handling by an external AI service is governed by the provider selected by the user.

When the user explicitly requests an AI tarot interpretation, the app sends the current question, the ten drawn cards, and their orientations to the configured model service. User-entered nickname, birthday, focus keywords, and the current offline almanac may be included as secondary context. Schedules, history, recordings, and unrelated local data are not included in tarot requests.

### Permissions and privacy

| Permission | Purpose | Required for offline core features |
| --- | --- | --- |
| `INTERNET` | Optional AI, chat, and cloud transcription | No |
| `ACCESS_NETWORK_STATE` | Distinguish offline state from configuration errors | No |
| `RECORD_AUDIO` | Hold-to-talk speech input | No |
| `VIBRATE` | Haptics for dice, wheel, and coin toss | No |

- No advertising SDK, analytics SDK, or third-party user tracking is included.
- Profiles, schedules, history, chat, and service settings remain on the device and are excluded from system cloud backup.
- Audio is not uploaded in offline speech mode.
- Cloud AI and speech features run only after explicit user configuration.
- Schedule images are granted through Android's document picker; the app does not request broad media-library access.
- `local.properties`, signing files, API keys, model binaries, and APKs must not be committed to Git.

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

Dependency, model, artwork, source-text, and license details are documented in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md). Major components include AndroidX, Jetpack Compose, `lunar-java`, Sherpa-ONNX, OpenAI Whisper Tiny, the Kanseki Repository I Ching text, and the public-domain Rider-Waite-Smith deck and *The Pictorial Key to the Tarot*.

### Contributing

Reproducible bug reports and focused improvements are welcome. Pull requests should explain behavioral changes and verification, include relevant tests, and must not contain models, APKs, secrets, or machine-specific configuration.

### License

No open-source license is currently granted for this repository. Except where third-party licenses apply, project code and original assets remain all rights reserved. Public source visibility does not grant permission to copy, modify, or redistribute the project.
