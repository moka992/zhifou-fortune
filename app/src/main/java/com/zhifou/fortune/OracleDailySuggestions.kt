package com.zhifou.fortune

import java.time.LocalDate

internal fun dailyOracleSuggestions(snapshot: DailyFortuneSnapshot): List<String> {
    val almanac = snapshot.almanac
    val seed = (almanac.date.toEpochDay() xor almanac.dayGanZhi.hashCode().toLong()).toInt()
    val fortuneQuestion = when (snapshot.reading.score) {
        in 74..100 -> "今天整体势头较好，最值得主动把握什么？"
        in 66..73 -> "今天最应该优先推进哪件事？"
        in 58..65 -> "今天怎样安排节奏会更稳妥？"
        else -> "今天更适合守成、调整还是行动？"
    }
    val almanacQuestion = when (Math.floorMod(seed, 4)) {
        0 -> "今日${almanac.dayGanZhi}，做决定时应把握什么节奏？"
        1 -> "今日财神在${almanac.wealthDirection}，财务安排上应关注什么？"
        2 -> "今日冲${almanac.clash}、煞${almanac.shaDirection}，沟通上应留意什么？"
        else -> if (almanac.solarTerm.isNotBlank()) {
            "${almanac.solarTerm}当日，我近期应调整什么节奏？"
        } else {
            "今天面对选择时，我应更看重什么？"
        }
    }

    return buildList {
        add(fortuneQuestion)
        add(safeSuitableOracleQuestion(almanac.suitable, seed))
        add(safeAvoidOracleQuestion(almanac.avoid, seed / 7))
        add(almanacQuestion)
    }.distinct().fillToFour(almanac.date)
}

internal fun shouldShowDailyOraclePrompt(lastShownDate: String, today: LocalDate): Boolean =
    lastShownDate != today.toString()

internal fun isUpwardOraclePageSwipe(totalDragY: Float, thresholdPx: Float): Boolean =
    totalDragY <= -thresholdPx

internal fun isDownwardOraclePageSwipe(totalDragY: Float, thresholdPx: Float): Boolean =
    totalDragY >= thresholdPx

// 黄历原始事项包含丧葬、疾病、祭祀等不适合作为消费级推荐文案的词。
// 推荐只允许使用审校后的日常语义白名单，其他事项一律回退，不直接拼接原文。
private val safeSuitableActivityQuestions = mapOf(
    "破屋" to "今天适合整理哪些积压已久的问题？",
    "坏垣" to "今天适合整理哪些积压已久的问题？",
    "解除" to "今天适合放下哪项不再必要的负担？",
    "扫舍" to "今天整理环境时，最值得先处理哪里？",
    "沐浴" to "今天怎样调整状态会更舒适从容？",
    "馀事勿取" to "今天最值得专注的一件事是什么？",
    "余事勿取" to "今天最值得专注的一件事是什么？",
    "出行" to "今天出行或外出办事应注意什么？",
    "会亲友" to "今天与亲友沟通时，怎样表达更合适？",
    "交易" to "今天处理交易或财务决定应把握什么？",
    "立券" to "今天处理交易或财务决定应把握什么？",
    "开市" to "今天推进工作或经营计划时应关注什么？",
    "纳财" to "今天安排收支时，怎样做会更稳妥？",
    "入学" to "今天学习新内容时，最适合从哪里开始？",
    "求学" to "今天学习新内容时，最适合从哪里开始？",
)

private val safeAvoidActivityQuestions = mapOf(
    "诸事不宜" to "今天有哪些决定适合暂缓，多观察一步？",
    "馀事勿取" to "今天有哪些次要事项可以暂时放下？",
    "余事勿取" to "今天有哪些次要事项可以暂时放下？",
    "出行" to "如果今天必须外出，我应如何降低风险？",
    "会亲友" to "今天沟通时，哪些表达方式需要避免？",
    "交易" to "今天在合约和财务上应避免什么？",
    "立券" to "今天在合约和财务上应避免什么？",
    "开市" to "今天推进工作或经营计划时应避免什么？",
    "纳财" to "今天安排收支时，最需要避免什么？",
    "入学" to "今天学习时，怎样避免无效投入？",
    "求学" to "今天学习时，怎样避免无效投入？",
)

private val suitableQuestionFallbacks = listOf(
    "今天哪件事最值得优先完成？",
    "今天怎样安排精力会更从容？",
    "今天适合先解决哪个现实问题？",
    "今天做事时，怎样把握轻重缓急？",
)

private val avoidQuestionFallbacks = listOf(
    "今天最需要避免的判断误区是什么？",
    "今天有哪些决定适合多观察一步？",
    "今天沟通和行动时，最需要留意什么？",
    "今天怎样避免把精力消耗在次要事情上？",
)

internal fun safeSuitableOracleQuestion(activities: List<String>, seed: Int): String =
    activities
        .mapNotNull(safeSuitableActivityQuestions::get)
        .distinct()
        .pick(seed)
        ?: suitableQuestionFallbacks[Math.floorMod(seed, suitableQuestionFallbacks.size)]

internal fun safeAvoidOracleQuestion(activities: List<String>, seed: Int): String =
    activities
        .mapNotNull(safeAvoidActivityQuestions::get)
        .distinct()
        .pick(seed)
        ?: avoidQuestionFallbacks[Math.floorMod(seed, avoidQuestionFallbacks.size)]

private fun List<String>.pick(seed: Int): String? =
    takeIf { isNotEmpty() }?.get(Math.floorMod(seed, size))

private fun List<String>.fillToFour(date: LocalDate): List<String> {
    if (size >= 4) return take(4)
    val fallbacks = listOf(
        "今天工作上最值得关注的机会是什么？",
        "今天处理人际关系时应注意什么？",
        "今天我应坚持还是调整方向？",
        "今天怎样安排精力会更顺利？",
    )
    val offset = Math.floorMod(date.dayOfYear, fallbacks.size)
    return (this + List(fallbacks.size) { fallbacks[(offset + it) % fallbacks.size] })
        .distinct()
        .take(4)
}
