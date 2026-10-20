package com.zhifou.fortune

import com.nlf.calendar.Solar
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
    val monthGanZhi: String = "",
)

data class DailyFortuneSnapshot(
    val reading: FortuneReading,
    val almanac: DailyAlmanacInfo,
    val insights: List<DailyInsight>,
    val personalizationBasis: List<String>,
    /** Reserved for an explicit, optional environment source; null is the normal offline path. */
    val environmentSummary: String? = null,
    /** Internal AI context, not presentation text. */
    val rhythmBasis: String = "",
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
        val rhythm = DailyTraditionEngine.rhythm(almanac)
        val seasonal = DailyTraditionEngine.seasonalContext(almanac)
        val insights = DailyInsightEngine.create(almanac, keywords)
        val focusKeyword = keywords.firstOrNull()
        val birthdayNote = birthDate?.let { birthdayCycleNote(date, it) }.orEmpty()
        val focusCategory = focusKeyword?.let(DailyInsightEngine::keywordCategory)
        val focusInsight = insights.firstOrNull { it.category == focusCategory }
            ?: insights.minBy { when (it.tone) {
                DailyInsightTone.Cautious -> 0
                DailyInsightTone.Balanced -> 1
                DailyInsightTone.Favorable -> 2
            } }
        val advice = focusInsight.advice

        val body = buildString {
            append("${almanac.dayGanZhi}日。")
            seasonal.day?.let { phase ->
                append("《尚书·洪范》以“${phase.original}”描述${phase.label}，可借其意关注${phase.theme}。")
            }
            append(seasonal.explanation)
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
            score = rhythm.score,
            timeLabel = almanac.dateLabel,
        )
        return DailyFortuneSnapshot(
            reading = reading,
            almanac = almanac,
            insights = insights,
            personalizationBasis = basis,
            environmentSummary = normalizedEnvironment,
            rhythmBasis = rhythm.basis,
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
            monthGanZhi = lunar.monthInGanZhi,
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
}
