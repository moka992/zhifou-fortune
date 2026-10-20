# Interface Motion

## Direction

Motion explains a change of state and acknowledges a touch. It must not delay an action, replace a loading indicator, hide text while data loads, or replay whenever an off-screen row becomes visible.

Design references:

- [Material motion physics](https://m3.material.io/styles/motion/overview/how-it-works): interruptible spatial springs; non-overshooting color and opacity transitions.
- [Apple Human Interface Guidelines: Motion](https://developer.apple.com/design/human-interface-guidelines/motion): purposeful, direct feedback and support for reduced motion.
- [Gemini visual design](https://design.google/library/gemini-ai-visual-design): clear transitions between interaction states.
- [Linear's 2026 design refresh](https://linear.app/now/behind-the-latest-design-refresh): consistent, quiet navigation and predictable visual hierarchy.

These references inform interaction behavior, not copied artwork or branding.

## Implementation

`FortuneMotion.kt` owns the shared presentation policy:

| Interaction | Treatment |
| --- | --- |
| Buttons and actionable cards | 0.965 press scale; interruptible spring release |
| Oracle send button | Stronger 0.9 press scale; disabled controls remain static |
| Screen changes | Direct presentation at the final position; no page-wide lift, scale, fade or bounce |
| Daily-insight dialog | No custom entrance transform; original category colors |
| Navigation selection | Short color transition and icon-scale response; stable labels and hit targets |
| Advanced fields / reading details | Top-aligned height expansion and opacity transition |
| Schedule sorting | Stable-ID placement transitions, preserving list state |
| Incoming messages | Opacity only; no repeated animation when scrolling old messages |
| Oracle page indicator | Finger-linked pill width and color; fade in during page motion, hide after 1.2 s idle |
| Daily rhythm | Existing 850 ms gauge sweep, with a synchronized numeric count |

The project uses Material 1.4, where `MotionScheme` is not public. Shared springs use stable Compose APIs and the expressive token values shipped in that dependency: default spatial damping/stiffness 0.8/380, fast spatial 0.6/800, fast effects 1/3800. There is no hidden-API access or dependency upgrade.

Drawing transforms read animated values in `graphicsLayer` or drawing callbacks. The changing rhythm number has its own small composition scope; accessibility exposes the final score rather than announcing intermediate numbers. Color and opacity are bounded. No continuous ambient animation was added.

Oracle paging retains Compose `HorizontalPager` and the existing system-Back edge guard. Its two marks continuously interpolate from `currentPage + currentPageOffsetFraction`, including when a drag reverses direction. Their combined width remains constant. The indicator overlays the page instead of reserving a row or moving the composer; it has no clickable area. A cancelled hide coroutine keeps it visible across quick consecutive swipes. It starts hidden and is suppressed during recording or message selection. [Compose pager guidance](https://developer.android.com/develop/ui/compose/layouts/pager)

## Accessibility And Lifecycle

The theme observes the system animator-duration preference and refreshes it on resume. Removing system animations snaps custom transitions to their final state and suppresses press scaling. The existing copper-coin and tarot accessibility paths use the same preference. Thinking dots stop animating when the activity is not resumed or motion is disabled.

## State And Feature Contracts

- One root page composition location is retained when changing from a bottom bar to a side rail.
- Transitions do not retain duplicate calendars, editors, speech models, or native 3D scenes.
- Scroll positions, drafts, Back navigation, calendar selection and saved data keep their existing owners.
- No changes to RNG, dice geometry or physics, tarot draws, copper-coin arithmetic, AI prompts, token budgets, caching, speech protocols, or accepted oracle-animation timelines.
- No new haptic cues were added to list scrolling or decorative movement.

## Verification

`FortuneMotionTest` covers disabled controls, reduced-motion specs and bounded press depth. The debug-only `MotionPreviewActivity` exercises the production motion helpers using synthetic data, without repository access or network clients. It is protected by a shell permission and excluded from release builds.

Physical-device checks should cover held presses, disabled controls, interrupted expansion, reordered items, dialog dismissal, repeated tab changes, rotation and the live system animation preference. Keep synthetic recordings local; do not record personal profiles, credentials, schedules or reading history.
