# Haptic Design

The tactile system reinforces a change the user can see or control. It does not continuously vibrate while reading, waiting for AI, or scrolling ordinary text. Existing settings, system touch-feedback preferences and lifecycle cancellation remain authoritative. A disabled send control has no tactile event.

## References and Limits

The desired progressive pull-and-release sensation was described through the user's experience of Threads on iOS. No Meta-published Threads motor waveform or implementation was found. Third-party recreations are not evidence of its internals, and this project does not claim to reproduce proprietary timing or iPhone motor mechanics.

Primary technical references:

- [Apple: Playing haptics](https://developer.apple.com/design/human-interface-guidelines/playing-haptics): meaningful events, restraint and audiovisual synchronization.
- [Apple: Real-time haptic parameters](https://developer.apple.com/documentation/corehaptics/updating-continuous-and-transient-haptic-parameters-in-real-time): input-driven intensity and sharpness. This is a design reference, not an Android dependency.
- [Android haptics APIs](https://developer.android.com/develop/ui/views/haptics/haptics-apis) and [BasicEnvelopeBuilder](https://developer.android.com/reference/android/os/VibrationEffect.BasicEnvelopeBuilder): capability checks and device-adapted intensity/sharpness envelopes.

## Implementation

`HapticDesign` defines original, finite signatures for navigation, sending, confirmation, rejection, gesture tension/release, card extraction/turn/landing, shuffling and tool completion. Each envelope starts from silence and ends at zero. Directions and physical materials are distinguished without making a supposedly favorable reading feel like a reward or an unfavorable one feel like a punishment.

Hardware discovery examines both the default vibrator and public individual actuators off the UI thread. API level or chipset alone does not imply actuator capabilities. On compatible API 36 hardware, perceptual envelopes express intensity and sharpness. Supported primitive scores combine rise, fall, low ticks, clicks, spins and thuds; every primitive is checked, including its advertised duration. Limited tick-only hardware still receives distinct multi-event scores rather than the same tick for every scene. Other amplitude-capable devices use smoothly tapered sampled waveforms; that path cannot independently reproduce sharpness. Compiled effects are cached and finite.

Devices without these controls use OEM-tuned `View.performHapticFeedback` semantics for selections, dense picker detents, toggles, gesture activation/deactivation, confirmations and cancellations. Physical collisions can use Android predefined impacts. Timing-only collision patterns are a last resort on older systems, never an attempt to fabricate analog intensity using rapid on/off duty cycles. No system-preference bypass, hidden vendor API, additional permission, service or network dependency is introduced.

Deliberate pager, calendar and curtain gestures use spatial detents with velocity-dependent spacing/rate and reversible activation thresholds. Stationary fingers produce no periodic buzz. Hysteresis prevents jitter at a threshold while allowing a deliberate return to cancel and re-arm. Gesture regions and functional thresholds are not expanded. Date/time wheels emit dense detents only during a user drag and its fling, with a separate settled snap. Programmatic positioning and protected system-Back edges produce no synthetic drag feedback. No meaningless pull-to-refresh is added.

Coverage includes navigation, tool menus, configuration choices, appearance choices, switches, schedule pickers/colors/save/delete, voice start/stop/cancel boundaries, card extraction/turn/landing, coin spin/impact, wheel detents and dice collisions. A priority arbiter drops stale or overlapping low-priority cues: a selection tick cannot interrupt a landing or duplicate a save's return-navigation response. Feedback is not emitted for ordinary text entry/scrolling, model token streaming, startup, rotation or favorable/unfavorable divination outcomes.

## Verification

Pure unit tests validate envelope limits, taper, sampling, supported-only compositions, slow-vendor-primitive fallback, arbitration, stationary-finger silence, fling limits and reversible hysteresis. The permission-protected debug fixture reports capability/backend and submitted versus suppressed requests, checks the disabled setting, and uses no account history or AI calls. A successful API request is not a measurement of motor acceleration or subjective comfort. Actuator quality, grip and vendor tuning still require physical evaluation. The code does not claim perceptual identity across devices or an exact reproduction of Threads.
