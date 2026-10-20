# Semantic Icon Motion

`FortuneIcon` animates the **original Material vector**, not a hand-drawn approximation. A single source `VectorPainter` supplies the artwork throughout the cue and at rest. Filled, outlined, and auto-mirrored variants retain their own paths, stroke weight, intrinsic dimensions, tint, and layout behavior.

## Geometry And Continuity

- There is no alternate drawing, crossfade to a different glyph, or final-frame asset replacement.
- Semantic regions select parts of the source image: calendar dates turn inside their frame, book leaves move around their spine, tool tiles separate and assemble, clock hands advance, and paper-plane wings fold. Dice pips never switch to an unrelated drawing or count.
- Region masks use disjoint integer-pixel boundaries. Stationary partitioned frames precede the single-draw resting path, allowing their raster output to be checked directly for seams.
- Every part starts at its identity pose and returns smoothly to that pose before the cue completes. The closing frames use the unchanged source vector, without introducing new artwork, proportions, or opacity.
- The Material `Icon` composable remains responsible for sizing, alignment, accessibility, and tint for both animated and static icons. There is no separate Canvas viewport or fixed-size wrapper.
- RTL motion mirrors region coordinates and direction while leaving the original vector's auto-mirroring intact.

## Playback And Cost

- Cues last 680 ms and do not loop. They never initiate haptics, navigation, recordings, draws, or network requests.
- One playback coroutine owns the phase. Rapid presses do not cancel a running pose and snap it back to the start, nor build up a replay backlog.
- Ordinary recomposition does not restart a cue. Selected navigation items and explicitly keyed changes can replay. Visible cues settle briefly when deselected; disabled send, reduced-motion, and background icons become still immediately.
- Phase reads occur during drawing. The source vector's raster cache is reused by moving regions; no paths, alternate vectors, GIFs, video, or model assets are generated per frame.
- No per-icon timers keep running after a cue finishes. Safety, delete, visibility, close, and unknown symbols retain the standard static Material artwork.

## Verification

`IconMotionPolicyTest` covers meaning selection and motion permissions. `IconPartMotionTest` checks region partitioning, finite poses, smooth excursion endpoints, and exact identity throughout the closing interval. The debug-only icon fixture compares the original vector and animated painter on a physical device, including outline/filled variants, multiple sizes, transparency, RTL, and frames around the moving/resting boundary. It uses only bundled icons and synthetic state, with no access to account data or AI services.

Physical-device verification on 2026-10-20 completed **8,037 comparisons across 47 source icon variants**, with zero differing pixels at the sampled identity, partitioned-rest, and final frames. This includes 7,896 native bitmap comparisons across six dimensions, two directions, two tints, and seven phase samples, plus 141 on-screen GPU comparisons. Intermediate moving frames intentionally differ from the static glyph. This is evidence of endpoint raster equivalence in the tested configurations, not a claim of zero frame jank on every device.
