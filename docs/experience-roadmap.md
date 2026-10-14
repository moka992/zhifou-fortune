# Experience Review After v1.0.0

Reviewed on 2026-10-14. This is a prioritized engineering and design review,
not a list of features already implemented or a promise of measured performance.
The v1.0.0 release preserves existing product behavior.

## 中文

### 目标与参考边界

下一阶段的目标是更即时的操作响应、更清楚的阅读层级，以及与动作同步的
触觉反馈，而不是继续叠加装饰和特效。

Quin 的公开产品资料强调洗牌、抽牌与后续追问。适合借鉴的是连续的提问与
解读流程、明确的视觉焦点和仪式感，而不是复制品牌、牌背或商业插画。
本次未完成 Quin 原生应用的实机体验，不能据公开介绍推断它的性能或实现。
[参考：Quin 官方产品说明](https://quin.love/cn)。

Threads 是交互质量的体验目标。本次没有取得其内部触觉参数，也没有进行
同机原生应用触觉对比，因此不声称已复刻 Threads。下一阶段应在同一台手机
上对比轻触、选择、确认与取消的节奏，再建立本项目自己的反馈规范。

### 优先级

#### P1：生产发布与凭据保护

- 当前下载包是可调试、调试签名的 APK。正式面向广泛用户分发前，应建立
  独立保管的生产签名、非调试 R8 构建和签名备份流程，并明确旧安装的迁移路径。
- `FortuneRepository` 把 AI 与云语音密钥保存为应用私有 SharedPreferences
  字符串。这不是向网络泄露，但没有额外的静态加密保护。建议用 Android
  Keystore 管理加密密钥，迁移既有配置，并确保重试日志不含凭据。
- Manifest 已关闭云备份，还应明确 Android 12+ 的设备迁移排除规则。
- 定位：`app/build.gradle.kts`；`MainActivity.kt` 的 `FortuneRepository`；
  `app/src/main/AndroidManifest.xml`。

#### P1：建立统一触觉系统

- `MainActivity.kt` 的 `ToolHaptics` 目前使用定长单次与波形振动，并把
  Android 13+ 的用途统一设为 `USAGE_MEDIA`。不同马达的清脆程度和强度
  无法仅凭相同幅度值保证一致；普通输入反馈也不应与游戏反馈混为一类。
- 建议拆分为选择、确认、取消、边界、碰撞和揭晓等语义事件。普通操作优先
  使用系统动作反馈；特殊工具按设备能力使用预定义效果或组合原语，并保留
  旧系统回退。遵守系统触觉偏好，增加应用内关闭选项。
- 触觉由动画中的实际事件驱动：转盘越过分隔线、硬币经过翻面节点、骰子
  产生有效碰撞才触发。使用单调时钟限流；离开页面或取消动作后停止反馈。
- 不给所有点击都加震动。无效发送保持完全静默，多个硬币不叠加成连续噪振。
- 验收：同机对比至少两类马达；系统关闭触觉后普通控件不振；无重复脉冲、
  无离页残留，振动触发与视觉事件使用同一时间线。真实体感必须实机验收。

Android 推荐面向操作语义的反馈，并提供设备适配与系统设置支持。
[参考：Android 触觉反馈](https://developer.android.com/develop/ui/views/haptics/haptic-feedback)。

#### P1：即时响应与可重复的流畅度测试

- 首页 `HomeScreen` 的“去占卜”先等待 70ms 和 45ms 两段动画，再执行导航。
  建议导航状态立即提交，按压反馈和页面过渡并行，避免人为增加约 115ms 等待。
- 目前有 JVM 测试，但没有应用专属 Macrobenchmark 或 Baseline Profile
  生成模块；截图不能证明低端设备的首开流畅度。
- 在非调试构建上测冷启动、首次切页、月年切换、长解读滚动和六骰运动，
  记录帧超时、主线程阻塞、内存和温升，再针对真实热点优化。
- 初始目标：本地操作的首次视觉响应 p95 不超过 100ms；持续滚动的超时帧
  比例低于 1%，且没有超过 100ms 的停顿。按 60/90/120Hz 各自的帧预算
  统计。这些是拟定验收目标，不是 v1.0.0 已达到的测量结果。
- 使用应用路径生成 Baseline/Startup Profiles，并比较安装后首次运行，
  而不只比较已经预热的页面。

[参考：Macrobenchmark](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview)、
[Baseline Profiles](https://developer.android.com/topic/performance/baselineprofiles/overview)。

#### P2：占卜阅读与等待过程

- 当前 AI 客户端使用同步 HTTP 调用并一次性读取完整响应；即使运行在后台，
  用户仍要等整段内容生成完成。定位：`AiInterpreter.requestCompletion`。
- 建议支持流式解读、明确的生成状态、取消和失败重试；不支持流式的服务继续
  使用原有路径。不得因为自动重试重复收费、重复起卦或改变抽牌结果。
- 结果先呈现简短结论，再展开依据与经典；保持完整原文和离线解释可访问。
  追问绑定当前牌阵或卦象，明确区分追问与重新占卜。
- 保持十张塔罗牌的五列两行布局和现有抽牌规则；增加点击放大和完整名称
  的无障碍语义，而不是为美观缩小字体或改动结果。
- 验收：断网仍完成离线流程；取消不丢失结果；半途切页不重复请求；流式
  内容增长不强行打断正在阅读旧消息的用户。

#### P2：内容密度与跨设备布局

- 小窗口、大字体的日历截图里，页面标题、说明、今日行、完整日期和年月
  控件叠加后，首屏只有约两行日期。定位：`ScheduleScreen`、`CalendarPanel`。
- 建议合并重复的日期层级，减少非必要说明和垂直间距，保留清晰的今日、
  年月选择与新增入口。普通手机尽量首屏显示完整月份；极端大字体时优先
  保持可读性和可滚动，而不是强行压缩。
- 部分弹窗和 Wiki 仍使用 `Configuration.screenWidthDp/screenHeightDp`；
  应逐步按实际容器约束布局，以覆盖分屏、折叠屏和横屏键盘。
- 每个页面保留一个明确主操作；材质服务阅读，避免重新堆叠高光和嵌套卡片。
- 验收覆盖窄屏、大字体、手势与三键导航、横屏键盘、分屏，以及 TalkBack。
  操作目标至少 48dp，图标有语义，颜色不是唯一状态线索。

#### P2：可维护性与安装体积

- `MainActivity.kt` 超过一万行，同时承载页面、数据与网络。先分离触觉、
  AI 客户端和 Repository，再按页面提取；用回归测试约束迁移，不一次重写。
- APK 捆绑离线语音模型，体积较大。可评估额外提供轻量包和校验下载，但
  完整离线包继续保留，未经用户明确选择不自动下载或上传音频。
- 增加 CI 构建、单元测试、Lint、脱敏密钥扫描及布局回归，避免重复发生
  已修好的导航、输入和屏幕适配问题。

### 建议实施顺序

1. 生产签名与凭据保护，同时建立实机性能基线。
2. 统一触觉与动作时间线，去除人为导航等待。
3. 优化占卜等待、阅读与追问流程。
4. 精简日历首屏并补齐多窗口和无障碍回归。

以上均为后续建议；除发布检查发现的兼容修正外，没有混入 v1.0.0 的交互改动。

## English Summary

The next milestone should improve response, reading hierarchy, and synchronized
feedback rather than add decoration. Quin's public material informs the ritual
and follow-up flow; its native performance was not tested. Threads is a quality
reference, not a source of verified haptic parameters or copied implementation.

1. **Production safety:** replace debug distribution with managed production
   signing, protect locally stored credentials using Keystore-backed encryption,
   and define device-transfer exclusions. Plan update compatibility first.
2. **Haptics:** replace the uniform one-shot/waveform approach with semantic,
   capability-aware feedback. Honor user preferences and synchronize effects to
   real animation events. Validate on physical motors, not an emulator.
3. **Responsiveness:** remove the approximately 115ms pre-navigation animation
   wait. Add release-build Macrobenchmarks and app-specific Baseline Profiles.
   Proposed targets are p95 local visual response under 100ms and fewer than 1%
   late frames during sustained scrolling; these are not current measurements.
4. **Readings:** introduce cancellable streaming with a compatible non-streaming
   fallback, concise summaries, preserved source details, and contextual follow-up.
   Do not change offline draws or repeat billable requests on navigation.
5. **Density and accessibility:** compact the calendar's repeated header layers,
   size dialogs from their real containers, and test large text, TalkBack,
   landscape keyboards, gesture navigation, and multi-window layouts.
6. **Maintainability:** extract responsibilities from the 10,000-line activity,
   add regression CI, and consider an optional smaller download without removing
   the complete offline build.

Code anchors and acceptance criteria are detailed above. v1.0.0 is a frozen
product baseline; this review does not silently introduce these future changes.
