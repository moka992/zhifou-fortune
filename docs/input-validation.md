# Composer and Speech Validation

The oracle composer retains `TextFieldValue` selection and composing ranges instead of reconstructing them on each keystroke. External question changes move the caret to the end. Material's multiline editor owns its scrolling, with a three-line landscape limit and five-line portrait limit. The screen uses Compose IME insets and yields the compact navigation bar while the keyboard is visible.

Speech capture releases `AudioRecord` before local decoding or cloud transcription. A typed start result distinguishes recording, transcription, cleanup, missing model, permission and actual microphone failures. A second hold during transcription is rejected as busy without discarding the first result.

## Oracle Navigation

Daily inspiration and messages are sibling pages in the standard Compose `HorizontalPager`: inspiration on the left, messages on the right. Swipe right from messages for inspiration; swipe left from inspiration for messages. Horizontal paging is restricted to the content region; the shared composer is outside it. The plus menu contains only reading tools. Vertical scrolling never changes pages.

Paging must start away from the physical window edges: each side reserves at least 32dp, or the reported system gesture inset plus 8dp when wider. A passive initial-down observer and an ancestor nested-scroll gate suppress only horizontal movement/fling for edge-origin gestures. Taps and vertical scrolling remain available, no visible space is removed, and no system gesture exclusion rect is requested. Window coordinates avoid creating a spurious dead strip beside a navigation rail. The origin stays authoritative until the next gesture, including release and fling; native pager animations and programmatic navigation are unaffected.

The message `LazyListState` remains owned by the parent screen, so returning from inspiration does not jump to the latest message. Both sibling pages use the same primary-destination Back behavior: System Back returns Home, not to the other page. The inspiration page deliberately has no visible return button. Native pager accessibility scroll actions remain available. The recommendation list can scroll independently without blocking horizontal paging. Selecting a recommendation fills the draft and returns to messages, without sending or calling a model.

The daily-first-visit gate still chooses the initial page. After that, the saved pager state is authoritative, including for an empty timeline; an empty-state fallback must not force users back to inspiration after they explicitly leave it. New reading/chat content follows the existing timeline follow policy and does not redraw or change any reading result.

## Page Navigation

System Back and visible page returns share the same parent routes. Child handlers take precedence: a wiki card returns to its library; wheel settings/history return to the wheel; schedule edits return to their source page; account settings return through Settings and My. At a main destination's root, Back returns Home. Home leaves Back to Android rather than trapping the user in the application. Existing configuration-discard confirmation and native dialog/menu dismissal remain in place.

Secondary pages use a shared, transparent Material 3 top app bar with a 48dp Back target, a single-line title and trailing actions. App-level safe insets are not applied a second time inside the bar. Dice, coin and wheel bars stay outside their scrolling content; wheel subpages have distinct titles and independent scroll positions.

The tools catalog's scroll state is owned above tool destinations, so leaving a tool restores the catalog at its existing offset, without keeping rendering engines active in the background. Calendar scroll state similarly survives detail/editor navigation; memorial and event-detail positions use scoped saved state. Editor cancellation does not retain abandoned drafts. Wiki search/filter changes reset the library only when those inputs change, not whenever card details close; the library and system section each retain their own list state.

## Automated Checks

- `OracleEditorTest` covers composition, selection, external edits, clearing and busy-message semantics.
- `OracleDailySuggestionsTest` covers the daily gate, initial page policy and safe offline recommendation content; `OracleTimelineStateTest` covers the unchanged follow-new-content policy.
- `OraclePagerGesturesTest` covers page order, both protected edges, center-origin paging, wider/asymmetric system insets, gesture lifetime, vertical-axis isolation and invalid coordinates.
- `AppNavigationTest` covers primary-destination Back behavior; `MineNavigationTest` covers account/settings parent routes.
- `SpeechBusyPreviewActivity` is a shell-only debug fixture protected by `android.permission.DUMP`. It uses real capture and a delayed in-memory decoder, without preferences, provider credentials or network requests. Check a second start during transcription and a successful restart after completion.
- `tools/android-fixtures/` contains an optional local IME for reproducing a full-height keyboard on devices whose normal IME changes to hardware-keyboard mode during ADB input. These files are not included in normal debug or release builds. Temporary installation requires copying the source/XML into debug sources and registering an input-method service with `android.permission.BIND_INPUT_METHOD`. Capture the original default/enabled IMEs first, and always restore them and clear synthetic drafts afterward.

Physical-device validation used a 280dp native keyboard: the populated composer's bottom was 961px, above the keyboard's 988px top. The final synthetic suffix remained visible after more than five lines of input. No chat or reading was submitted, and the original input method and protected account/record digests were unchanged.
