package com.zhifou.fortune

import kotlin.math.roundToInt

internal fun rhythmDisplayValue(value: Int): Int = if (value == 44) 43 else value

internal enum class FivePhase(val label: String, val theme: String, val original: String) {
    Wood("木", "规划与生长", "木曰曲直"),
    Fire("火", "表达与落实", "火曰炎上"),
    Earth("土", "承接与积累", "土爰稼穑"),
    Metal("金", "取舍与整理", "金曰从革"),
    Water("水", "观察与变通", "水曰润下");

    fun generates(other: FivePhase): Boolean = entries[(ordinal + 1) % entries.size] == other
    fun controls(other: FivePhase): Boolean = entries[(ordinal + 2) % entries.size] == other

    companion object {
        fun ofStem(stem: Char?): FivePhase? = when (stem) {
            '甲', '乙' -> Wood
            '丙', '丁' -> Fire
            '戊', '己' -> Earth
            '庚', '辛' -> Metal
            '壬', '癸' -> Water
            else -> null
        }

        fun ofBranch(branch: Char?): FivePhase? = when (branch) {
            '寅', '卯' -> Wood
            '巳', '午' -> Fire
            '辰', '戌', '丑', '未' -> Earth
            '申', '酉' -> Metal
            '亥', '子' -> Water
            else -> null
        }
    }
}

internal data class DailySeasonalContext(
    val day: FivePhase?,
    val month: FivePhase?,
    val adjustment: Int,
    val explanation: String,
)

internal data class DailyRhythmAssessment(
    val score: Int,
    val almanacAdjustment: Int,
    val seasonalAdjustment: Int,
    val conflictAdjustment: Int,
    val calibrationAdjustment: Int,
) {
    val basis: String
        get() = buildString {
            append("基准 64 · 宜忌 ${signed(almanacAdjustment)} · 节令 ${signed(seasonalAdjustment)}")
            if (conflictAdjustment != 0) append(" · 复核 ${signed(conflictAdjustment)}")
            if (calibrationAdjustment != 0) append(" · 内部校准 ${signed(calibrationAdjustment)}")
        }

    private fun signed(value: Int): String = if (value > 0) "+$value" else value.toString()
}

internal object DailyTraditionEngine {
    fun seasonalContext(almanac: DailyAlmanacInfo): DailySeasonalContext {
        val day = FivePhase.ofStem(almanac.dayGanZhi.firstOrNull())
        val month = FivePhase.ofBranch(almanac.monthGanZhi.getOrNull(1))
        val (adjustment, explanation) = when {
            day == null || month == null -> 0 to "节令依据不足，保持中性参考。"
            month == day -> 2 to "节月与日干同属${day.label}，可保持连贯，也给自己留出停顿。"
            month.generates(day) -> 3 to "节月${month.label}生日干${day.label}，借相生之意，先借助已有条件，再小步推进。"
            day.generates(month) -> -1 to "日干${day.label}生节月${month.label}，借付出之意，安排任务时也留一点余量。"
            month.controls(day) -> -2 to "节月${month.label}与日干${day.label}有相制关系，借制衡之意，先校准条件，不必急于扩大。"
            else -> -1 to "日干${day.label}与节月${month.label}有相制关系，借制衡之意，把范围收拢比同时用力更从容。"
        }
        return DailySeasonalContext(day, month, adjustment, explanation)
    }

    fun rhythm(almanac: DailyAlmanacInfo): DailyRhythmAssessment {
        val signals = DailyInsightEngine.signals(almanac)
        val almanacAdjustment = signals.sumOf { it.balance } * 3
        val seasonalAdjustment = seasonalContext(almanac).adjustment
        val conflictAdjustment = -signals.count { it.conflicting }.coerceAtMost(3)
        // These are bounded editorial weights, not a classical formula or measured probability.
        val evidence = almanacAdjustment + seasonalAdjustment + conflictAdjustment
        val scale = if (evidence >= 0) 30.0 / 21.0 else 22.0 / 23.0
        val rawScore = 64 + evidence
        val score = rhythmDisplayValue((64 + evidence * scale).roundToInt().coerceIn(42, 94))
        return DailyRhythmAssessment(score, almanacAdjustment, seasonalAdjustment, conflictAdjustment, score - rawScore)
    }
}
