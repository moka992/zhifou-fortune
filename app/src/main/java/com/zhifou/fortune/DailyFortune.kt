package com.zhifou.fortune

import com.nlf.calendar.Solar
import java.security.MessageDigest
import java.time.DateTimeException
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class DailyAlmanacInfo(
    val date: LocalDate,
    val dateLabel: String,
    val lunarLabel: String,
    val dayGanZhi: String,
    val solarTerm: String,
    val joyDirection: String,
    val fortuneDirection: String,
    val wealthDirection: String,
    val yangNobleDirection: String,
    val yinNobleDirection: String,
    val suitable: List<String>,
    val avoid: List<String>,
    val clash: String,
    val shaDirection: String,
)

data class DailyFortuneSnapshot(
    val reading: FortuneReading,
    val almanac: DailyAlmanacInfo,
    val insights: List<DailyInsight>,
    val personalizationBasis: List<String>,
    /** Reserved for an explicit, optional environment source; null is the normal offline path. */
    val environmentSummary: String? = null,
)

internal object DailyFortuneEngine {
    fun create(
        date: LocalDate,
        nickname: String,
        birthDateText: String,
        keywordsText: String,
        environmentSummary: String? = null,
        almanacOverride: DailyAlmanacInfo? = null,
    ): DailyFortuneSnapshot {
        val almanac = almanacOverride?.takeIf { it.date == date } ?: almanac(date)
        val birthDate = parseBirthDate(birthDateText, date)
        val keywords = parseKeywords(keywordsText)
        val normalizedEnvironment = environmentSummary?.trim()?.takeIf { it.isNotBlank() }
        val seed = listOf(
            date.toString(),
            nickname.trim(),
            birthDate?.toString().orEmpty(),
            keywords.joinToString("|"),
            normalizedEnvironment.orEmpty(),
        ).joinToString("#")

        val score = 52 + stableIndex("score#$seed", 29)
        val dayTheme = dayTheme(almanac.dayGanZhi.firstOrNull())
        val themeDetail = themeDetails.getValue(dayTheme)
        val focusKeyword = keywords.takeIf { it.isNotEmpty() }
            ?.let { it[stableIndex("keyword#$seed", it.size)] }
        val birthdayNote = birthDate?.let { birthdayCycleNote(date, it) }.orEmpty()
        val dailyDetail = themeDetail[stableIndex("detail#$seed", themeDetail.size)]
        val advice = dailyAdvice[stableIndex("advice#$seed", dailyAdvice.size)]

        val body = buildString {
            append("${almanac.dayGanZhi}日的节奏偏向$dayTheme。$dailyDetail")
            if (focusKeyword != null) {
                append(" 结合你设置的关注词“$focusKeyword”，今天更适合把它收敛成一项当天可以验证的小目标。")
            }
            if (birthdayNote.isNotBlank()) {
                append(" $birthdayNote")
            }
            if (normalizedEnvironment != null) {
                append(" 环境参考：$normalizedEnvironment。")
            }
        }

        val basis = buildList {
            add("日期与离线黄历")
            if (nickname.isNotBlank()) add("昵称")
            if (birthDate != null) add("生日")
            if (keywords.isNotEmpty()) add("关键词")
            if (normalizedEnvironment != null) add("可选环境信息")
        }
        val reading = FortuneReading(
            id = date.toEpochDay(),
            kind = "运势",
            title = if (nickname.isBlank()) "今日运势" else "${nickname.trim()}的今日运势",
            question = "",
            body = body,
            advice = "今日建议：$advice",
            score = score,
            timeLabel = almanac.dateLabel,
        )
        return DailyFortuneSnapshot(
            reading = reading,
            almanac = almanac,
            insights = DailyInsightEngine.create(almanac, seed),
            personalizationBasis = basis,
            environmentSummary = normalizedEnvironment,
        )
    }

    fun almanac(date: LocalDate): DailyAlmanacInfo {
        val lunar = Solar.fromYmd(date.year, date.monthValue, date.dayOfMonth).lunar
        val lunarMonth = (if (lunar.month < 0) "闰" else "") + lunar.monthInChinese + "月"
        val weekday = listOf("一", "二", "三", "四", "五", "六", "日")[date.dayOfWeek.value - 1]
        return DailyAlmanacInfo(
            date = date,
            dateLabel = "${date.year}年${date.monthValue}月${date.dayOfMonth}日 周$weekday",
            lunarLabel = "${lunar.yearInGanZhi}年 $lunarMonth${lunar.dayInChinese}",
            dayGanZhi = lunar.dayInGanZhi,
            solarTerm = lunar.jieQi.orEmpty(),
            joyDirection = lunar.dayPositionXiDesc,
            fortuneDirection = lunar.dayPositionFuDesc,
            wealthDirection = lunar.dayPositionCaiDesc,
            yangNobleDirection = lunar.dayPositionYangGuiDesc,
            yinNobleDirection = lunar.dayPositionYinGuiDesc,
            suitable = lunar.dayYi.filter(String::isNotBlank).distinct(),
            avoid = lunar.dayJi.filter(String::isNotBlank).distinct(),
            clash = lunar.dayChongDesc,
            shaDirection = lunar.daySha,
        )
    }

