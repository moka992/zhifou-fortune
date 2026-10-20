# Zhifou Fortune

Native Android / Kotlin / Jetpack Compose. Preserve offline functionality, existing user data, accessibility, and responsive layouts when changing a feature. Account settings and real readings are private, not development fixtures.

## Relevant Modules

- Runtime reading prompts: `OraclePrompts.kt`, generated from the standalone [oracle skill](https://github.com/moka992/zhifou-oracle-skills). Use its exporter for shared prompt changes.
- Structured reading payload and local preflight: `OraclePromptBuilder.kt`.
- Copper-coin arithmetic and classical selection: `MainActivity.kt` / `ZhouyiDivination.kt`; source corpus: `ZhouyiClassics.kt`.
- Tarot draws and local references: `TarotDeck.kt`; original reflection entries: `AnswerBook.kt`.
- Daily rhythm and category advice: `DailyFortune.kt`, `DailyInsights.kt`, `DailyTradition.kt`.
- Provider protocols and credentials: `AiApiTransport.kt`, `AiProviderCatalog.kt`, and the separate speech provider adapter.

This file guides development agents. It is not automatically sent to the app's API models.

## Invariants

Local code owns casts, card draws, line selection, and numeric calculations. AI interprets fixed results; it does not redraw, invent missing passages, or silently replace a tradition. Keep source text, later conventions, modern summaries, and application-defined weights distinct. Preserve CC BY-SA notices for the adapted classical corpus and the separate historical artwork notices.

Maintain the completion marker and continuation handling, current provider/model choices, and per-feature output/caching contracts. Home-category AI uses its own daily cache and 700-output-token limit; prompt work must not silently expand budgets or enable new paid calls. Avoid verbose chain-of-thought or repetitive verification instructions; use local validation for deterministic facts.

Use synthetic fixtures for tests. Local JVM tests do not access paid APIs and can be run and iterated without per-run approval. A normal check is `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:lintDebug --offline`. Use an installed JDK compatible with Gradle and the project's Android SDK. For device testing, use the user's authorized physical device; do not substitute emulator-only results for a real-device request.

Never commit API keys, preferences, user history, device identifiers, signing secrets, or local machine paths. New network calls, live-provider tests, releases, and public uploads must follow the user's actual authorization rather than inferring it from these repository notes.
