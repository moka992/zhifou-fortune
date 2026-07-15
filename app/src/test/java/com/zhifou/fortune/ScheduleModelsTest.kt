package com.zhifou.fortune

import java.time.LocalDate
import java.time.LocalTime
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleModelsTest {
    @Test
    fun legacyJsonLoadsWithAdvancedFieldsEmpty() {
        val item = JSONObject(
            """{"id":7,"title":"旧日程","note":"备注","date":"2026-07-14","createdAt":"2026-07-13 09:00","done":true}"""
        ).toScheduleItem()

        assertEquals("旧日程", item.title)
        assertTrue(item.done)
        assertEquals("", item.startTime)
        assertEquals("", item.endDate)
        assertEquals("", item.backgroundImageUri)
        assertFalse(item.pinned)
    }

    @Test
    fun advancedFieldsRoundTripThroughJson() {
        val original = sampleItem(
            id = 9,
            date = "2026-07-14",
            endDate = "2026-07-16",
            pinned = true,
        ).copy(
            startTime = "09:30",
            endTime = "18:20",
            location = "会议室",
            participants = "甲、乙",
            highlightColor = "#49A078",
            backgroundImageUri = "content://local/image/9",
        )

        assertEquals(original, original.toJson().toScheduleItem())
    }

    @Test
    fun distanceUsesWholeCalendarDays() {
        val today = LocalDate.of(2026, 7, 14)

        assertEquals(
            ScheduleDayDistance(ScheduleDayDistance.Relation.Future, 4),
            scheduleDayDistance(sampleItem(1, "2026-07-18"), today),
        )
        assertEquals(
            ScheduleDayDistance(ScheduleDayDistance.Relation.Today, 0),
            scheduleDayDistance(sampleItem(2, "2026-07-14"), today),
        )
        assertEquals(
            ScheduleDayDistance(ScheduleDayDistance.Relation.Past, 3),
            scheduleDayDistance(sampleItem(3, "2026-07-11"), today),
        )
    }

    @Test
    fun sortingKeepsPinnedFirstAndAppliesSelectedOrder() {
        val today = LocalDate.of(2026, 7, 14)
        val items = listOf(
            sampleItem(1, "2026-07-10", endDate = "2026-07-30"),
            sampleItem(2, "2026-07-20", endDate = "2026-07-21"),
            sampleItem(3, "2026-07-01", endDate = "2026-07-02", pinned = true),
        )

        assertEquals(
            listOf(3L, 2L, 1L),
            sortScheduleItems(items, ScheduleSortOrder.StartDateDescending, today).map { it.id },
        )
        assertEquals(
            listOf(3L, 1L, 2L),
            sortScheduleItems(items, ScheduleSortOrder.EndDateDescending, today).map { it.id },
        )
        assertEquals(
            listOf(3L, 2L, 1L),
            sortScheduleItems(items, ScheduleSortOrder.DurationAscending, today).map { it.id },
        )
    }

    @Test
    fun endTimeValidationHandlesImplicitSameDay() {
        val date = LocalDate.of(2026, 7, 14)

        assertFalse(
            isScheduleEndValid(
                ScheduleDraft(date = date, startTime = "15:00", endTime = "14:00")
            )
        )
        assertTrue(
            isScheduleEndValid(
                ScheduleDraft(date = date, startTime = "15:00", endTime = "16:00")
            )
        )
        assertFalse(
            isScheduleEndValid(
                ScheduleDraft(date = date, endDate = "2026-07-13")
            )
        )
    }

    @Test
    fun scheduleTimeDefaultsUsePhoneTimeAndOneHourDuration() {
        val date = LocalDate.of(2026, 7, 15)

        assertEquals("09:17", defaultScheduleStartTime(LocalTime.of(9, 17, 48)))
        assertEquals(
            ScheduleEndDefault(date, "10:17"),
            defaultScheduleEnd(date, startTime = "", now = LocalTime.of(9, 17, 48)),
        )
        assertEquals(
            ScheduleEndDefault(date, "16:30"),
            defaultScheduleEnd(date, startTime = "15:30", now = LocalTime.NOON),
        )
        assertEquals(
            ScheduleEndDefault(date.plusDays(1), "00:30"),
            defaultScheduleEnd(date, startTime = "23:30", now = LocalTime.NOON),
        )
    }

    private fun sampleItem(
        id: Long,
        date: String,
        endDate: String = "",
        pinned: Boolean = false,
    ) = ScheduleItem(
        id = id,
        title = "事项$id",
        note = "",
        date = date,
        createdAt = "2026-07-14 10:00",
        endDate = endDate,
        pinned = pinned,
    )
}
