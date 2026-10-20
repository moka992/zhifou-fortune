package com.zhifou.fortune

enum class DailyInsightCategory(val key: String, val label: String) {
    Career("career", "事业"),
    Relationship("relationship", "关系"),
    Finance("finance", "财务"),
}

enum class DailyInsightTone { Favorable, Balanced, Cautious }

data class DailyInsight(
    val category: DailyInsightCategory,
    val headline: String,
    val summary: String,
    val tone: DailyInsightTone,
    val advice: String = "",
)

data class DailyAiInsight(val date: String, val category: DailyInsightCategory, val content: String)

enum class AiConnectionStatus { Unconfigured, NeedsValidation, Checking, Connected, NoNetwork, Failed }

internal data class DailyDomainSignal(
    val category: DailyInsightCategory,
    val balance: Int,
    val suitable: List<String>,
    val avoid: List<String>,
    val conflicting: Boolean,
    val restricted: Boolean,
)

internal object DailyInsightEngine {
    private data class Activity(val terms: Set<String>, val categories: Set<DailyInsightCategory>)
    private data class Guidance(val headline: String, val action: String, val classic: String)

    // Synonyms share one signal: related terms must not count as separate opportunities.
    private val activities = listOf(
        Activity(setOf("开市", "开工", "赴任", "上任", "求职"), setOf(DailyInsightCategory.Career)),
        Activity(setOf("交易", "立券"), setOf(DailyInsightCategory.Career, DailyInsightCategory.Finance)),
        Activity(setOf("入学", "求学", "习艺"), setOf(DailyInsightCategory.Career)),
        Activity(setOf("修造", "扫舍"), setOf(DailyInsightCategory.Career)),
        Activity(setOf("会亲友"), setOf(DailyInsightCategory.Relationship)),
        Activity(setOf("嫁娶", "纳采", "订盟"), setOf(DailyInsightCategory.Relationship)),
        Activity(setOf("出行"), setOf(DailyInsightCategory.Relationship)),
        Activity(setOf("纳财", "开仓"), setOf(DailyInsightCategory.Finance)),
        Activity(setOf("置产", "求财", "借贷"), setOf(DailyInsightCategory.Finance)),
    )
    private val safeTerms = activities.flatMap { it.terms }.toSet()
    private val restrictiveTerms = setOf("诸事不宜", "馀事勿取", "余事勿取")

    fun safeSuitable(almanac: DailyAlmanacInfo): List<String> = sanitize(almanac.suitable)
    fun safeAvoid(almanac: DailyAlmanacInfo): List<String> = sanitize(almanac.avoid)
    fun safeActivities(items: List<String>): List<String> = sanitize(items)

    private fun sanitize(items: List<String>): List<String> = items
        .map(String::trim).filter { it in safeTerms }.distinct().sorted()

    fun signals(almanac: DailyAlmanacInfo): List<DailyDomainSignal> {
        val suitable = safeSuitable(almanac).toSet()
        val avoid = safeAvoid(almanac).toSet()
        val restricted = (almanac.suitable + almanac.avoid).any { it.trim() in restrictiveTerms }
        return DailyInsightCategory.entries.map { category ->
            val groups = activities.filter { category in it.categories }
            val positive = groups.filter { group -> group.terms.any { it in suitable } }.toSet()
            val negative = groups.filter { group -> group.terms.any { it in avoid } }.toSet()
            val conflicting = positive.intersect(negative).isNotEmpty()
            val net = positive.size - negative.size
            val balance = (if (restricted) minOf(net, 0) - 1 else net).coerceIn(-2, 2)
            DailyDomainSignal(
                category, balance,
                suitable.filter { item -> groups.any { item in it.terms } },
                avoid.filter { item -> groups.any { item in it.terms } },
                conflicting, restricted,
            )
        }
    }

    fun create(almanac: DailyAlmanacInfo, keywords: List<String> = emptyList()): List<DailyInsight> {
        val seasonal = DailyTraditionEngine.seasonalContext(almanac)
        return signals(almanac).map { signal ->
            val tone = when {
                signal.restricted || signal.balance < 0 -> DailyInsightTone.Cautious
                signal.balance > 0 && !signal.conflicting -> DailyInsightTone.Favorable
                else -> DailyInsightTone.Balanced
            }
            val guidance = guidance(signal.category, tone, seasonal.day)
            val evidence = when {
                signal.conflicting -> "同类事项的宜忌有交叉，不取单一吉凶判断，先核对实际条件。"
                signal.restricted -> "当天可直接用于日常安排的依据较少，以整理和保留余量为主。"
                signal.suitable.isNotEmpty() && signal.avoid.isNotEmpty() ->
                    "日常相关事项中，宜${signal.suitable.joinToString("、")}，忌${signal.avoid.joinToString("、")}，需区分场景。"
                signal.suitable.isNotEmpty() -> "黄历宜${signal.suitable.joinToString("、")}，可借其意作小步安排，并非结果保证。"
                signal.avoid.isNotEmpty() -> "黄历忌${signal.avoid.joinToString("、")}，可借其意增加复核，不必因此否定正常安排。"
                else -> "当天没有与此项直接对应的日常宜忌，不额外推断好坏。"
            }
            val focus = keywords.firstOrNull { keyword -> keywordCategory(keyword) == signal.category }
            val personal = if (focus == null) "" else " 可从你关注的“${focus.take(24)}”中选一件小事落实。"
            DailyInsight(signal.category, guidance.headline,
                "$evidence ${guidance.classic} 现代参考：${guidance.action}$personal", tone,
                guidance.action + personal)
        }
    }

