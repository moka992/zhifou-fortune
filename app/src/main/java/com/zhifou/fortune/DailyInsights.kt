package com.zhifou.fortune

import java.security.MessageDigest

enum class DailyInsightCategory(val key: String, val label: String) {
    Career("career", "事业"),
    Relationship("relationship", "关系"),
    Finance("finance", "财务"),
}

enum class DailyInsightTone {
    Favorable,
    Balanced,
    Cautious,
}

data class DailyInsight(
    val category: DailyInsightCategory,
    val headline: String,
    val summary: String,
    val tone: DailyInsightTone,
)

data class DailyAiInsight(
    val date: String,
    val category: DailyInsightCategory,
    val content: String,
)

enum class AiConnectionStatus {
    Unconfigured,
    NeedsValidation,
    Checking,
    Connected,
    NoNetwork,
    Failed,
}

internal object DailyInsightEngine {
    private data class DomainRules(
        val suitableTerms: Set<String>,
        val avoidTerms: Set<String>,
        val favorableStems: Set<Char>,
        val cautiousStems: Set<Char>,
        val headlines: Map<DailyInsightTone, List<String>>,
        val neutralFocus: List<String>,
    )

    fun create(almanac: DailyAlmanacInfo, seed: String): List<DailyInsight> =
        DailyInsightCategory.entries.map { category ->
            createOne(category, almanac, seed)
        }

    fun safeSuitable(almanac: DailyAlmanacInfo): List<String> = sanitize(almanac.suitable)

    fun safeAvoid(almanac: DailyAlmanacInfo): List<String> = sanitize(almanac.avoid)

    private fun createOne(
        category: DailyInsightCategory,
        almanac: DailyAlmanacInfo,
        seed: String,
    ): DailyInsight {
        val rules = rules.getValue(category)
        val suitable = safeSuitable(almanac).firstOrNull { item ->
            rules.suitableTerms.any { term -> term in item }
        }
        val avoid = safeAvoid(almanac).firstOrNull { item ->
            rules.avoidTerms.any { term -> term in item }
        }
        val broadCaution = (almanac.suitable + almanac.avoid).any { item ->
            broadCautionTerms.any { term -> term in item }
        }
        val stem = almanac.dayGanZhi.firstOrNull()
        val signal = (if (suitable != null) 2 else 0) -
            (if (avoid != null) 2 else 0) +
            (if (stem in rules.favorableStems) 1 else 0) -
            (if (stem in rules.cautiousStems) 1 else 0) -
            (if (broadCaution && suitable == null && avoid == null) 3 else 0)
        val tone = when {
            signal >= 2 -> DailyInsightTone.Favorable
            signal <= -2 -> DailyInsightTone.Cautious
            else -> DailyInsightTone.Balanced
        }
        val headlineOptions = rules.headlines.getValue(tone)
        val headline = headlineOptions[stableIndex("headline#${category.key}#$seed", headlineOptions.size)]
        val neutral = rules.neutralFocus[stableIndex("focus#${category.key}#$seed", rules.neutralFocus.size)]
        val summary = when {
            suitable != null && avoid != null ->
                "黄历同时见宜“$suitable”与忌“$avoid”，今天宜先确认条件，再分步处理$neutral。"
            suitable != null ->
                "黄历宜“$suitable”，可适度推进$neutral，同时保留复核和调整空间。"
            avoid != null ->
                "黄历忌“$avoid”，涉及${neutral}时宜放慢确认，避免一次性作出过重承诺。"
            broadCaution ->
                "当日黄历没有适合直接转译为现代生活的明确项目，处理${neutral}时宜缩小决策范围，并以事实和实际反馈为准。"
            else ->
                "${almanac.dayGanZhi}日没有直接对应的宜忌信号，处理${neutral}时以事实和实际反馈为准。"
        }
        return DailyInsight(category, headline, summary, tone)
    }

    private fun stableIndex(value: String, size: Int): Int {
        require(size > 0)
        val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        val number = ((bytes[0].toInt() and 0xFF) shl 24) or
            ((bytes[1].toInt() and 0xFF) shl 16) or
            ((bytes[2].toInt() and 0xFF) shl 8) or
            (bytes[3].toInt() and 0xFF)
        return Math.floorMod(number, size)
    }

