# Adaptive Layout

Phone landscape is a short, wide window, not a stretched portrait screen. The
layout policy uses current window dimensions and the available content width,
not model names or physical screen resolution. A split-screen window narrower
than 600 dp keeps the compact layout. Taller tablet windows retain the existing
medium/expanded presentation.

- Short windows use a compact, scrollable side rail with full labels and 48 dp
  minimum navigation targets. Rail width includes the measured label width,
  icon, gap, and padding. Subpage toolbars start at 48 dp and grow with the
  actual action-text line height so larger text does not clip button outlines.
- Home separates the daily overview from insights and almanac information;
  its two main actions remain outside the overview's scroll area.
- The calendar separates the month grid from month/year selection, the selected
  date's details, and schedule actions. Four-, five-, and six-week months size
  their rows from available height. Larger accessibility text can scroll rather
  than being reduced or clipped; the year picker remains available when vertical
  scrolling is needed. Horizontal month changes are unchanged.
- The tool catalog uses its actual pane width. Coin and wheel tools put controls
  beside a height-bounded scene. Landscape dice use an unframed, expanded table
  beside a full-height counter; up to six individual results occupy two rows.
  Counter width follows the available pane and text scale rather than a fixed
  device size. Count, sides, and roll controls remain in the toolbar, outside the
  curtain. Its small handle follows the table center; the curtain still covers
  both the table and the complete result counter.
- Landscape reading animations use side-by-side casting and six-line progress,
  or a compact two-by-five tarot spread. Their timelines, haptics, fixed outcomes,
  and interpretation logic do not change. The selected tool sits inside the
  landscape composer row to free conversation space.
- The tarot library reduces fixed chrome and uses complete, height-bounded
  thumbnails. Card details retain the complete artwork and text. Profile and
  account entries use a two-column overview.

Insets remain owned by the app scaffold. Tool headers do not apply them again.
The content scaffold keeps the same composition location when switching between
bottom navigation and the side rail, so saved subpage state is not re-keyed by
the surrounding navigation layout.
Lightweight tool counts, results, and the dice curtain's closed state use saved
instance state across configuration changes; physics frames are not serialized.

The pure policy tests cover window classification, catalog widths/font scales,
calendar week counts, dice pane proportions, and readable large-text fallbacks. Real-device checks use
offline fixtures for casts and cards and do not submit AI requests or save test
readings. Debug screenshots are diagnostic, not a guarantee for every device.
