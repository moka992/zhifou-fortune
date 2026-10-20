# Interaction and Layout Validation

Validated on 2026-10-18, after the v1.0.0 release. These changes belong to the
working development build; they do not replace the published v1.0.0 artifacts.

## Changes

- Navigation updates immediately. Home's primary action no longer waits for two
  sequential animations before opening the reading screen. Press feedback and
  page transitions remain independent of navigation.
- A shared haptic controller provides selection, confirmation, collision, spin,
  and settling feedback. It uses system feedback or supported motor primitives,
  with predefined and legacy fallbacks. Motion pulses are rate-limited using a
  monotonic clock. Feedback stops when a tool is dismissed or the app is paused.
- An application-level haptic switch persists across process restarts. Effects
  use the system's touch-feedback policy rather than media vibration attributes.
  Disabled send actions remain silent.
- The calendar has a compact header, typography-aware grid height, and date
  accessibility actions. Its existing offline calculations, date retention,
  and month/year gestures are preserved.
- Bottom navigation and the wide-layout rail reserve mandatory system gesture
  insets, including devices that hide the navigation handle but still intercept
  touches in that region.
- The dice simulation stops its idle update loop. Its final haptic event follows
  actual physical settling rather than the result timer. Dice rules, geometry,
  camera settings, and result generation are unchanged.

## Verification

### Build

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --offline
```

- Debug APK built and installed successfully.
- 58 JVM tests passed, including four haptic pulse-gate tests; no failed or
  skipped tests.
- Lint reported no errors. Warning and informational findings remain and should
  not be treated as resolved by this interaction pass.

### Physical Device

Android 16 / API 36, 720 x 1600 pixels, effective density 350 dpi:

- Twenty successive tab changes and the home-to-reading action completed.
- Returning from date details or a cancelled schedule edit preserved the chosen
  calendar date. Horizontal month and vertical year gestures remained functional.
- Ten-coin flipping, wheel settings/back navigation, and six-die rolls for D6,
  D10, D12, and D20 completed. Settled dice were visually checked for separation
  and correspondence with the displayed results.
- System vibration records confirmed touch effects were executed. Disabling
  application haptics produced no new application vibration records during tab
  changes; re-enabling restored feedback. Handheld feedback was confirmed as
  light and distinct, and the tested strength was retained.
- No application crash was recorded during the final device regression.

### Emulator Layout Matrix

Android 15 / API 35:

| Display | Density | Font scale | Checks |
| --- | --- | --- | --- |
| 1080 x 2400 | 420 dpi | 1.0 | Five destinations, light/dark themes |
| 720 x 1280 | 360 dpi | 1.3 | Compact calendar and reading composer |
| 2400 x 1080 | 420 dpi | 1.0 | Landscape layout and destination retention |
| 1600 x 2560 | 320 dpi | 1.0 | Tablet calendar and navigation rail |

The emulator also verified haptic-preference persistence, disabled empty send,
and offline answer-book execution without a question. Small windows with large
fonts remain scrollable instead of compressing the complete month into an
unreadable grid.

## Limits

This is functional, visual, and physical-haptic verification of a debug build,
not a release-build latency benchmark. No p95 response time, frame-jank target,
or performance equivalence to another application is claimed. The software GPU
emulator cannot establish physical-device rendering performance. Additional
motor families, TalkBack interaction, split-screen, and release-build performance
profiling remain part of the [experience roadmap](experience-roadmap.md).

Device credentials, screenshots, raw diagnostic dumps, and API keys are not part
of this document or its repository artifacts. The tests did not require billable
AI requests or changes to stored user content.

## 中文摘要

本轮开发版已完成即时导航、统一触觉反馈、日历首屏精简、手势安全区适配和
骰子空闲更新优化，并安装到真机回归。58 项单元测试通过，Lint 无错误。
真机验证了日期保留、多骰结算、十枚硬币、转盘返回以及触觉开关的实际执行；
模拟器覆盖浅深色、小屏大字号、横屏和平板布局。触觉强度保留实机确认的
轻柔清晰档位。现有离线规则和结果生成未改动，已发布的 v1.0.0 保持不变。
本轮没有宣称达到尚未测量的延迟或掉帧指标。
