# 知否运势

[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Version](https://img.shields.io/badge/version-0.9.0-D6AA43)](https://github.com/moka992/zhifou-fortune/releases)

知否运势是一款面向 Android 的离线优先运势、占卜与日程应用。项目将传统历法、周易铜钱卦、答案之书、日历日程和轻量互动工具整合为一个可直接在手机端使用的应用；核心历法与占卜流程可离线运行，AI 解读和云端语音转写均为用户主动配置的可选能力。

> 当前版本为 `0.9.0` 预览版。应用中的运势、黄历与占卜内容用于传统文化研究和休闲参考，不构成医疗、法律、投资或其他专业建议。

## 主要功能

### 今日运势与离线黄历

- 按日期、昵称、生日和关注关键词生成每日运势。
- 离线计算公历、农历、干支、生肖、纳音、二十四节气和传统节日。
- 展示喜神、福神、财神、阴阳贵神方位以及经审校的宜忌信息。
- 日期详情包含值神、十二神、彭祖百忌、冲煞、胎神、空亡、九宫飞星等扩展黄历信息。
- 地理位置、天气和联网信息缺失时仍可完整使用基础运势功能。

### 占卜与解读

- **铜钱卦**：每爻独立投掷三枚铜钱，依次生成六爻，展示阴阳组合、本卦、动爻和变卦。
- **周易经典**：内置64卦卦辞、384爻爻辞、《彖》与《象》等离线语料。
- **答案之书**：提供100条本地答案，适合快速随机提示。
- **AI 解读**：可连接 OpenAI 兼容的 Chat Completions 接口，根据用户问题与卦象生成进一步解读。
- **AI 对话**：支持围绕周易、传统文化与相关学术主题进行连续对话，最近消息保存在本机。
- 占卜结果和聊天记录采用数量与时间上限，避免长期使用后产生不必要的性能负担。

### 语音输入

- 默认使用 Sherpa-ONNX 与 INT8 Whisper Tiny 多语言模型进行离线识别。
- 支持中文、英文及中英混合语音输入。
- 长按问题输入框开始录音，松手后转写，上滑可进入取消状态。
- 可选配置 OpenAI 兼容音频转写接口；云端转写默认关闭。

### 日历与日程

- 月历仅显示当月日期，并根据月份自动采用4、5或6行布局。
- 左右滑动切换月份，上下滑动切换年份；年、月滚动选择器支持快速滑动和精确选择。
- 双击日期可查看完整离线黄历与当日日程。
- 日程支持事项、备注、开始时间、结束日期时间、位置、参与者、卡片颜色、本地背景图和置顶状态。
- “日程纪念”支持未来倒数、过去累计、置顶和多种排序方式。

### 小工具

- **多面骰子**：支持1至6枚 D4、D6、D8、D10、D12、D20 骰子，包含轻量3D运动、稳定落点、摇晃手机触发、振动与音效反馈。
- **转盘**：支持自定义选项、触控与摇晃启动、结果历史和分段触觉反馈。
- **抛硬币**：支持1至10枚双面硬币动画、摇晃手机触发以及独立的50:50随机结果。

### 本地体验

- 深色、浅色和跟随系统主题。
- 沉浸式系统栏适配。
- 针对不同工具设计的振动反馈。
- 个人资料、历史记录、日程和接口配置均保存在设备本地。

## 技术栈

| 类别 | 技术 |
| --- | --- |
| 语言 | Kotlin / Java 17 |
| UI | Jetpack Compose / Material 3 |
| 架构 | Single Activity、ViewModel、协程、本地 Repository |
| 导航 | Navigation Compose 与页面内状态导航 |
| 历法 | `lunar-java` |
| 离线语音 | Sherpa-ONNX、Whisper Tiny INT8 |
| 网络 | OkHttp、OpenAI 兼容接口 |
| 本地数据 | SharedPreferences、JSON、持久化文档 URI |
| 最低系统 | Android 8.0 / API 26 |
| 目标系统 | Android API 35 |
| ABI | `arm64-v8a` |

## 项目结构

```text
app/src/main/java/com/zhifou/fortune/
├── MainActivity.kt              # 应用入口、主导航与核心 Compose 页面
├── DailyFortune.kt              # 今日运势与离线黄历摘要
├── AlmanacDayDetail.kt          # 日期黄历详情
├── ZhouyiDivination.kt          # 周易起卦与卦象计算
├── ZhouyiClassics.kt            # 离线周易经典语料
├── AnswerBook.kt                # 答案之书数据
├── OfflineSpeechRecognizer.kt   # 离线语音识别
├── CloudSpeechTranscriber.kt    # 可选云端语音转写
├── ScheduleModels.kt            # 日程领域模型与排序规则
└── ScheduleScreens.kt           # 日程新增、纪念、详情和编辑页面

scripts/
└── fetch_whisper_model.sh       # 下载并校验离线语音模型

tools/
└── generate_zhouyi_corpus.py    # 周易语料生成与完整性校验
```

## 构建环境

- macOS、Linux 或 Windows
- Android Studio（建议使用当前稳定版）
- JDK 17
- Android SDK Platform 35
- Git 与 `curl`

仓库不提交 Whisper 模型二进制文件。构建前需要运行脚本下载模型；脚本会对所有文件执行 SHA-256 校验。

```bash
git clone https://github.com/moka992/zhifou-fortune.git
cd zhifou-fortune

./scripts/fetch_whisper_model.sh
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

构建完成后，Debug APK 位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

通过 ADB 安装：

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 下载 APK

已构建的测试 APK 发布在 [GitHub Releases](https://github.com/moka992/zhifou-fortune/releases)。Release 中的 APK 使用 Android Debug Key 签名，用于功能测试和开发验证，不应作为应用商店生产签名包分发。

当前 APK 包含离线语音模型，因此文件体积明显大于普通 Android 应用。模型和原生运行库目前仅提供 `arm64-v8a` 架构。

## AI 接口配置

AI 解读和 AI 对话默认不可用，用户可以在应用的“我的 > 设置”中填写：

- API Key
- 模型名称
- OpenAI 兼容的 HTTPS Chat Completions 地址

云端语音转写使用独立配置，默认关闭。仓库、构建脚本和 APK 均不包含任何预置 API Key。

## 权限与隐私

| 权限 | 用途 | 是否影响离线核心功能 |
| --- | --- | --- |
| `INTERNET` | 可选 AI 解读、AI 对话和云端语音转写 | 否 |
| `RECORD_AUDIO` | 长按语音输入 | 否 |
| `VIBRATE` | 骰子、转盘和硬币的触觉反馈 | 否 |

- 应用不包含广告 SDK、统计 SDK 或第三方用户追踪组件。
- 运势、黄历、周易语料、答案之书、日历和小工具均可离线运行。
- 离线语音模式下，录音不会上传到网络。
- 只有用户主动启用并配置云端语音后，录音才会发送到所填写的第三方接口。
- API Key、个人资料、聊天记录与日程仅保存在应用本地数据中，且不参与系统云备份。
- 日程背景图片通过 Android 系统文档选择器授权，应用不请求读取整个媒体库。
- `local.properties`、密钥文件、模型文件和 APK 均被 `.gitignore` 排除，避免开发机配置进入 Git 历史。

提交 Issue 或日志时，请先移除 API Key、接口地址中的凭据、个人资料、聊天内容和设备标识。

## 测试

项目包含针对以下模块的 JVM 单元测试：

- 周易铜钱卦结构与经典语料完整性
- 今日运势和黄历规则
- 答案之书条目与随机范围
- 每日推荐敏感事项过滤
- 占卜时间线状态
- 日程兼容、排序、倒数和默认时间
- 硬币双面动画与结算角度

运行测试：

```bash
./gradlew :app:testDebugUnitTest
```

## 周易语料再生成

铜钱卦语料基于 Kanseki Repository `KR1a0001` 的固定版本生成。生成器会检查64卦、384爻、《象》以及乾坤用九/用六的完整性。

```bash
python3 tools/generate_zhouyi_corpus.py \
  /path/to/KR1a0001 \
  app/src/main/java/com/zhifou/fortune/ZhouyiClassics.kt
```

## 第三方组件

第三方依赖、模型来源和许可证信息见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。主要组件包括：

- AndroidX / Jetpack Compose
- `lunar-java`
- Sherpa-ONNX
- OpenAI Whisper Tiny
- Kanseki Repository 周易文本

## 参与贡献

欢迎通过 Issue 报告可复现的问题或提出改进建议。提交 Pull Request 前请：

1. 保持改动范围清晰，不提交模型、APK、API Key 或本机配置。
2. 为领域逻辑和回归修复补充测试。
3. 运行 `./gradlew :app:testDebugUnitTest :app:assembleDebug`。
4. 在 PR 描述中说明行为变化、验证方式和兼容性影响。

## 许可证

本仓库当前未授予开源许可证。除第三方组件按各自许可证使用外，项目代码与原创资源保留全部权利。公开仓库仅表示源代码可查看，并不自动授予复制、修改或再分发许可。
