package com.zhifou.fortune

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiInterpretationTest {
    @Test
    fun continuesWhenProviderReportsAnOutputLimit() {
        assertTrue(AiInterpreter.needsContinuation("length", "尚未写完。"))
        assertTrue(AiInterpreter.needsContinuation("max_tokens", "尚未写完。"))
    }

    @Test
    fun completionMarkerCoversProvidersWithoutFinishReason() {
        assertTrue(AiInterpreter.needsContinuation("", "内容在这里中断"))
        assertFalse(AiInterpreter.needsContinuation("", "内容完整。\n【解读完成】"))
        assertFalse(AiInterpreter.needsContinuation("stop", "兼容未返回完成标记的接口。"))
    }

    @Test
    fun completionMarkerIsNotShownToUsers() {
        val cleaned = AiInterpreter.stripCompletionMarker("完整内容。\n【解读完成】")

        assertTrue(cleaned == "完整内容。")
    }

    @Test
    fun answerBookPromptAlsoRequiresACompleteBoundedResponse() {
        val prompt = AiInterpreter.buildAiPrompt(FortuneOracle().answerBook("我现在适合开始吗？"))

        assertTrue(prompt.contains("全文控制在400至700个汉字"))
        assertTrue(prompt.contains("【解读完成】"))
    }

    @Test
    fun disablesDefaultThinkingForDeepSeekModelsAndOfficialEndpoint() {
        assertTrue(
            shouldDisableDeepSeekThinking(
                "https://api.deepseek.com/chat/completions",
                "DeepSeek-V4-Flash-0731",
            )
        )
        assertTrue(
            shouldDisableDeepSeekThinking(
                "https://api.deepseek.com/chat/completions",
                "custom-model-alias",
            )
        )
        assertFalse(
            shouldDisableDeepSeekThinking(
                "https://api.openai.com/v1/chat/completions",
                "gpt-4o-mini",
            )
        )
    }

    @Test
    fun aiConfigurationFingerprintChangesOnlyWithConfiguration() {
        val first = aiConfigurationFingerprint("https://api.example.com/v1/chat/completions", "model-a", "secret-a")
        val same = aiConfigurationFingerprint("https://api.example.com/v1/chat/completions", "model-a", "secret-a")
        val changed = aiConfigurationFingerprint("https://api.example.com/v1/chat/completions", "model-b", "secret-a")

        assertTrue(first.isNotBlank())
        assertTrue(first == same)
        assertFalse(first == changed)
        assertFalse(isAiConfigurationComplete("not-a-url", "secret", "model"))
    }

    @Test
    fun dailyInsightPromptStaysWithinTheTokenBudgetDesign() {
        val snapshot = DailyFortuneEngine.create(
            java.time.LocalDate.of(2026, 8, 1),
            "测试用户",
            "1995-04-18",
            "工作、关系、储蓄",
        )
        val prompt = buildDailyAiInsightPrompt(
            snapshot,
            DailyInsightCategory.Career,
            "测试用户",
            "1995-04-18",
            "工作、关系、储蓄",
        )

        assertTrue(prompt.length < 2_500)
        assertTrue(DAILY_INSIGHT_MAX_OUTPUT_TOKENS <= 700)
        assertTrue(prompt.contains("本次只解读“事业”"))
        assertTrue(prompt.contains("220至320个汉字"))
    }

    @Test
    fun dailyInsightPromptIsIsolatedFromCoinDivinationPrompt() {
        val lines = (1..6).map { position ->
            CoinLineResult(position, listOf(3, 2, 2))
        }
        val coinPrompt = AiInterpreter.buildAiPrompt(FortuneOracle().coin("测试问题", lines))
        val snapshot = DailyFortuneEngine.create(
            java.time.LocalDate.of(2026, 8, 1),
            "",
            "",
            "",
        )
        val dailyPrompt = buildDailyAiInsightPrompt(
            snapshot,
            DailyInsightCategory.Career,
            "",
            "",
            "",
        )

        assertTrue(coinPrompt.contains("六次投掷"))
        assertTrue(coinPrompt.contains("700至1100个汉字"))
        assertFalse(dailyPrompt.contains("六次投掷"))
        assertFalse(dailyPrompt.contains("700至1100个汉字"))
    }

    @Test
    fun dailyInsightPromptNeverReceivesSensitiveAlmanacTerms() {
        val original = DailyFortuneEngine.create(
            java.time.LocalDate.of(2026, 8, 1),
            "",
            "",
            "",
        )
        val filteredAlmanac = original.almanac.copy(
            suitable = listOf("交易", "安葬", "出殡", "诸事不宜"),
            avoid = listOf("立券", "破屋", "入殓", "馀事勿取"),
        )
        val snapshot = original.copy(
            almanac = filteredAlmanac,
            insights = DailyInsightEngine.create(filteredAlmanac, "filtered"),
        )
        val prompt = buildDailyAiInsightPrompt(
            snapshot,
            DailyInsightCategory.Finance,
            "",
            "",
            "",
        )

        assertTrue(prompt.contains("交易"))
        assertTrue(prompt.contains("立券"))
        listOf("安葬", "出殡", "诸事不宜", "破屋", "入殓", "馀事勿取").forEach {
            assertFalse(prompt.contains(it))
        }
    }
}
