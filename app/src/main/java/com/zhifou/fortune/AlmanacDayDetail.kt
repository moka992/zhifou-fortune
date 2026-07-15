package com.zhifou.fortune

import com.nlf.calendar.LunarYear
import com.nlf.calendar.Solar
import com.nlf.calendar.util.LunarUtil
import java.time.LocalDate

internal data class AlmanacPillar(
    val label: String,
    val ganZhi: String,
    val zodiac: String,
    val naYin: String,
)

internal data class AlmanacDayDetail(
    val date: LocalDate,
    val weekday: String,
    val constellation: String,
    val lunarYear: String,
    val lunarMonth: String,
    val lunarDay: String,
    val pillars: List<AlmanacPillar>,
    val dayStemElement: String,
    val dayOfficer: String,
    val dayGod: String,
    val dayGodType: String,
    val dayGodLuck: String,
    val suitable: List<String>,
    val avoid: List<String>,
    val auspiciousGods: List<String>,
    val inauspiciousGods: List<String>,
    val pengZuGan: String,
    val pengZuZhi: String,
    val clash: String,
    val shaDirection: String,
    val monthFetalGod: String,
    val dayFetalGod: String,
    val solarTerm: String,
    val previousSolarTerm: String,
    val nextSolarTerm: String,
    val seasonalPhase: String,
    val phenology: String,
    val moonPhase: String,
    val liuYao: String,
    val dayLu: String,
    val joyDirection: String,
    val fortuneDirection: String,
    val wealthDirection: String,
    val yangNobleDirection: String,
    val yinNobleDirection: String,
    val yearVoid: String,
    val monthVoid: String,
    val dayVoid: String,
    val nineStar: String,
    val nineStarDetail: String,
    val annualFolkCustoms: List<String>,
    val traditionalFestivals: List<String>,
)

internal object AlmanacDayDetailEngine {
    fun create(date: LocalDate): AlmanacDayDetail {
        val solar = Solar.fromYmd(date.year, date.monthValue, date.dayOfMonth)
        val lunar = solar.lunar
        val lunarYear = LunarYear.fromYear(lunar.year)
        val nineStar = lunar.dayNineStar
        val solarTerm = lunar.jieQi.orEmpty()
        val traditionalFestivals = (
            lunar.festivals +
                lunar.otherFestivals +
                if (solarTerm == "清明") listOf("清明节") else emptyList()
            ).filter { it in TRADITIONAL_FESTIVALS }.distinct()

        return AlmanacDayDetail(
            date = date,
            weekday = WEEKDAYS[date.dayOfWeek.value - 1],
            constellation = solar.xingZuo,
            lunarYear = lunar.yearInChinese + "年",
            lunarMonth = (if (lunar.month < 0) "闰" else "") + lunar.monthInChinese + "月",
            lunarDay = lunar.dayInChinese,
            pillars = listOf(
                AlmanacPillar("年", lunar.yearInGanZhi, lunar.yearShengXiao, lunar.yearNaYin),
                AlmanacPillar("月", lunar.monthInGanZhi, lunar.monthShengXiao, lunar.monthNaYin),
                AlmanacPillar("日", lunar.dayInGanZhi, lunar.dayShengXiao, lunar.dayNaYin),
            ),
            dayStemElement = LunarUtil.WU_XING_GAN[lunar.dayGan].orEmpty(),
            dayOfficer = lunar.zhiXing,
            dayGod = lunar.dayTianShen,
            dayGodType = lunar.dayTianShenType,
            dayGodLuck = lunar.dayTianShenLuck,
            suitable = lunar.dayYi.cleaned(),
            avoid = lunar.dayJi.cleaned(),
            auspiciousGods = lunar.dayJiShen.cleaned(),
            inauspiciousGods = lunar.dayXiongSha.cleaned(),
            pengZuGan = lunar.pengZuGan,
            pengZuZhi = lunar.pengZuZhi,
            clash = "${lunar.dayShengXiao}日冲${lunar.dayChongDesc}",
            shaDirection = lunar.daySha,
            monthFetalGod = lunar.monthPositionTai,
            dayFetalGod = lunar.dayPositionTai,
            solarTerm = solarTerm,
            previousSolarTerm = lunar.prevJieQi.toDisplayLabel(),
            nextSolarTerm = lunar.nextJieQi.toDisplayLabel(),
            seasonalPhase = lunar.hou,
            phenology = lunar.wuHou,
            moonPhase = lunar.yueXiang,
            liuYao = lunar.liuYao,
            dayLu = lunar.dayLu,
            joyDirection = lunar.dayPositionXiDesc,
            fortuneDirection = lunar.dayPositionFuDesc,
            wealthDirection = lunar.dayPositionCaiDesc,
            yangNobleDirection = lunar.dayPositionYangGuiDesc,
            yinNobleDirection = lunar.dayPositionYinGuiDesc,
            yearVoid = lunar.yearXunKong,
            monthVoid = lunar.monthXunKong,
            dayVoid = lunar.dayXunKong,
            nineStar = "${nineStar.number}${nineStar.color}${nineStar.wuXing}",
            nineStarDetail = "${nineStar.nameInTaiYi}星（${nineStar.typeInTaiYi}）· ${nineStar.positionDesc}",
            annualFolkCustoms = listOf(
                lunarYear.gengTian,
                lunarYear.fenBing,
                lunarYear.zhiShui,
                lunarYear.deJin,
            ).cleaned(),
            traditionalFestivals = traditionalFestivals,
        )
    }

    private fun com.nlf.calendar.JieQi.toDisplayLabel(): String = "$name · ${solar.toYmd()}"

    private fun List<String>.cleaned(): List<String> = filter(String::isNotBlank).distinct()

    private val WEEKDAYS = listOf("一", "二", "三", "四", "五", "六", "日")

    private val TRADITIONAL_FESTIVALS = setOf(
        "春节",
        "元宵节",
        "龙抬头",
        "龙头节",
        "上巳节",
        "寒食节",
        "清明节",
        "端午节",
        "七夕节",
        "中元节",
        "中秋节",
        "重阳节",
        "寒衣节",
        "下元节",
        "腊八节",
        "小年",
        "除夕",
    )
}
