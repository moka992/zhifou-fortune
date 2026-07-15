package com.zhifou.fortune

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

private val scheduleTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

internal data class ScheduleEndDefault(
    val date: LocalDate,
    val time: String,
)

internal data class ScheduleDraft(
    val title: String = "",
    val note: String = "",
    val date: LocalDate,
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val location: String = "",
    val participants: String = "",
    val highlightColor: String = "",
    val backgroundImageUri: String = "",
    val pinned: Boolean = false,
    val done: Boolean = false,
)

internal enum class ScheduleSortOrder(val label: String) {
    StartDateDescending("按时间倒序"),
    EndDateDescending("按结束时间倒序"),
    DurationAscending("按总经历天数正序"),
}

internal data class ScheduleDayDistance(
    val relation: Relation,
    val days: Long,
) {
    enum class Relation { Future, Today, Past }
}

internal fun ScheduleItem.toDraft(fallbackDate: LocalDate = LocalDate.now()): ScheduleDraft = ScheduleDraft(
    title = title,
    note = note,
    date = scheduleStartDate(this) ?: fallbackDate,
    startTime = startTime,
    endDate = endDate,
    endTime = endTime,
    location = location,
    participants = participants,
    highlightColor = highlightColor,
    backgroundImageUri = backgroundImageUri,
    pinned = pinned,
    done = done,
)

internal fun scheduleStartDate(item: ScheduleItem): LocalDate? =
    runCatching { LocalDate.parse(item.date) }.getOrNull()

internal fun scheduleEndDate(item: ScheduleItem): LocalDate? =
    item.endDate.takeIf(String::isNotBlank)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

internal fun scheduleDayDistance(item: ScheduleItem, today: LocalDate): ScheduleDayDistance {
    val start = scheduleStartDate(item) ?: today
    val delta = ChronoUnit.DAYS.between(today, start)
    return when {
        delta > 0 -> ScheduleDayDistance(ScheduleDayDistance.Relation.Future, delta)
        delta < 0 -> ScheduleDayDistance(ScheduleDayDistance.Relation.Past, -delta)
        else -> ScheduleDayDistance(ScheduleDayDistance.Relation.Today, 0)
    }
}

internal fun scheduleExperienceDays(item: ScheduleItem, today: LocalDate): Long {
    val start = scheduleStartDate(item) ?: today
    val end = scheduleEndDate(item) ?: today
    return ChronoUnit.DAYS.between(start, end).absoluteValue
}

internal fun sortScheduleItems(
    items: List<ScheduleItem>,
    order: ScheduleSortOrder,
    today: LocalDate,
): List<ScheduleItem> {
    val orderComparator = when (order) {
        ScheduleSortOrder.StartDateDescending -> compareByDescending<ScheduleItem> {
            scheduleStartDate(it)?.toEpochDay() ?: Long.MIN_VALUE
        }.thenByDescending { it.startTime }.thenByDescending { it.id }

        ScheduleSortOrder.EndDateDescending -> compareByDescending<ScheduleItem> {
            scheduleEndDate(it)?.toEpochDay() ?: Long.MIN_VALUE
        }.thenByDescending { it.endTime }.thenByDescending { it.id }

        ScheduleSortOrder.DurationAscending -> compareBy<ScheduleItem> {
            scheduleExperienceDays(it, today)
        }.thenByDescending { scheduleStartDate(it)?.toEpochDay() ?: Long.MIN_VALUE }
    }
    return items.sortedWith(
        compareByDescending<ScheduleItem> { it.pinned }.then(orderComparator)
    )
}

internal fun isScheduleEndValid(draft: ScheduleDraft): Boolean {
    val endDate = when {
        draft.endDate.isNotBlank() -> runCatching { LocalDate.parse(draft.endDate) }.getOrNull() ?: return false
        draft.endTime.isNotBlank() -> draft.date
        else -> return true
    }
    if (endDate < draft.date) return false
    if (endDate == draft.date && draft.startTime.isNotBlank() && draft.endTime.isNotBlank()) {
        return draft.endTime >= draft.startTime
    }
    return true
}

internal fun defaultScheduleStartTime(now: LocalTime = LocalTime.now()): String =
    now.truncatedTo(ChronoUnit.MINUTES).format(scheduleTimeFormatter)

internal fun defaultScheduleEnd(
    date: LocalDate,
    startTime: String,
    now: LocalTime = LocalTime.now(),
): ScheduleEndDefault {
    val baseTime = runCatching { LocalTime.parse(startTime, scheduleTimeFormatter) }
        .getOrElse { now.truncatedTo(ChronoUnit.MINUTES) }
    val end = LocalDateTime.of(date, baseTime).plusHours(1)
    return ScheduleEndDefault(
        date = end.toLocalDate(),
        time = end.toLocalTime().format(scheduleTimeFormatter),
    )
}
