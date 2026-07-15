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
}