    internal fun parseBirthDate(value: String, today: LocalDate): LocalDate? {
        val normalized = value.trim()
            .replace('年', '-')
            .replace('月', '-')
            .replace("日", "")
            .replace('.', '-')
            .replace('/', '-')
        val match = Regex("^(\\d{4})-(\\d{1,2})-(\\d{1,2})$").matchEntire(normalized) ?: return null
        return runCatching {
            LocalDate.of(
                match.groupValues[1].toInt(),
                match.groupValues[2].toInt(),
                match.groupValues[3].toInt(),
            )
        }.getOrNull()?.takeIf { !it.isAfter(today) }
    }

    private fun parseKeywords(value: String): List<String> = value
        .split(Regex("[，,、;；\\s]+"))
        .map(String::trim)
        .filter(String::isNotBlank)
        .distinct()
        .take(8)

    private fun birthdayCycleNote(today: LocalDate, birthDate: LocalDate): String {
        val birthdayThisYear = safeBirthday(today.year, birthDate)
        val previous = if (birthdayThisYear.isAfter(today)) safeBirthday(today.year - 1, birthDate) else birthdayThisYear
        val next = if (birthdayThisYear.isBefore(today)) safeBirthday(today.year + 1, birthDate) else birthdayThisYear
        val daysSince = ChronoUnit.DAYS.between(previous, today).toInt()
        val daysUntil = ChronoUnit.DAYS.between(today, next).toInt()
        return when {
            daysUntil == 0 -> "今天也是你的生日节点，适合回看过去一年并只确定一个新阶段重点。"
            daysUntil in 1..14 -> "临近你的年度生日节点，适合先整理旧事项，再决定下一阶段的投入。"
            daysSince in 1..14 -> "刚经过你的年度生日节点，适合把新计划拆成可持续的小步骤。"
            else -> ""
        }
    }

    private fun safeBirthday(year: Int, birthDate: LocalDate): LocalDate = try {
        LocalDate.of(year, birthDate.monthValue, birthDate.dayOfMonth)
    } catch (_: DateTimeException) {
        LocalDate.of(year, 2, 28)
    }

    private fun dayTheme(dayGan: Char?): String = when (dayGan) {
        '甲', '乙' -> "生发与规划"
        '丙', '丁' -> "表达与行动"
        '戊', '己' -> "整理与承接"
        '庚', '辛' -> "取舍与定界"
        '壬', '癸' -> "观察与调整"
        else -> "稳步推进"
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

    private val themeDetails = mapOf(
        "生发与规划" to listOf(
            "适合启动小范围尝试，但应先明确投入上限。",
            "新想法较容易出现，先记录和验证，比立即扩大更稳妥。",
            "今天可向前推进一步，同时保留根据反馈调整的空间。",
        ),
        "表达与行动" to listOf(
            "沟通与执行的作用更明显，重要表达宜具体而不过度承诺。",
            "适合把想法变成一次真实行动，并观察他人的实际反馈。",
            "行动效率可能高于反复权衡，但仍应给关键决定留出复核时间。",
        ),
        "整理与承接" to listOf(
            "适合补齐旧事项和稳定已有安排，不必急于同时开启太多新任务。",
            "今天的价值更多来自持续和收尾，先处理会影响后续的基础环节。",
            "把资源、时间和承诺重新排一次顺序，可能比增加投入更有效。",
        ),
        "取舍与定界" to listOf(
            "适合明确优先级和边界，但判断前仍要核对事实与代价。",
            "减少模糊承诺会让今天更轻松，重要选择宜保留退出条件。",
            "可以处理需要决断的事项，同时避免因追求干脆而忽略关系成本。",
        ),
        "观察与调整" to listOf(
            "信息变化可能比行动本身更重要，先观察再修正会更稳妥。",
            "适合处理需要理解、复盘或转向的事情，不必强求一次定论。",
            "今天可多留意情绪和环境反馈，把不确定性拆成几个可验证问题。",
        ),
        "稳步推进" to listOf("保持常规节奏，先完成最关键的一件事。"),
    )

    private val dailyAdvice = listOf(
        "把最重要的目标压缩成一件今天能够完成的事。",
        "重要回复稍作停顿，确认事实后再表达立场。",
        "先完成低风险验证，再决定是否增加投入。",
        "为重要选择保留一个可以撤回或调整的方案。",
        "把模糊担心写成三个具体问题，逐一确认。",
        "减少一个非必要承诺，为真正重要的事情留出空间。",
    )
}
