# Dice Motion

## Implementation

Dice use Bullet rigid-body dynamics through libGDX's `gdx-bullet` 1.14.2 wrapper.
The physics hull and the visible polygon mesh share the same vertices for D4,
D6, D8, D10, D12, and D20. The existing sizes, silver/gold appearance, D4 camera,
top-down camera for other dice, result counter, and curtain remain.

- A worker computes gravity, angular motion, floor and wall contacts, friction,
  restitution, and inter-die collisions at a fixed 120 Hz. No position targets,
  final orientation attraction, or last-frame placement are used.
- A candidate is accepted only after motion becomes quiet, every die has flat
  floor support, and result faces remain separately readable. Invalid candidates
  are discarded before playback. Failure is reported rather than presented as a
  successful new result.
- The current configuration has one random result with three prepared motion
  variants: light, medium and strong. Medium is prepared first and is always used
  by the button. After consumption, the next throw is prepared in the background.
  Configuration changes and leaving the tool
  cancel obsolete work; native objects are disposed in `finally` blocks.
- Playback follows the display frame clock and interpolates adjacent recorded
  poses. It does not run the rigid-body solver on the UI thread. Idle dice do not
  run an update loop. Contact impulses drive the existing rate-limited haptics.
- Geometry indices, text paint objects, and face-local text frames are reused.
  Each frame transforms each unique vertex once. Rotations avoid intermediate
  quaternion allocations.

## Shake Strength

Shake input uses Android's linear-acceleration sensor when available, with a
time-based gravity filter on the accelerometer as a fallback. Sampling requests
the game rate, with no high-rate sensor permission. A 160–280 ms gesture window
measures peak and RMS acceleration and requires sustained motion with a direction
reversal; a single bump, slow tilt and ordinary noise do not start a throw.
Three bounded strength tiers alter actual launch speed and horizontal-axis
tumbling, not playback speed or selected numbers. Medium and strong rolls have
greater guaranteed initial tumbling than the previous launch distribution.

The detector adapts its upper thresholds for low-range sensors. It requires a
quiet interval before rearming, blocks repeats for 1.2 seconds, and discards
gestures while a throw is busy. Sensors are registered only on the resumed dice
page and released on pause or exit. Unsupported sensors do not disable the
button. Sensor samples are processed locally and are never saved or transmitted.

Reference: [Android motion sensors](https://developer.android.com/develop/sensors-and-location/sensors/sensors_motion).

## Tool Layout and Curtain

The throw action is a compact 48 dp Material button in the top app bar, with a
tooltip, press animation and the existing start haptic. It stays reachable when
the user scrolls recent results. Starting a new throw returns the content to the
table. There is no full-width throw button below the counter.

The main panel uses the viewport remaining below the app bar. Portrait places
the selectors above a flexible table and its result counter; short wide windows
place compact selectors in the toolbar and the counter beside the table. A minimum content height preserves readable
controls under unusually small windows or enlarged fonts, with scrolling as a
fallback. Recent results remain below the main panel.

Only the 96 x 48 dp curtain handle accepts a curtain drag. It follows the leading
edge as the cover opens or closes; starting a gesture elsewhere on the table,
counter or page does not operate the cover. Tapping the handle is an accessible
alternative. The cover still masks both the table and counter while the throw
action remains available above it.
The handle is visually a small muted metal pull strip, not a filled arrow button;
its touch target remains larger than the visible strip.

## Numbers and Results

The existing independent random-number generation remains the source of results;
this is not a claim that rendered physical outcomes are statistically uniform.
Face values are assigned once before each playback so its final reading faces
match the selected results. Every face has a unique value, and opposite D6 faces
sum to seven. Values do not change during a throw.

Text uses a permanent local center, tangent, and vertical direction for each
polygon. An affine face transform attaches it to the rotating solid. Projected
edge lengths never select its orientation, and rotation never changes its fitted
font size. Face clipping prevents glyphs from extending past polygon boundaries.

## Validation

The strength revision passes 175 JVM tests. The native solver regression includes
the existing 720 medium throws and 1,080 additional throws covering all three
strengths, all supported polyhedra and counts. Synthetic sensor traces cover
25/50/100 Hz input, gravity filtering, noise, isolated impacts, cooldown,
rearming, busy input, sensor gaps and limited-range devices. These traces do not
replace a human check of real handheld shake sensitivity.

On 2026-10-18, the debug build, 65 JVM tests, and Lint completed without errors.
The native-backed tests include 720 deterministic throws across all six supported
types and counts from one through six, flat support and separation checks,
result correspondence, continuous motion, D6 opposite values, fixed text frames,
and cancellation/resource cleanup. These are host tests, not phone FPS claims.

Physical-device checks covered six D6, D4, and D8 throws. The fixed-text revision
was installed and D6 motion/result screenshots were inspected. The full physical
UI matrix was not completed; automated runs were interrupted by page changes.
No isolated release-build latency or jank benchmark is claimed.

Both bundled arm64 native libraries have 16 KB ELF load-segment alignment.
Their Gradle extraction and JNI field-retention rules are part of the build.
See [third-party notices](../THIRD_PARTY_NOTICES.md) for licenses.

## 中文摘要

骰子改为后台计算的 Bullet 多面体刚体运动，界面只播放真实求解得到的轨迹。
移除了末段吸附落点和强制摆正，保留原有随机结果、面数数量、视角、颜色、
卷帘和摇晃触发。文字固定在各个面的局部坐标上，随模型一起倾斜和转动，
不再根据屏幕上的最长边重新定向或动态调整字号。65 项单元测试通过，
其中包含 720 组物理投掷；真机检查范围和性能验证限制见上文。
