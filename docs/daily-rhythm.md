# Daily rhythm and daily guidance

## Scope

Daily rhythm is an offline, deterministic editorial index for planning and reflection. It is **not** a probability of good fortune, a measurement of a person's abilities, an empirical forecast, or a formula found in a classical text. Almanac conventions vary between traditions; this project uses the existing `lunar-java` calculations consistently rather than claiming universal agreement.

Calendar facts, traditional associations, classical quotations, and modern advice are kept distinct. The implementation does not generate an unsolicited daily hexagram or treat a nickname, birthday, zodiac clash, or auspicious direction as evidence that an event will happen.

## Calculation principles

1. Match almanac activities against an exact, reviewed allowlist of everyday activities. Unknown activities and funeral-related content are excluded from home-page guidance and daily AI input.
2. Group synonymous or closely related activities. For example, `交易` and `立券` form one agreement signal, while `开市`, `开工`, and `赴任` belong to one work-start group. Repetition and list order do not increase the score.
3. Compare suitable and avoid signals separately for career, relationships, and finance. An agreement can be relevant to both career and finance, without allowing repeated terms to dominate the result.
4. If the same group appears on both sides, its positive and negative contributions cancel. Conflicting evidence calls for review rather than a favorable headline.
5. Broad restrictive entries, including an almanac with both sides restricted, suppress positive domain conclusions and produce restrained, practical guidance. The raw restrictive wording is not repeated on the home page.
6. Compare the day stem's phase with the solar month's branch phase using traditional generation and control associations. Missing phase information is neutral. This comparison informs an application-authored index, not a formula or empirical effect attributed to the classics.

The home page presents the dial and practical guidance without calculation breakdowns. Settings > Data Sources & Notes (`我的 > 设置 > 数据来源与说明`) contains public sources, calculation principles, privacy information, and the application-wide disclaimer. Source and method notes describe the evidence and its limitations, not the index's internal calibration.

The solar month follows `lunar-java`'s `monthInGanZhi`, which changes on the calendar day of a **jie** boundary. It does not change simply because a Gregorian or lunar month begins. Day-level granularity keeps a daily result stable rather than changing during a solar-term hour. `甲乙 / 丙丁 / 戊己 / 庚辛 / 壬癸` map to wood / fire / earth / metal / water; `寅卯 / 巳午 / 辰戌丑未 / 申酉 / 亥子` use the corresponding branch-phase associations. This is a simplified traditional reference, not a full strength, eight-character, or useful-god analysis.

Empty or unknown almanac activities add no evidence. Five-phase relations never establish medical outcomes, relationship compatibility, investment returns, or personal destiny. Higher values suggest a more active planning emphasis within this editorial scale; lower values suggest review and consolidation, not bad luck.

## Guidance

Each category selects a short headline from its evidence and, for neutral cases, the day's phase as an interpretive lens. The explanation distinguishes the actual almanac evidence, a short classical citation with its chapter, and an independently written modern application. Trigram meanings are illustrative references, **not** a claimed result of casting a hexagram.

Recognized profile keywords select relevant areas of attention without changing the numeric index. Birthday proximity adds an annual review note only; no birth time, place, location permission, weather request, or AI connection is required. A nickname labels the reading and does not alter the score.

Daily AI explanations receive the solar month, seasonal comparison, internal calculation context, and each category's evidence, explicitly labelled as editorial interpretation rather than an ancient scoring formula. The model is instructed not to repeat calculation details in its response. They continue to use their separate prompt and existing once-per-day category cache, with the existing 700-output-token cap. Changes here do not alter copper-coin casting, tarot draws, Book of Answers, or their AI prompts. Existing daily AI results are retained rather than invalidated and requested again on upgrade.

The date-specific almanac cache includes the solar-month stem/branch. Its schema is versioned so an older cache is recalculated once locally. Routine page switching reuses the ViewModel snapshot; the scoring layer does not access the network or load a speech model.

## Textual references

The project uses short excerpts of public-domain Chinese originals, normalized to simplified characters. Modern wording and numeric weights are the project's own, not attributed to the original authors or a modern translator.

- [Dao De Jing](https://ctext.org/dao-de-jing/zh): chapters 5, 8, 9, 44, 63, and 64; restraint in speech, trustworthiness, knowing limits, small beginnings, and care through completion.
- [Zhou Yi, Shuo Gua](https://ctext.org/book-of-changes/shuo-gua): the meanings of the eight trigrams. Movement, entering, stopping, responsiveness, and other images inform reflection rather than predicting events.
- [Zhou Yi, Dui, Xiang](https://ctext.org/book-of-changes/yi-jing/zh): learning through exchange with friends.
- [Shang Shu, Hong Fan](https://ctext.org/shang-shu/great-plan/zh): the five phases' descriptive qualities. This passage does not contain this app's score or its seasonal weights.
- [Li Ji commentary, seasonal five-phase discussion](https://ctext.org/wiki.pl?chapter=779549&if=en): traditional generation, control, and seasonal associations; separate from the Zhou Yi's primary text.
- [lunar calendar documentation](https://6tail.cn/calendar/api.html): stem/branch, solar terms, and daily almanac calculation APIs.

## Regression coverage

Tests cover stable results, profile independence of the index, exact allowlist matching, duplicate and synonym suppression, contradictory or restrictive activities, all phase pairings, solar-month boundaries, empty-data fallback, display consistency, ordered responses to evidence, and a complete year's safe compact guidance. Disclosure tests check source attribution, external links, and separation between public method notes and internal calculation details. Existing daily AI prompt-safety and divination tests remain part of the suite.