    private fun sanitize(items: List<String>): List<String> = items
        .asSequence()
        .map(String::trim)
        .filter(String::isNotBlank)
        .filter { item -> excludedTerms.none { term -> term in item } }
        .filter { item -> safeModernTerms.any { term -> term in item } }
        .distinct()
        .take(8)
        .toList()

    private val broadCautionTerms = setOf("诸事不宜", "馀事勿取", "余事勿取")

    private val excludedTerms = broadCautionTerms + setOf(
        "安葬", "出殡", "殡葬", "入殓", "移柩", "启钻", "破土", "修坟", "立碑",
        "谢土", "开生坟", "合寿木", "行丧", "成服", "除服", "破屋", "坏垣",
    )

    private val safeModernTerms = setOf(
        "开市", "交易", "立券", "求职", "赴任", "上任", "开工", "修造",
        "嫁娶", "纳采", "订盟", "会亲友", "出行", "祈福", "诉讼", "词讼",
        "纳财", "开仓", "置产", "求财", "借贷",
    )

    private val rules = mapOf(
        DailyInsightCategory.Career to DomainRules(
            suitableTerms = setOf("开市", "交易", "立券", "求职", "赴任", "上任", "开工", "修造"),
            avoidTerms = setOf("开市", "交易", "立券", "赴任", "上任", "开工", "修造"),
            favorableStems = setOf('甲', '乙', '丙', '丁'),
            cautiousStems = setOf('庚', '辛'),
            headlines = mapOf(
                DailyInsightTone.Favorable to listOf("可推进重点", "行动稍主动", "先做关键一步", "表达带动进展"),
                DailyInsightTone.Balanced to listOf("稳中推进", "聚焦一项重点", "先理顺再行动", "按节奏处理"),
                DailyInsightTone.Cautious to listOf("先核对再推进", "减少临时承诺", "以整理为主", "决定保留余地"),
            ),
            neutralFocus = listOf("核心任务", "团队协作", "工作边界", "计划执行", "重要沟通"),
        ),
        DailyInsightCategory.Relationship to DomainRules(
            suitableTerms = setOf("嫁娶", "纳采", "订盟", "会亲友", "出行", "祈福"),
            avoidTerms = setOf("嫁娶", "纳采", "订盟", "会亲友", "出行", "诉讼", "词讼"),
            favorableStems = setOf('乙', '丁', '己', '癸'),
            cautiousStems = setOf('庚', '壬'),
            headlines = mapOf(
                DailyInsightTone.Favorable to listOf("适合坦诚交流", "关系可向前一步", "主动表达善意", "回应重于猜测"),
                DailyInsightTone.Balanced to listOf("先听后说", "尊重彼此节奏", "以行动确认", "边界与体谅并重"),
                DailyInsightTone.Cautious to listOf("避免急下结论", "重要话慢一点说", "先处理误解", "给情绪留缓冲"),
            ),
            neutralFocus = listOf("重要关系", "沟通分寸", "共同安排", "彼此期待", "分歧处理"),
        ),
        DailyInsightCategory.Finance to DomainRules(
            suitableTerms = setOf("纳财", "开市", "交易", "立券", "开仓", "置产", "求财"),
            avoidTerms = setOf("纳财", "开市", "交易", "立券", "开仓", "置产", "求财", "借贷"),
            favorableStems = setOf('戊', '己', '庚', '辛'),
            cautiousStems = setOf('丙', '壬'),
            headlines = mapOf(
                DailyInsightTone.Favorable to listOf("可做小步安排", "账目适合梳理", "投入先看回报", "稳妥配置资源"),
                DailyInsightTone.Balanced to listOf("控制变量", "先核对再决定", "收支保持清楚", "预留调整空间"),
                DailyInsightTone.Cautious to listOf("避免冲动投入", "大额决定暂缓", "先守住预算", "谨慎新增负担"),
            ),
            neutralFocus = listOf("预算与现金流", "交易条件", "资源投入", "非必要支出", "长期安排"),
        ),
    )
}
