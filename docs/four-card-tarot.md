# Four-Card Tarot

The Tools catalog includes a manually picked, four-card Rider-Waite-Smith reading, separate from the existing ten-card Celtic Cross in Oracle.

## Method and Sources

There is no single universally canonical four-card spread. This implementation uses the modern **Cross** variant documented by [TaroTerra](https://taroterra.com/en/rozklady-taro/khrest/): situation in the center, resistance above, resources to the left, and a conditional direction to the right. Resources are also used as a practical advice lens. This is not presented as Waite's original Celtic Cross or as a scientifically validated way to predict events.

The existing complete 78-card deck, historical artwork, and original Chinese summaries are reused. Card symbolism and upright/reversed background refer to A. E. Waite's [The Pictorial Key to the Tarot](https://sacred-texts.com/tarot/pkt/index.htm). Position-specific applications are original modern writing, not attributed quotations. Existing artwork notices remain in `THIRD_PARTY_NOTICES.md`.

Interaction research includes [Labyrinthos' own description of manual digital draws and intention setting](https://app.labyrinthos.co/get-reading/). This project does not copy that app's artwork or interface. The visual hierarchy follows [Apple's distinction between glass controls and content](https://developer.apple.com/videos/play/wwdc2025/219/) and [Material's meaningful spatial motion](https://m3.material.io/styles/motion/overview/how-it-works). The native Android implementation follows the [Compose animation performance guidance](https://developer.android.com/develop/ui/compose/animation/quick-guide): cached bitmaps, draw-phase transforms, finite effects, and reduced-motion support.

## State and Privacy

- An unbiased Fisher-Yates shuffle uses `SecureRandom`. All 78 identities and optional independent orientations are fixed before any pick. Each slot may be picked once; exactly four draws form a result.
- The question is optional. Its absence is preserved; neither local text nor AI invents a silently held question.
- Small saveable session state preserves the committed deck and selection order across configuration changes. Animation is presentation only.
- Completed results use the existing local reading store and retention policy. Older ten-card records retain their meanings and rendering.
- AI is opt-in per reading. No network request is made by washing, picking, revealing, or reopening a completed result. An explicit request sends the fixed four cards, question, and optional nickname/keywords to the user's configured provider. Birthday, location, and almanac data are not sent for this feature.
- Successful AI text is stored with that reading. Reopening cannot silently redraw cards or request another interpretation. Failed requests can be retried explicitly.

## Rendering

The continuous light/dark stage uses historical front artwork and an original foil-backed design. Android 13 and later render the foil and art-preserving clear coat with local AGSL shaders; earlier devices use a simpler Canvas treatment without changing the draw. Reflections respond to card motion and the app's existing tilt controller. Floating header controls use the app's real backdrop compositor, rather than baking glass into a card image. No additional permissions or network assets are required.

Perspective deck motion, paper edges, contact shadows, and the slow two-sided reveal share exact resting endpoints. The flat cards do not need an additional Blender mesh or renderer. Artwork is loaded off the UI thread through the shared bounded cache. The picker lazily draws an overlapping fan, not 78 simultaneously animated surfaces. Idle screens have no particle loop. Numbered cards are presented in two columns, or one row in a short landscape window; this presentation does not change the four positions or their draw order. Landscape places controls beside the stage, while portrait uses bounded setup controls and a scrollable completed result. System animation disabling removes large motion while preserving the same outcomes.

## Verification

`FourCardTarotTest` checks deck completeness, selection uniqueness/order, restoration, invalid records, history serialization, local position semantics, AI data isolation, old Celtic Cross compatibility, and reveal endpoints. `TarotAtelierTest` checks layout anchors and text contrast in both materials. Tests use synthetic inputs, never credentials or live providers. The debug-only `FourCardTarotPreviewActivity` provides the real interaction with an in-memory record sink and shader compilation checks for physical-device verification without changing private history or invoking AI.

Verification covers the JVM suite, debug assembly, and Android Lint. Physical-device scenarios include manual selection with and without a written question, rotation during a partial draw, four unique completed cards, portrait and short landscape layouts, card details, system back, larger text, and reduced-motion presentation. Provider requests are validated as structured fixtures; no paid live-provider test is included.
