package com.zhifou.fortune

import java.net.URI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDataDisclosureTest {
    @Test fun coversSourcesMethodsPrivacyAndDisclaimers() {
        assertEquals("数据来源与说明", MinePage.Privacy.title)
        assertEquals(listOf("公开来源与授权", "计算与解读", "数据与隐私", "使用声明"),
            AppDataDisclosure.sections.map { it.title })
        assertTrue(AppDataDisclosure.sections.all { it.items.isNotEmpty() })
        assertTrue(AppDataDisclosure.sections.flatMap { it.items }.all {
            it.title.isNotBlank() && it.description.isNotBlank()
        })
    }

    @Test fun sourcesRetainAttributionAndDirectSecureLinks() {
        val items = AppDataDisclosure.sections.first().items
        val content = items.joinToString { it.title + it.description }
        listOf("MIT", "CC BY-SA 4.0", "Pamela Colman Smith", "Arthur Edward Waite", "Apache 2.0").forEach {
            assertTrue(content.contains(it))
        }
        val links = items.mapNotNull { it.url }
        assertTrue(links.size >= 7)
        assertEquals(links.size, links.distinct().size)
        links.forEach { url ->
            val uri = URI(url)
            assertEquals("https", uri.scheme)
            assertTrue(uri.host.isNotBlank())
            assertTrue(uri.path.isNotBlank())
            assertTrue(uri.userInfo == null && uri.query == null && uri.fragment == null)
        }
    }

    @Test fun publicExplanationDoesNotIncludeCalibrationOrInternalBreakdown() {
        val content = AppDataDisclosure.sections.flatMap { it.items }
            .joinToString { it.title + it.description }
        listOf("基准", "内部校准", "范围校准", "42", "44", "64", "85", "94").forEach {
            assertFalse(content.contains(it))
        }
        assertTrue(content.contains("不通过随机数生成每日指数"))
        assertTrue(content.contains("不是吉凶、成功或事件发生的概率"))
        assertTrue(content.contains("不代表任何必然结果"))
        assertTrue(content.contains("不上传录音"))
    }
}
