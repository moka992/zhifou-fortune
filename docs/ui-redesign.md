# Interface Design

## Direction

The interface uses a quiet Material 3 foundation: clear typography, neutral content
surfaces, restrained motion, and distinct colors for primary actions, supporting
information, and personal content. Refractive effects are limited to navigation;
reading surfaces no longer rely on transparent layers to establish hierarchy.

References informed the information hierarchy and material restraint, not the
reuse of third-party logos, screenshots, or proprietary artwork:

- [Gemini visual design](https://design.google/library/gemini-ai-visual-design)
- [2026 Apple Design Awards: Moonlitt and Tide Guide](https://www.apple.com/newsroom/2026/06/apple-reveals-winners-of-the-2026-apple-design-awards/)

## Design Tokens

| Role | Light | Dark |
| --- | --- | --- |
| Background | `#F7F8F7` | `#151918` |
| Content surface | `#FFFFFF` | `#1C221E` |
| Primary | `#216451` | `#ACD5BE` |
| Secondary | `#4B647A` | `#B7CCD9` |
| Tertiary | `#A8534C` | `#E5B0A4` |
| Primary text | `#222826` | `#E8EEE8` |
| Secondary text | `#666F6B` | `#AAB6AD` |

Typography uses the system sans-serif family with zero letter spacing. Body
copy follows system font scaling. Compact layouts use the same type hierarchy;
calendar rows and navigation heights grow when larger text needs more space.
Wide windows use the same side navigation in portrait and landscape. Short
windows distribute its five destinations over the available height; taller
windows keep the destinations together near the top.

Home content is ordered as daily context, primary actions, category summaries,
and the almanac. The calendar remains unframed. Tool cards use real artwork
with consistent framing. The profile page groups identity, counters, and actions.
The chat composer retains the tool menu, removable selection, and disabled-send
state. Empty eligible history shows daily suggestions rather than a blank page.

The visual refactor does not replace fortune calculations, lunar-calendar data,
AI prompts, model settings, speech recognition, dice physics, or stored records.
Wallpaper selection remains available with a subdued overlay for readability.

The daily rhythm dial has 41 ticks and a single entrance sweep. It uses the
existing daily score and does not imply a new statistical model. Career,
relationship, and finance summaries have blue, coral, and green category
surfaces. With larger text or a narrow window they become full-width rows.

## Artwork

Three new images were generated with the built-in image generation tool, then
resized to 768 px and compressed as JPEG for offline use. The original tarot
deck remains unchanged. Final project assets:

- `app/src/main/res/drawable-nodpi/tool_dice_refined.jpg`
- `app/src/main/res/drawable-nodpi/tool_wheel_refined.jpg`
- `app/src/main/res/drawable-nodpi/tool_coin_refined.jpg`

Generation prompt specifications:

**Dice**: A square studio product illustration of two real six-sided dice. One
matte porcelain-white die with forest-green pips, one sage die with ivory pips.
Correct cubic geometry, soft studio shadows, centered subject occupying about
70% of the frame, pale green `#E4EEE7` background. No text, logos, neon, gold, or
decorative glow.

**Wheel**: A square studio product illustration of a small six-segment decision
wheel on a simple stand. Slate blue, sage, soft coral, and porcelain segments;
a thin brushed-silver rim and coral pointer. Pale blue `#E5EDF5` background,
soft light, centered complete object. No lettering, logos, gold, or sparkles.

**Coins**: A square studio product illustration of two satin-silver coins with
raised rims and a subtle rosy cast. One standing coin bears an engraved plum
blossom, the other lies flat with a square recess. Pale blush `#F5E6E1` backdrop,
soft light and contact shadows. No lettering, logos, gold, or sparkles.

## Verification

The debug build and the existing unit-test suite are run offline. Additional
theme tests verify primary and secondary text contrast in both modes, chat
bubble contrast, and the primary action color pair. Screen captures and
interaction checks use an Android 15 emulator; they do not substitute for
physical-device GPU, speech, or vibration testing.

No production AI credentials, personal profiles, or remote inference requests
are used during visual verification. No release or repository publication is
performed by the design update.

### Validation Record

The final debug build passes all 54 unit tests across 12 suites, including the
new light/dark contrast checks. The emulator matrix covers:

| Configuration | Checks |
| --- | --- |
| 1080 x 2400, 420 dpi | Five primary pages, light and dark themes |
| 720 x 1280, 360 dpi, 1.3x text | Wrapping, scroll access, composer, navigation |
| 2400 x 1080, 420 dpi | Side navigation, system insets, state after rotation |
| 1600 x 2560, 320 dpi | Tablet content widths and navigation consistency |

Local interaction checks include the tool picker, empty-question divination,
answer-book output, six coin casts, ten-card tarot reveal, dice rolling, coin
tossing, wheel spinning, wiki navigation, and schedule-editor cancellation.
These checks verify paths and rendering, not the subjective quality of haptics
or frame-time guarantees on low-end physical devices. Small windows and larger
text retain scrolling rather than shrinking body text to fit.

Review captures are kept locally under `captures/ui-redesign/`, which is
excluded from version control. The debug APK is generated at
`app/build/outputs/apk/debug/app-debug.apk`.
