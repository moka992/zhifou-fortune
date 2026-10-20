<p><img src="docs/media/cover-v1.1.1.jpg" width="100%" alt="知否运势 / Zhifou Fortune — Offline first. AI optional."></p>

# 知否运势 / Zhifou Fortune

**离线占卜、传统历法与日常记录。AI 解读，由你选择。**

I Ching, Rider-Waite tarot, and the Chinese calendar. Offline first, with optional bring-your-own-key AI.

[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-216451?logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![v1.1.1](https://img.shields.io/badge/release-v1.1.1-216451)](https://github.com/moka992/zhifou-fortune/releases/tag/v1.1.1)

**[下载 APK / Download](https://github.com/moka992/zhifou-fortune/releases/latest)** · [实机演示 / Demo](docs/showcase.md) · [中文](#中文) · [English](#english) · [Agent Skill](https://github.com/moka992/zhifou-oracle-skills)

<p>
  <img src="docs/media/home.png" width="32%" alt="今日节奏与关照 / Daily overview">
  <img src="docs/media/calendar.png" width="32%" alt="离线公历农历月历 / Offline Gregorian and lunar calendar">
  <img src="docs/media/tarot-spread.png" width="32%" alt="手动选取的四牌塔罗 / Manual four-card tarot">
</p>

实机截图，使用独立演示数据。Android 界面主要为简体中文。

Actual device captures with isolated demo data. The Android interface is primarily Simplified Chinese.

## 中文

知否运势是一款原生 Android 应用，将传统历法、周易、塔罗和日程放在一起。基础占卜与历法在本机完成，不需要账号或 API Key。配置自己的模型服务后，可以围绕本次问题与结果获得进一步解读。

### 功能

| 模块 | 主要内容 |
| --- | --- |
| 今日 | 每日节奏、事业 / 关系 / 财务提示、经典关照、黄历入口 |
| 占卜 | 铜钱卦六爻全过程、78 张韦特牌组与十张十字牌阵、100 条答案之书、可选 AI 对话 |
| 日历 | 公历、农历、节气、传统节日、详细黄历、日程与纪念日 |
| 小工具 | 手动四牌塔罗、塔罗 Wiki、多面骰子、转盘、3D 抛硬币 |
| 输入与体验 | 本地中英语音转写、可选云端转写、触觉反馈、横竖屏布局与滚动位置记忆 |

骰子支持 1–6 枚 D4 / D6 / D8 / D10 / D12 / D20，硬币支持 1–10 枚。完整功能与技术说明见[中文指南](docs/guide.zh-CN.md)。

### 看一轮抽牌

<p><img src="docs/media/tarot-demo.gif" width="320" alt="实机录制：手动选牌与翻面"></p>

[完整翻牌视频、硬币与六骰子演示](docs/showcase.md) · [没有安卓设备？试试电脑端 Skill 演示](https://github.com/moka992/zhifou-oracle-skills#try-it-in-a-minute)

### 安装

1. 在 [Releases](https://github.com/moka992/zhifou-fortune/releases/latest) 下载 APK，适用于 **Android 8.0+ / arm64-v8a**。
2. 当前附件为**调试签名体验包**，不是应用商店生产包。签名一致时可覆盖安装；不要先卸载，以免丢失本地资料。
3. 直接体验离线功能。AI 与云端语音在“我的 → 设置”中按需配置，第三方服务可能收费。

APK 包含离线语音模型，体积较大。当前版本：[v1.1.1 更新说明](docs/releases/v1.1.1.md)：四牌塔罗逐牌棘轮触觉、翻牌文字层级修正与全新项目头图。

### 开发

使用 Kotlin、Jetpack Compose、Material 3 与本地 Repository；历法由 `lunar-java` 计算，离线语音使用 Sherpa-ONNX / Whisper Tiny INT8，骰子采用 Bullet 刚体运动。

需要 JDK 17、Android SDK Platform 36、Git 和 `curl`。模型二进制不提交到 Git，首次构建先下载并校验：

```sh
git clone https://github.com/moka992/zhifou-fortune.git
cd zhifou-fortune
./scripts/fetch_whisper_model.sh
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

输出：`app/build/outputs/apk/debug/app-debug.apk`。更完整的配置、权限和测试说明见[开发指南](docs/guide.zh-CN.md#从源码构建)。

### 隐私、来源与反馈

个人资料、日程、历史与接口配置保存在设备本地，不参与系统云备份。离线转写不上传录音；可选 AI 和云端转写会将相应请求发送给用户选择的服务商。应用不包含广告或统计 SDK。

[AI 服务配置](docs/ai-providers.md) · [语音配置](docs/speech-providers.md) · [权限与隐私](docs/guide.zh-CN.md#权限与隐私) · [资料与第三方许可](THIRD_PARTY_NOTICES.md)

发现问题请[提交反馈](https://github.com/moka992/zhifou-fortune/issues/new/choose)，附上版本、复现步骤和系统环境；截图与日志请先脱敏，不上传 Key、真实提问、个人资料或设备标识。

黄历与占卜用于文化学习、反思和休闲参考，不是确定预测或专业建议。**本应用源码公开可查看，未授予开源许可证**；原创代码及资源保留全部权利，第三方内容遵循各自许可。

## English

Zhifou Fortune is a native Android app for traditional calendars, I Ching, tarot, and personal schedules. Core draws and calendar calculations run locally without an account or API key. Connect your own model provider for question-aware interpretation when you need it.

### Features

| Area | Included |
| --- | --- |
| Today | Daily rhythm, career / relationship / finance guidance, classical references, and an almanac |
| Readings | Full six-line coin casting, the 78-card RWS deck and ten-card Celtic spread, 100 reflection answers, optional AI chat |
| Calendar | Gregorian and lunar dates, solar terms, traditional festivals, detailed almanacs, schedules, and anniversaries |
| Tools | Manual four-card tarot, tarot Wiki, polyhedral dice, a custom wheel, and 3D coin tosses |
| Interaction | Offline Chinese / English speech, optional cloud transcription, haptics, landscape layouts, and scroll retention |

Roll 1–6 D4 / D6 / D8 / D10 / D12 / D20 dice, or toss 1–10 coins. [Full feature and developer guide](docs/guide.en.md).

### Try It

- [Watch real-device recordings](docs/showcase.md#english): card selection and reveal, double-sided coin rotation, and a six-die roll.
- [Download the APK](https://github.com/moka992/zhifou-fortune/releases/latest) for **Android 8.0+ / arm64-v8a**. The interface is primarily Simplified Chinese.
- No Android device? [Run the independent skill demo](https://github.com/moka992/zhifou-oracle-skills#try-it-in-a-minute) on your computer, without an API key. It demonstrates local reading tools, not the Android interface.

The APK is **debug-signed for evaluation**, not a production store build. Update in place when signatures match; uninstalling removes local data. The bundled offline speech model increases download size. AI and cloud transcription are optional and may incur provider charges.

### Build

The stack is Kotlin, Jetpack Compose, Material 3, local repositories, `lunar-java`, Sherpa-ONNX / Whisper Tiny INT8, and Bullet rigid-body dice. Use JDK 17 and Android SDK Platform 36; run the commands in [开发](#开发) above. Output: `app/build/outputs/apk/debug/app-debug.apk`.

[Build guide](docs/guide.en.md#build-from-source) · [v1.1.1 release notes](docs/releases/v1.1.1.md) · [AI providers](docs/ai-providers.md) · [Speech providers](docs/speech-providers.md)

v1.1.1 aligns browsing haptics with individual card-center crossings, keeps position captions beneath the reveal animation, and adds a new project cover. Reading rules and AI behavior are unchanged.

### Privacy, Sources, and Feedback

Profiles, schedules, history, and provider settings stay on the device and are excluded from system cloud backup. Offline speech does not upload audio. Optional AI and cloud speech send the relevant request to your chosen provider. No advertising or analytics SDK is included.

[Permissions and privacy](docs/guide.en.md#permissions-and-privacy) · [Sources and third-party notices](THIRD_PARTY_NOTICES.md) · [Report an issue](https://github.com/moka992/zhifou-fortune/issues/new/choose)

Include the app version, reproduction steps, and relevant system details. Remove credentials, real questions, personal data, and device identifiers from reports. Focused contributions should explain behavior changes and verification without including models, APKs, or local configuration.

Readings are for cultural study, reflection, and recreation, not guaranteed predictions or professional advice. **The app source is publicly viewable but not open-source licensed.** Original code and assets remain all rights reserved; third-party materials retain their own licenses. The [standalone Agent Skill](https://github.com/moka992/zhifou-oracle-skills) has separate licensing terms.
