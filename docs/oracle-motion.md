# Reading Motion and Touch

Copper-coin casting and Rider-Waite draws share a presentation clock with their
haptic cues. `OracleMotion.kt` defines the timing and poses; local rule engines
still generate every coin value and card orientation. The motion code does not
redraw results or change AI prompts, requests, or budgets.

## Choreography

- Copper coins lift, rotate independently, land 86 ms apart, and settle before
  the line is published. The six-line sequence lasts about 7.2 seconds. Historical
  readings retain all eighteen coin outcomes and the original classical analysis.
- Tarot starts with a short gathering shuffle, then deals into ten fixed slots
  in two rows of five. Each card turns over 1,080 ms with a small lift, shadow,
  and moving reflection. The complete sequence lasts about 8.7 seconds.
- Reversed artwork keeps its orientation during the flip and in the saved
  reading. Pose sampling never changes the reading's stored values.

## Touch Policy

Short, rate-limited cues distinguish the lift, landing, shuffle, draw, turn, and
completion. Existing send confirmation remains in place; feature selection,
cancellation, multi-selection, and voice-state changes have lightweight semantic
feedback. An empty, unselected send action remains disabled and silent.

The shared controller respects application and system preferences. Unsupported
motors fall back to simpler feedback and omit dense secondary cues. Feedback is
only collected while the reading page is resumed. Late events are discarded,
not replayed after a stall or background return. See the
[Android haptic design principles](https://developer.android.com/develop/ui/views/haptics/haptics-principles).

## Rendering

The casting coins use an original Blender model with a true square opening,
rounded rims, raised cloud ornament, and beaded borders. A light brass sun and
three solid strokes identify the yang face; a dark patinated moon and three
broken strokes identify yin. These are application-designed visual identifiers,
not a claim that historical coins used a universal sun/moon convention. The
existing convention remains yang = 3, yin = 2.

Two transparent 512px model renders are sampled to 256px off the UI thread and
shared by all three coins (512 KiB decoded). Runtime drawing projects the visible
face and the outer and square-hole walls, retaining thickness through an edge-on
turn. This is a lightweight 2.5D presentation, not a new real-time Blender or
physics engine. It does not alter the casting timeline, haptics, line arithmetic,
stored readings, or AI interpretation. The geometry and lighting are reproducible
with `tools/build_oracle_coin_art.py`; the source scene is in `art/oracle-coin/`.
The cast shadow uses a smooth elliptical penumbra and a narrow contact component,
not an outlined tray or a clipped circular gradient. Height softens and offsets
the cast shadow while fading contact; turning changes its footprint, and horizontal
drift moves it with the coin. Reduced motion keeps both coin and shadow grounded.

Per-frame transforms and drawing stay in local graphics layers rather than
recomposing the conversation. Card artwork is decoded off the UI thread, at a
size appropriate for the spread, into an 8 MiB LRU cache. Shared card-back decode
work is coalesced. Completed motion clocks stop requesting frames; background
clocks suspend. Disabling system animations removes flight and rotation rather
than compressing them into a rapid flash.

## Verification

The motion tests check final coin faces, independent landings, result publication
after settling, hidden future card faces, final card poses, flip/cue alignment,
cue spacing, reduced motion, and preservation of input values.
Additional casting-art tests check transparent square holes, asset budgets,
grayscale face contrast, upright reverse projection, edge thickness, and bounded
projection throughout a complete rotation.

`OracleMotionPreviewActivity` is a synthetic, offline physical-device fixture in
the debug source set. It has no repository or API client and does not write
readings or settings. Its exported entry requires the signature-level Android
`DUMP` permission for shell testing. It is absent from the release manifest.

Physical-device checks include complete six-line and ten-card animations, motor
execution records, disabled feedback, background cancellation, and on-device
preference digest comparison. Debug frame statistics are diagnostic only, not a
release-build performance guarantee or a claim about every supported motor.