    internal fun keywordCategory(keyword: String): DailyInsightCategory? = when {
        listOf("工作", "事业", "学习", "求职", "项目", "考试").any { it in keyword } -> DailyInsightCategory.Career
        listOf("关系", "沟通", "亲友", "家庭", "感情", "伴侣").any { it in keyword } -> DailyInsightCategory.Relationship
        listOf("财务", "预算", "存钱", "收入", "支出", "理财").any { it in keyword } -> DailyInsightCategory.Finance
        else -> null
    }

    private fun guidance(category: DailyInsightCategory, tone: DailyInsightTone, phase: FivePhase?): Guidance =
        when (category) {
            DailyInsightCategory.Career -> when (tone) {
                DailyInsightTone.Favorable -> Guidance("先做关键一步", "把主要任务拆成可验证的一步，完成后再扩展。",
                    "《说卦》“震，动也”；《道德经》第六十三章“图难于其易，为大于其细”。")
                DailyInsightTone.Cautious -> Guidance("先核对再推进", "先补齐信息与交付条件，对不确定的承诺留出调整空间。",
                    "《说卦》“艮，止也”；《道德经》第六十四章“为之于未有，治之于未乱”。")
                DailyInsightTone.Balanced -> when (phase) {
                    FivePhase.Wood -> Guidance("先规划再行动", "先写清目标与边界，再做一次小范围尝试。",
                        "《说卦》“巽，入也”；《道德经》第六十四章“千里之行，始于足下”。")
                    FivePhase.Fire -> Guidance("表达重在落实", "把想法说清楚，也约定一个可以确认的交付点。",
                        "《说卦》“离，丽也”；《道德经》第六十四章“慎终如始”。")
                    FivePhase.Earth -> Guidance("先把基础做稳", "梳理手头事项，完成一项能为后续铺路的工作。",
                        "《说卦》“坤，顺也”；《道德经》第六十四章“九层之台，起于累土”。")
                    FivePhase.Metal -> Guidance("聚焦一项重点", "先排优先级，减少同时开展的任务。",
                        "《说卦》“艮，止也”；《道德经》第四十四章“知止不殆”。")
                    else -> Guidance("观察后再调整", "先确认一处信息缺口，得到反馈后再调整计划。",
                        "《说卦》“坎，陷也”；《道德经》第六十四章“为之于未有，治之于未乱”。")
                }
            }
            DailyInsightCategory.Relationship -> when (tone) {
                DailyInsightTone.Favorable -> Guidance("主动表达善意", "以一次具体交流了解彼此需要，不替对方作决定。",
                    "《周易·兑·象》“君子以朋友讲习”；《道德经》第八章“言善信”。")
                DailyInsightTone.Balanced -> when (phase) {
                    FivePhase.Wood -> Guidance("先了解彼此期待", "先问清对方需要什么，再提出一个双方可调整的安排。",
                        "《说卦》“巽，入也”；《道德经》第八章“言善信”。")
                    FivePhase.Fire -> Guidance("表达清楚不过量", "把自己的期待说具体，避免一次要求对方回应太多。",
                        "《周易·兑·象》“君子以朋友讲习”；《道德经》第五章“多言数穷，不如守中”。")
                    FivePhase.Earth -> Guidance("体谅也守边界", "照顾共同安排，也如实说明自己能承担的部分。",
                        "《说卦》“坤，顺也”；《道德经》第四十四章“知止不殆”。")
                    FivePhase.Metal -> Guidance("分歧先讲事实", "先把事实与个人感受分开，再讨论能够共同确认的部分。",
                        "《说卦》“艮，止也”；《道德经》第八章“言善信”。")
                    else -> Guidance("先听后说", "先确认对方的意思，再说明自己的期待和边界。",
                        "《说卦》“巽，入也”；《道德经》第五章“多言数穷，不如守中”。")
                }
                DailyInsightTone.Cautious -> Guidance("给沟通留余地", "分歧出现时先复述事实，重要回复不必急于当场作出。",
                    "《说卦》“艮，止也”；《道德经》第八章“夫唯不争，故无尤”。")
            }
            DailyInsightCategory.Finance -> when (tone) {
                DailyInsightTone.Favorable -> Guidance("按预算小步安排", "先核对收支和约定条件，只安排能够承担的日常支出。",
                    "《说卦》“坤，顺也”；《道德经》第四十四章“知足不辱，知止不殆”。")
                DailyInsightTone.Balanced -> when (phase) {
                    FivePhase.Wood -> Guidance("计划先定上限", "新计划先列出预算上限，再决定是否需要这笔支出。",
                        "《说卦》“巽，入也”；《道德经》第六十三章“图难于其易，为大于其细”。")
                    FivePhase.Fire -> Guidance("决定前停一停", "付款前再核对需求和总费用，不把当下兴致当成长期需要。",
                        "《说卦》“艮，止也”；《道德经》第四十四章“知止不殆”。")
                    FivePhase.Earth -> Guidance("收支保持清楚", "核对一笔账目，区分必要支出与可以稍后决定的项目。",
                        "《说卦》“坤，顺也”；《道德经》第四十四章“知足不辱”。")
                    FivePhase.Metal -> Guidance("条款再核对", "核对价格、额外费用和约定，优先消除一处模糊条件。",
                        "《说卦》“艮，止也”；《道德经》第六十四章“慎终如始”。")
                    else -> Guidance("留一点周转余量", "先看近期固定支出，给临时需要留出可支配的余量。",
                        "《说卦》“坎，陷也”；《道德经》第六十四章“为之于未有，治之于未乱”。")
                }
                DailyInsightTone.Cautious -> Guidance("先守住预算", "增加支出前核对预算、费用与退出条件，不凭黄历决定投资。",
                    "《说卦》“艮，止也”；《道德经》第九章“持而盈之，不如其已”。")
            }
        }
}
