# 知否运势 / Zhifou Fortune

[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Version](https://img.shields.io/badge/version-1.1.0-216451)](https://github.com/moka992/zhifou-fortune/releases/tag/v1.1.0)

一款离线优先的 Android 运势、传统历法、周易占卜与日程应用。

An offline-first Android app for daily reflections, the traditional Chinese calendar, I Ching divination, schedules, and tactile utility tools.

[中文](#中文) · [English](#english)

---

## 中文

### 项目简介

知否运势把传统历法、周易铜钱卦、韦特塔罗、答案之书、日历日程和几款轻量互动工具整合在一个原生 Android 应用中。历法计算、基础运势、占卜流程、日程和小工具均可离线使用；AI 解读与云端语音转写属于用户自行配置的可选功能。

项目希望在传统文化表达和现代移动体验之间取得平衡：来源明确，结论克制，不把黄历或占卜描述为确定预测，也不以恐惧性措辞推动用户作出决定。

> 当前版本为 `v1.1.0`。应用中的运势、黄历和占卜内容仅用于传统文化研究、个人反思与休闲参考，不构成医疗、法律、投资或其他专业建议。下载附件仍为调试签名 APK，签名与分发说明见下文。

### v1.1.0 更新

- 新增四牌塔罗：亲手选牌、离线牌义、缓慢翻牌与独立 AI 解读，提供完整玩法来源。
- 更新 3D 硬币、铜钱正反面与阴影；改进骰子刚体运动、分档摇晃输入和工具布局。
- 扩展触觉反馈，区分翻牌、硬币、转盘、骰子、滚轮、开关、语音取消和确认操作，并按设备能力选择实现。
- 增加 AI 与云端语音的服务商、模型选项，保留自定义配置及连接测试；调整解读表达与长文本输入体验。
- 改进横屏、窄屏、大字体、系统安全区、页面返回与滚动位置记忆；动态图标与静态端点共用图形。
- 增加几何、运动、抽牌、输入、配置和触觉测试。发布验证与限制见 [v1.1.0 说明](docs/releases/v1.1.0.md)。

设计基线见 [Interface Design](docs/ui-redesign.md)，当前触觉设计见 [Haptic Design](docs/haptic-design.md)。

### 功能概览

#### 今日运势与黄历

- 根据日期、昵称、生日和关注关键词生成稳定的每日提示。
- 今日节奏依据日常宜忌与节令规则在本机计算；今日关照结合经典原文与现代释义。“我的 > 设置 > 数据来源与说明”集中列出公开来源、计算原则、隐私信息及使用声明。数值是文化参考刻度，不代表吉凶概率。相关说明见 [每日节奏与关照](docs/daily-rhythm.md)。
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
- 可选配置硅基流动、Groq、OpenAI、通义千问、Mistral、ElevenLabs 的云端转写；支持平台与模型预设、自定义接口和手动连接测试，默认关闭。免费范围与接口说明见 [语音转写配置](docs/speech-providers.md)。

#### 日历与日程

- 月历只显示当月日期，并根据月份自动采用 4、5 或 6 行布局。
- 左右滑动切换月份，上下滑动切换年份；年、月滚动选择器支持快速和精确选择。
- 双击日期查看完整离线黄历和当日日程。
- 日程支持事项、备注、开始与结束时间、位置、参与者、颜色、背景图和置顶状态。
- “日程纪念”提供未来倒数、过去累计和多种排序方式。

#### 小工具

- **四牌塔罗**：从完整韦特牌组中亲手选取四张，按现状、阻碍、助力、趋向展开；支持可选提问、逆位开关、离线牌义和按需 AI 解读。[玩法与来源](docs/four-card-tarot.md)。
- **韦特塔罗 Wiki**：可离线检索完整 78 张牌，按大阿尔卡那和四组花色分类，查阅牌面象征、牌阵用途、正逆位释义、历史背景及相似牌区别。
- **多面骰子**：支持 1 至 6 枚 D4、D6、D8、D10、D12、D20，包含 Bullet 刚体运动、分档摇晃触发、音效和碰撞触觉反馈。
- **转盘**：支持自定义选项、结果历史、摇晃启动和分段振动。
- **抛硬币**：支持 1 至 10 枚原创 3D 双面硬币、摇晃启动和独立的 50:50 随机结果。

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
| 目标系统 | Android API 36 |
| 当前 ABI | `arm64-v8a` |

### 获取 APK

测试版本发布在 [GitHub Releases](https://github.com/moka992/zhifou-fortune/releases)。Release 中的 APK 使用 Android Debug Key 签名，适合功能体验和开发测试，不应用作应用商店生产签名包。

当前 APK 包含离线语音模型，因此体积明显大于普通 Android 应用，并且暂时只提供 `arm64-v8a` 架构。

### 从源码构建

需要 Android Studio 当前稳定版、JDK 17、Android SDK Platform 36、Git 和 `curl`。

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

在“我的 > 设置”中选择 API 平台和模型，填写对应的 API Key 后保存。预设支持 DeepSeek、通义千问、智谱、Kimi、MiniMax、硅基流动、OpenAI、Gemini 和 Claude；也可选择“自定义”填写 OpenAI 兼容的完整接口地址与模型 ID。现有配置会保留为自定义配置，不会被预设覆盖。平台、地区、模型及官方来源见 [AI 配置说明](docs/ai-providers.md)。

应用会在配置保存并发生变化后进行一次低成本连接验证，并保存不可逆配置指纹：

- 配置未变化时，重新打开应用不会重复测试模型。
- 断网、验证失败和配置缺失会显示不同状态。
- 用户可随时点击“连接测试”手动重试。
- 首页三项 AI 解读按日期和类别缓存，重复打开不会再次调用接口。

仓库和 APK 不包含预置 API Key。第三方 AI 服务如何处理请求数据，取决于用户自行选择的服务提供商及其隐私政策。

占卜页的十张牌 AI 解读会向用户配置的模型服务发送本次问题、十张牌及正逆位；用户已填写的昵称、生日、关注词和当日离线黄历只作为可选的次要语境发送。日程、历史记录、录音和其他本地内容不会加入塔罗请求。

小工具中的四牌塔罗使用独立提示词，仅在用户点击 AI 解读后发送该次问题、四张牌及正逆位、可选昵称和关注词，不发送生日或黄历。成功解读随该次记录保存，重新查看不会自动发起请求。

### 权限与隐私

| 权限 | 用途 | 是否影响离线核心功能 |
| --- | --- | --- |
| `INTERNET` | 可选 AI 解读、对话和云端语音转写 | 否 |
| `ACCESS_NETWORK_STATE` | 在调用 AI 前区分无网络与配置错误 | 否 |
| `RECORD_AUDIO` | 长按语音输入 | 否 |
| `VIBRATE` | 导航、选择、占卜和小工具触觉反馈，可在设置中关闭 | 否 |

- 不包含广告 SDK、统计 SDK 或第三方用户追踪组件。
- 个人资料、日程、历史、聊天和接口配置保存在设备本地，且不参与系统云备份。
- 离线语音模式下，录音不会上传到网络。
- 云端 AI 和语音功能仅在用户主动配置后使用。
- 日程背景图片通过 Android 系统文档选择器授权，应用不请求读取整个媒体库。
- `local.properties`、签名文件、API Key、离线语音模型二进制和 APK 均不应进入 Git 历史。原创硬币的运行时 GLB 资源随源码提供；可能保留本机路径的 Blender 工程仅在本地保留，可用建模脚本重新生成。

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

> The current version is `v1.1.0`. Fortune, almanac, and divination content is intended for cultural study, personal reflection, and entertainment. It is not medical, legal, financial, or professional advice. The downloadable APK remains debug-signed; see the distribution notes below.

### What's new in v1.1.0

- Added four-card tarot with manual selection, offline references, a slower reveal, an independent AI interpretation flow, and documented method sources.
- Refined original 3D coins, casting-coin artwork and shadows, rigid-body dice motion, strength-sensitive shaking, and tool layouts.
- Added distinct tactile cues for cards, coins, dice, wheel detents, pickers, toggles, voice cancellation, and confirmations, selected according to device capabilities.
- Added AI and cloud-transcription provider/model choices alongside custom configuration and connection tests; refined interpretation wording and long-text input.
- Improved landscape, compact windows, larger fonts, system insets, Back navigation, and scroll retention. Animated icons share their static geometry at resting endpoints.
- Expanded geometry, motion, tarot, input, configuration, and haptic tests. See the [v1.1.0 notes](docs/releases/v1.1.0.md) for verification and limitations.

See [Interface Design](docs/ui-redesign.md) for the visual baseline and [Haptic Design](docs/haptic-design.md) for current tactile behavior.

### Features

#### Daily reading and offline almanac

- Stable daily guidance based on date, nickname, birthday, and optional focus keywords.
- Daily rhythm is calculated locally from everyday-almanac and seasonal rules. Category guidance separates classical excerpts from modern advice. Data Sources & Notes in settings brings together public sources, calculation principles, privacy information, and disclaimers. The index is a cultural reference, not a probability of good fortune. See [daily rhythm and guidance](docs/daily-rhythm.md) for details.
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
- Optional cloud transcription through SiliconFlow, Groq, OpenAI, Qwen, Mistral, and ElevenLabs, with provider/model presets, custom configuration, and a manual connection test. Disabled by default. See [speech provider configuration](docs/speech-providers.md) for free-tier conditions and endpoints.

#### Calendar and schedules

- Month views show only dates from the current month and automatically use four, five, or six rows.
- Horizontal swipes change month; vertical swipes change year. Wheel pickers support both fast travel and precise selection.
- Double-tap a date to open its full offline almanac and schedule list.
- Schedule fields include notes, start and end time, location, participants, color, background image, and pinning.
- Schedule Memorial provides countdowns, elapsed-day tracking, and multiple sort orders.

#### Utility tools

- **Four-card tarot**: manually pick four cards from the complete RWS deck for situation, resistance, resources, and conditional direction. Optional questions and reversals, offline meanings, and explicit opt-in AI interpretation. [Method and sources](docs/four-card-tarot.md).
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

Under **My > Settings**, choose a provider and model, enter the corresponding API key, and save. Presets cover DeepSeek, Qwen, Zhipu GLM, Kimi, MiniMax, SiliconFlow, OpenAI, Gemini, and Claude. **Custom** retains editable OpenAI-compatible endpoint and model ID fields. Existing configurations remain unchanged as custom settings. See [AI provider configuration](docs/ai-providers.md) for endpoints, regional choices, models, and official references.

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

Dependency, model, artwork, source-text, and license details are documented in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md). Major components include AndroidX, Jetpack Compose, `lunar-java`, Sherpa-ONNX, OpenAI Whisper Tiny, the Kanseki Repository I Ching text, and the public-domain Rider-Waite-Smith deck and *The Pictorial Key to the Tarot*.

### Contributing

Reproducible bug reports and focused improvements are welcome. Pull requests should explain behavioral changes and verification, include relevant tests, and must not contain models, APKs, secrets, or machine-specific configuration.

### License

No open-source license is currently granted for this repository. Except where third-party licenses apply, project code and original assets remain all rights reserved. Public source visibility does not grant permission to copy, modify, or redistribute the project.
