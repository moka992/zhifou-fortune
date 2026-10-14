package com.zhifou.fortune

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth

private val scheduleImageCache = android.util.LruCache<String, ImageBitmap>(12)

private val scheduleHighlightColors = listOf(
    "",
    "#D6AA43",
    "#49A078",
    "#3F88C5",
    "#C855A5",
    "#D76A4A",
    "#7A6FC2",
)

@Composable
internal fun ScheduleMemorialEntryCard(itemCount: Int, onClick: () -> Unit) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .widthIn(max = 920.dp)
            .fillMaxWidth(),
    ) {
        GeminiGlassPanel(
            tone = GeminiGlassTone.Neutral,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
        ) {
        Row(
            modifier = Modifier.padding(
                horizontal = layout.cardPadding,
                vertical = if (layout.compactHeight) 11.dp else 15.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (layout.compactWidth) 10.dp else 14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(if (layout.compactWidth) 38.dp else 42.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("日程纪念", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${itemCount}项", color = C.textSub, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = C.textSub)
        }
        }
    }
}

@Composable
internal fun ScheduleEditorScreen(
    initialDate: LocalDate,
    existingItem: ScheduleItem?,
    allItems: List<ScheduleItem>,
    onCancel: () -> Unit,
    onSave: (ScheduleDraft) -> Unit,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val context = LocalContext.current
    val initial = remember(existingItem?.id, initialDate) {
        existingItem?.toDraft(initialDate) ?: ScheduleDraft(date = initialDate)
    }
    var title by rememberSaveable(existingItem?.id, initialDate.toString()) { mutableStateOf(initial.title) }
    var note by rememberSaveable(existingItem?.id, initialDate.toString()) { mutableStateOf(initial.note) }
    var dateText by rememberSaveable(existingItem?.id, initialDate.toString()) { mutableStateOf(initial.date.toString()) }
    var startTime by rememberSaveable(existingItem?.id) { mutableStateOf(initial.startTime) }
    var endDate by rememberSaveable(existingItem?.id) { mutableStateOf(initial.endDate) }
    var endTime by rememberSaveable(existingItem?.id) { mutableStateOf(initial.endTime) }
    var location by rememberSaveable(existingItem?.id) { mutableStateOf(initial.location) }
    var participants by rememberSaveable(existingItem?.id) { mutableStateOf(initial.participants) }
    var highlightColor by rememberSaveable(existingItem?.id) { mutableStateOf(initial.highlightColor) }
    var backgroundImageUri by rememberSaveable(existingItem?.id) { mutableStateOf(initial.backgroundImageUri) }
    var pinned by rememberSaveable(existingItem?.id) { mutableStateOf(initial.pinned) }
    var done by rememberSaveable(existingItem?.id) { mutableStateOf(initial.done) }
    var advancedExpanded by rememberSaveable(existingItem?.id) {
        mutableStateOf(existingItem?.hasAdvancedScheduleContent() == true)
    }
    var picker by remember { mutableStateOf<ScheduleEditorPicker?>(null) }
    val selectedDate = runCatching { LocalDate.parse(dateText) }.getOrDefault(initialDate)
    val dayItems = remember(allItems, selectedDate) {
        allItems
            .filter { it.date == selectedDate.toString() }
            .sortedWith(compareByDescending<ScheduleItem> { it.pinned }.thenBy { it.startTime })
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            backgroundImageUri = uri.toString()
        }
    }

    fun submit() {
        val draft = ScheduleDraft(
            title = title,
            note = note,
            date = selectedDate,
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
        when {
            title.isBlank() -> Toast.makeText(context, "请填写事项名称", Toast.LENGTH_SHORT).show()
            !isScheduleEndValid(draft) -> Toast.makeText(context, "结束时间不能早于开始时间", Toast.LENGTH_SHORT).show()
            else -> onSave(draft)
        }
    }

    Column(Modifier.fillMaxSize()) {
        SubScreenHeader(title = if (existingItem == null) "新增日程" else "修改日程", onBack = onCancel) {
            TextButton(onClick = onCancel) {
                Text("取消", color = C.textSub, style = MaterialTheme.typography.labelMedium)
            }
            TextButton(onClick = ::submit) {
                Text("完成", color = C.gold, fontWeight = FontWeight.SemiBold)
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = layout.horizontalPadding,
                vertical = 10.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item(key = "title") {
                ScheduleGlassTextField(
                    value = title,
                    onValueChange = { title = it.take(120) },
                    label = "事项",
                    placeholder = "例如：项目截止日",
                    singleLine = true,
                )
            }
            item(key = "note") {
                ScheduleGlassTextField(
                    value = note,
                    onValueChange = { note = it.take(2_000) },
                    label = "备注",
                    minLines = 2,
                )
            }
            item(key = "date") {
                SchedulePickerField(
                    label = "日期",
                    value = formatScheduleFullDate(selectedDate),
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = C.gold) },
                    onClick = { picker = ScheduleEditorPicker.StartDate },
                )
            }
            item(key = "advanced") {
                ScheduleGlassControl(
                    onClick = { advancedExpanded = !advancedExpanded },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("高级选项", modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                    Icon(
                        if (advancedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (advancedExpanded) "收起" else "展开",
                    )
                }
                AnimatedVisibility(advancedExpanded) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SchedulePickerField(
                            label = "开始时间",
                            value = startTime.ifBlank { "未设置" },
                            onClear = if (startTime.isBlank()) null else ({ startTime = "" }),
                            icon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = C.gold) },
                            onClick = { picker = ScheduleEditorPicker.StartTime },
                        )
                        ScheduleEndDateTimeField(
                            dateText = endDate.takeIf(String::isNotBlank)
                                ?.let { runCatching { formatScheduleFullDate(LocalDate.parse(it)) }.getOrNull() },
                            timeText = endTime.takeIf(String::isNotBlank),
                            onClear = if (endDate.isBlank() && endTime.isBlank()) null else ({
                                endDate = ""
                                endTime = ""
                            }),
                            onClick = { picker = ScheduleEditorPicker.EndDateTime },
                        )
                        ScheduleGlassTextField(
                            value = location,
                            onValueChange = { location = it.take(240) },
                            label = "位置",
                            singleLine = true,
                        )
                        ScheduleGlassTextField(
                            value = participants,
                            onValueChange = { participants = it.take(500) },
                            label = "参与者",
                            placeholder = "多人可用逗号分隔",
                        )
                        ScheduleColorPicker(
                            selected = highlightColor,
                            onSelected = { highlightColor = it },
                        )
                        GeminiGlassPanel(
                            tone = GeminiGlassTone.Neutral,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text("卡片背景图", color = C.textSub, style = MaterialTheme.typography.labelLarge)
                                if (backgroundImageUri.isNotBlank()) {
                                    ScheduleBackgroundImage(
                                        uri = backgroundImageUri,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 7f)
                                            .clip(MaterialTheme.shapes.large),
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    ScheduleGlassButton(
                                        label = if (backgroundImageUri.isBlank()) "选择图片" else "更换图片",
                                        onClick = { imagePicker.launch(arrayOf("image/*")) },
                                        modifier = Modifier.weight(1f),
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.AddPhotoAlternate,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                            )
                                        },
                                    )
                                    if (backgroundImageUri.isNotBlank()) {
                                        ScheduleGlassButton(
                                            label = "移除",
                                            onClick = { backgroundImageUri = "" },
                                            tone = GeminiGlassTone.Rose,
                                        )
                                    }
                                }
                            }
                        }
                        ScheduleSwitchRow("置顶", pinned) { pinned = it }
                        ScheduleSwitchRow("已完成", done) { done = it }
                    }
                }
            }
            item(key = "day-items-title") {
                Text(
                    "${formatScheduleFullDate(selectedDate)}日程",
                    color = C.textMain,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (dayItems.isEmpty()) {
                item(key = "day-items-empty") {
                    GeminiGlassPanel(
                        tone = GeminiGlassTone.Neutral,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("当日还没有日程。", color = C.textSub, modifier = Modifier.padding(16.dp))
                    }
                }
            } else {
                items(dayItems, key = { "day-item-${it.id}" }) { item ->
                    GeminiGlassPanel(
                        tone = GeminiGlassTone.Neutral,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            if (item.pinned) Icon(Icons.Default.PushPin, contentDescription = "置顶", tint = scheduleAccent(item, C))
                            Text(item.title, color = C.textMain, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(item.startTime, color = C.textSub, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item(key = "editor-bottom") { Spacer(Modifier.height(24.dp)) }
        }
    }

    when (picker) {
        ScheduleEditorPicker.StartDate -> ScheduleDateWheelDialog(
            title = "选择日期",
            initialDate = selectedDate,
            onDismiss = { picker = null },
            onConfirm = {
                dateText = it.toString()
                picker = null
            },
        )
        ScheduleEditorPicker.StartTime -> ScheduleTimeWheelDialog(
            title = "选择开始时间",
            initialTime = startTime.ifBlank { defaultScheduleStartTime() },
            onDismiss = { picker = null },
            onConfirm = {
                startTime = it
                picker = null
            },
        )
        ScheduleEditorPicker.EndDateTime -> {
            val defaults = remember(selectedDate, startTime, picker) {
                defaultScheduleEnd(selectedDate, startTime)
            }
            ScheduleDateTimeWheelDialog(
                title = "选择结束时间",
                initialDate = endDate.takeIf(String::isNotBlank)
                    ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                    ?: defaults.date,
                initialTime = endTime.ifBlank { defaults.time },
                onDismiss = { picker = null },
                onConfirm = { date, time ->
                    endDate = date.toString()
                    endTime = time
                    picker = null
                },
            )
        }
        null -> Unit
    }
}

@Composable
internal fun ScheduleMemorialScreen(
    items: List<ScheduleItem>,
    onBack: () -> Unit,
    onItemClick: (ScheduleItem) -> Unit,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val today = remember { LocalDate.now() }
    var sortName by rememberSaveable { mutableStateOf(ScheduleSortOrder.StartDateDescending.name) }
    val sort = runCatching { ScheduleSortOrder.valueOf(sortName) }.getOrDefault(ScheduleSortOrder.StartDateDescending)
    val sortedItems = remember(items, sort, today) { sortScheduleItems(items, sort, today) }
    var menuExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        SubScreenHeader(title = "日程纪念", onBack = onBack) {}
        Box(modifier = Modifier.padding(horizontal = layout.horizontalPadding, vertical = 8.dp)) {
            ScheduleGlassControl(
                onClick = { menuExpanded = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(sort.label, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                Icon(Icons.Default.ExpandMore, contentDescription = null)
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                ScheduleSortOrder.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        leadingIcon = if (option == sort) ({ Icon(Icons.Default.Check, contentDescription = null) }) else null,
                        onClick = {
                            sortName = option.name
                            menuExpanded = false
                        },
                    )
                }
            }
        }
        if (sortedItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("还没有日程。", color = C.textSub)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = layout.horizontalPadding,
                    vertical = 8.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(sortedItems, key = { it.id }) { item ->
                    ScheduleMemorialCard(item = item, today = today, onClick = { onItemClick(item) })
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
internal fun ScheduleDetailScreen(
    item: ScheduleItem,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val today = remember { LocalDate.now() }
    var confirmDelete by remember { mutableStateOf(false) }
    val startDate = scheduleStartDate(item) ?: today
    val endDate = scheduleEndDate(item)

    Column(Modifier.fillMaxSize()) {
        SubScreenHeader(title = "日程详情", onBack = onBack) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "修改日程", tint = C.gold)
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = layout.horizontalPadding,
                vertical = 10.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item(key = "hero") { ScheduleDetailHero(item, today) }
            item(key = "time") {
                ScheduleInfoSection("时间") {
                    ScheduleInfoRow("开始", formatScheduleFullDate(startDate) + item.startTime.takeIf(String::isNotBlank)?.let { " $it" }.orEmpty())
                    ScheduleInfoRow(
                        "结束",
                        endDate?.let { formatScheduleFullDate(it) + item.endTime.takeIf(String::isNotBlank)?.let { time -> " $time" }.orEmpty() }
                            ?: "未设置（持续中）",
                    )
                    ScheduleInfoRow("经历天数", "${scheduleExperienceDays(item, today)}天")
                }
            }
            if (item.note.isNotBlank() || item.location.isNotBlank() || item.participants.isNotBlank()) {
                item(key = "details") {
                    ScheduleInfoSection("详细信息") {
                        if (item.note.isNotBlank()) ScheduleInfoRow("备注", item.note)
                        if (item.location.isNotBlank()) ScheduleInfoRow("位置", item.location)
                        if (item.participants.isNotBlank()) ScheduleInfoRow("参与者", item.participants)
                    }
                }
            }
            item(key = "state") {
                ScheduleInfoSection("记录状态") {
                    ScheduleInfoRow("置顶", if (item.pinned) "是" else "否")
                    ScheduleInfoRow("状态", if (item.done) "已完成" else "未完成")
                    ScheduleInfoRow("创建于", item.createdAt.ifBlank { "未记录" })
                }
            }
            item(key = "actions") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScheduleGlassButton(
                        label = if (item.done) "标记未完成" else "标记已完成",
                        onClick = onToggleDone,
                        modifier = Modifier.weight(1f),
                        tone = GeminiGlassTone.Teal,
                    )
                    ScheduleGlassButton(
                        label = "删除",
                        onClick = { confirmDelete = true },
                        tone = GeminiGlassTone.Rose,
                        contentColor = C.danger,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (confirmDelete) {
        ScheduleGlassDialog(
            title = "删除日程",
            tone = GeminiGlassTone.Rose,
            onDismiss = { confirmDelete = false },
            content = { Text("确定删除“${item.title}”吗？", color = C.textMain) },
            actions = {
                TextButton(onClick = { confirmDelete = false }) { Text("取消") }
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete()
                }) { Text("删除", color = C.danger) }
            },
        )
    }
}

@Composable
private fun ScheduleMemorialCard(item: ScheduleItem, today: LocalDate, onClick: () -> Unit) {
    val C = LocalFortunePalette.current
    val accent = scheduleAccent(item, C)
    val distance = scheduleDayDistance(item, today)
    val hasImage = item.backgroundImageUri.isNotBlank()
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = MaterialTheme.shapes.extraLarge,
        border = if (item.pinned) BorderStroke(1.dp, accent.copy(alpha = 0.72f)) else null,
        modifier = Modifier.fillMaxWidth(),
    ) {
        GeminiGlassPanel(
            tone = GeminiGlassTone.Neutral,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.fillMaxWidth(),
        ) {
        Box(modifier = Modifier.fillMaxWidth().height(112.dp).clipToBounds()) {
            if (hasImage) {
                ScheduleBackgroundImage(item.backgroundImageUri, Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.48f)))
            } else {
                Box(Modifier.fillMaxSize().background(accent.copy(alpha = 0.09f)))
                Box(Modifier.width(5.dp).height(112.dp).background(accent))
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 15.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.title,
                        color = if (hasImage) Color.White else C.textMain,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (item.pinned) {
                        Icon(Icons.Default.PushPin, contentDescription = "置顶", tint = if (hasImage) Color.White else accent, modifier = Modifier.size(18.dp))
                    }
                }
                ScheduleDistanceLabel(distance, accent, onImage = hasImage)
            }
        }
        }
    }
}

@Composable
private fun ScheduleDetailHero(item: ScheduleItem, today: LocalDate) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val accent = scheduleAccent(item, C)
    val hasImage = item.backgroundImageUri.isNotBlank()
    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item.pinned) {
                    Modifier.border(1.dp, accent.copy(alpha = 0.72f), MaterialTheme.shapes.extraLarge)
                } else {
                    Modifier
                },
            ),
    ) {
        Box(Modifier.fillMaxWidth().height(if (layout.compactHeight) 156.dp else 180.dp).clipToBounds()) {
            if (hasImage) {
                ScheduleBackgroundImage(item.backgroundImageUri, Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.48f)))
            } else {
                Box(Modifier.fillMaxSize().background(accent.copy(alpha = 0.1f)))
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(if (layout.compactWidth) 14.dp else 20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.title,
                        color = if (hasImage) Color.White else C.textMain,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    if (item.pinned) Icon(Icons.Default.PushPin, contentDescription = "置顶", tint = if (hasImage) Color.White else accent)
                }
                ScheduleDistanceLabel(scheduleDayDistance(item, today), accent, onImage = hasImage)
            }
        }
    }
}

@Composable
private fun ScheduleDistanceLabel(distance: ScheduleDayDistance, accent: Color, onImage: Boolean) {
    val base = if (onImage) Color.White else LocalFortunePalette.current.textMain
    when (distance.relation) {
        ScheduleDayDistance.Relation.Today -> Text("就是今天", color = if (onImage) Color.White else accent, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        ScheduleDayDistance.Relation.Future,
        ScheduleDayDistance.Relation.Past -> Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(if (distance.relation == ScheduleDayDistance.Relation.Future) "还有" else "已经", color = base, style = MaterialTheme.typography.bodyLarge)
            Text(distance.days.toString(), color = if (onImage) Color.White else accent, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("天", color = base, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ScheduleInfoSection(title: String, content: @Composable () -> Unit) {
    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun ScheduleInfoRow(label: String, value: String) {
    val C = LocalFortunePalette.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Text(label, color = C.textSub, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(76.dp))
        Text(value, color = C.textMain, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ScheduleGlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
) {
    GeminiGlassTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { hint -> ({ Text(hint) }) },
        singleLine = singleLine,
        minLines = minLines,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun ScheduleGlassControl(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: GeminiGlassTone = GeminiGlassTone.Neutral,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        modifier = modifier,
    ) {
        GeminiGlassPanel(
            tone = tone,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun ScheduleGlassButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: GeminiGlassTone = GeminiGlassTone.Neutral,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    ScheduleGlassControl(
        onClick = onClick,
        modifier = modifier,
        tone = tone,
        contentColor = contentColor,
    ) {
        leadingIcon?.invoke()
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ScheduleGlassDialog(
    title: String,
    onDismiss: () -> Unit,
    tone: GeminiGlassTone = GeminiGlassTone.Neutral,
    content: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
    val C = LocalFortunePalette.current
    val configuration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val scrimInteraction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.52f))
                    .clickable(
                        interactionSource = scrimInteraction,
                        indication = null,
                        onClick = onDismiss,
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center,
            ) {
                GeminiGlassPanel(
                    tone = tone,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp),
                ) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                                    alpha = if (C.isLight) 0.26f else 0.34f,
                                ),
                            ),
                    )
                    Column(
                        modifier = Modifier
                            .heightIn(max = (configuration.screenHeightDp * 0.88f).dp)
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        content()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            actions()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SchedulePickerField(
    label: String,
    value: String,
    icon: @Composable (() -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val C = LocalFortunePalette.current
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        GeminiGlassPanel(
            tone = GeminiGlassTone.Neutral,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
        ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            icon?.invoke()
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(label, color = C.textSub, style = MaterialTheme.typography.labelMedium)
                Text(value, color = C.textMain, style = MaterialTheme.typography.bodyMedium)
            }
            if (onClear != null) {
                TextButton(onClick = onClear) { Text("清除", style = MaterialTheme.typography.labelMedium) }
            } else {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = C.textSub)
            }
        }
        }
    }
}

@Composable
private fun ScheduleEndDateTimeField(
    dateText: String?,
    timeText: String?,
    onClear: (() -> Unit)?,
    onClick: () -> Unit,
) {
    val C = LocalFortunePalette.current
    val value = when {
        dateText == null && timeText == null -> "未设置（默认持续中）"
        dateText != null && timeText != null -> "$dateText  $timeText"
        dateText != null -> "$dateText  时间未设置"
        else -> "日期未设置  $timeText"
    }
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        GeminiGlassPanel(
            tone = GeminiGlassTone.Neutral,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
        ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Default.Schedule, contentDescription = null, tint = C.gold)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("结束时间", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                Text(value, color = C.textMain, style = MaterialTheme.typography.bodyMedium)
            }
            if (onClear != null) {
                TextButton(onClick = onClear) { Text("清除", style = MaterialTheme.typography.labelMedium) }
            } else {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = C.textSub)
            }
        }
        }
    }
}

@Composable
private fun ScheduleSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val C = LocalFortunePalette.current
    Surface(
        onClick = { onCheckedChange(!checked) },
        color = Color.Transparent,
        contentColor = C.textMain,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        GeminiGlassPanel(
            tone = if (checked) GeminiGlassTone.Teal else GeminiGlassTone.Neutral,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, color = C.textMain, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Switch(checked = checked, onCheckedChange = null)
            }
        }
    }
}

@Composable
private fun ScheduleColorPicker(selected: String, onSelected: (String) -> Unit) {
    val C = LocalFortunePalette.current
    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("卡片高亮颜色", color = C.textSub, style = MaterialTheme.typography.labelLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                scheduleHighlightColors.forEach { value ->
                    val color = if (value.isBlank()) C.gold else scheduleColor(value, C.gold)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color, CircleShape)
                            .border(if (selected == value) 3.dp else 1.dp, if (selected == value) C.textMain else C.line, CircleShape)
                            .clickable { onSelected(value) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected == value) Icon(Icons.Default.Check, contentDescription = "已选择", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleDateWheelDialog(
    title: String,
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
) {
    var year by rememberSaveable(initialDate.toString()) { mutableStateOf(initialDate.year) }
    var month by rememberSaveable(initialDate.toString()) { mutableStateOf(initialDate.monthValue) }
    var day by rememberSaveable(initialDate.toString()) { mutableStateOf(initialDate.dayOfMonth) }
    val maxDay = YearMonth.of(year, month).lengthOfMonth()
    val safeDay = day.coerceAtMost(maxDay)
    LaunchedEffect(maxDay) {
        if (day > maxDay) day = maxDay
    }
    val selectedDate = LocalDate.of(year, month, safeDay)

    ScheduleGlassDialog(
        title = title,
        onDismiss = onDismiss,
        tone = GeminiGlassTone.Gold,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(formatScheduleFullDate(selectedDate), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ScheduleNumberWheel(
                        values = (1900..2100).toList(),
                        selected = year,
                        suffix = "年",
                        onSelected = { year = it },
                        modifier = Modifier.weight(1.45f),
                    )
                    ScheduleNumberWheel(
                        values = (1..12).toList(),
                        selected = month,
                        suffix = "月",
                        onSelected = { month = it },
                        modifier = Modifier.weight(1f),
                    )
                    ScheduleNumberWheel(
                        values = (1..maxDay).toList(),
                        selected = safeDay,
                        suffix = "日",
                        onSelected = { day = it },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        actions = {
            TextButton(onClick = onDismiss) { Text("取消") }
            TextButton(onClick = { onConfirm(selectedDate) }) { Text("确定") }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleTimeWheelDialog(
    title: String,
    initialTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val parts = initialTime.split(":")
    var hour by rememberSaveable(initialTime) { mutableStateOf(parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 9) }
    var minute by rememberSaveable(initialTime) { mutableStateOf(parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0) }
    ScheduleGlassDialog(
        title = title,
        onDismiss = onDismiss,
        tone = GeminiGlassTone.Gold,
        content = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ScheduleNumberWheel((0..23).toList(), hour, "时", { hour = it }, Modifier.weight(1f))
                ScheduleNumberWheel((0..59).toList(), minute, "分", { minute = it }, Modifier.weight(1f))
            }
        },
        actions = {
            TextButton(onClick = onDismiss) { Text("取消") }
            TextButton(onClick = { onConfirm("%02d:%02d".format(hour, minute)) }) { Text("确定") }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleDateTimeWheelDialog(
    title: String,
    initialDate: LocalDate,
    initialTime: String,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, String) -> Unit,
) {
    val parts = initialTime.split(":")
    var year by rememberSaveable(initialDate.toString(), initialTime) { mutableStateOf(initialDate.year) }
    var month by rememberSaveable(initialDate.toString(), initialTime) { mutableStateOf(initialDate.monthValue) }
    var day by rememberSaveable(initialDate.toString(), initialTime) { mutableStateOf(initialDate.dayOfMonth) }
    var hour by rememberSaveable(initialDate.toString(), initialTime) {
        mutableStateOf(parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 9)
    }
    var minute by rememberSaveable(initialDate.toString(), initialTime) {
        mutableStateOf(parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0)
    }
    val maxDay = YearMonth.of(year, month).lengthOfMonth()
    val safeDay = day.coerceAtMost(maxDay)
    LaunchedEffect(maxDay) {
        if (day > maxDay) day = maxDay
    }
    val selectedDate = LocalDate.of(year, month, safeDay)
    val selectedTime = "%02d:%02d".format(hour, minute)

    ScheduleGlassDialog(
        title = title,
        onDismiss = onDismiss,
        tone = GeminiGlassTone.Gold,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "${formatScheduleFullDate(selectedDate)}  $selectedTime",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                )
                Text("日期", style = MaterialTheme.typography.labelMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ScheduleNumberWheel(
                        values = (1900..2100).toList(),
                        selected = year,
                        suffix = "年",
                        onSelected = { year = it },
                        modifier = Modifier.weight(1.45f),
                        visibleRows = 3,
                    )
                    ScheduleNumberWheel(
                        values = (1..12).toList(),
                        selected = month,
                        suffix = "月",
                        onSelected = { month = it },
                        modifier = Modifier.weight(1f),
                        visibleRows = 3,
                    )
                    ScheduleNumberWheel(
                        values = (1..maxDay).toList(),
                        selected = safeDay,
                        suffix = "日",
                        onSelected = { day = it },
                        modifier = Modifier.weight(1f),
                        visibleRows = 3,
                    )
                }
                Text("时刻", style = MaterialTheme.typography.labelMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScheduleNumberWheel(
                        (0..23).toList(), hour, "时", { hour = it }, Modifier.weight(1f), visibleRows = 3
                    )
                    ScheduleNumberWheel(
                        (0..59).toList(), minute, "分", { minute = it }, Modifier.weight(1f), visibleRows = 3
                    )
                }
            }
        },
        actions = {
            TextButton(onClick = onDismiss) { Text("取消") }
            TextButton(onClick = { onConfirm(selectedDate, selectedTime) }) { Text("确定") }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleNumberWheel(
    values: List<Int>,
    selected: Int,
    suffix: String,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visibleRows: Int = 5,
) {
    val C = LocalFortunePalette.current
    val edgeRows = (visibleRows.coerceAtLeast(3) / 2)
    val displayValues = remember(values, edgeRows) {
        List<Int?>(edgeRows) { null } + values + List<Int?>(edgeRows) { null }
    }
    val selectedIndex = values.indexOf(selected).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(listState)

    LaunchedEffect(values, selected) {
        if (!listState.isScrollInProgress) {
            val centeredIndex = listState.firstVisibleItemIndex + edgeRows
            val centeredValue = displayValues.getOrNull(centeredIndex)
            if (centeredValue != selected) listState.scrollToItem(selectedIndex)
        }
    }
    LaunchedEffect(listState, values) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrolling ->
            if (!scrolling) {
                val center = (listState.layoutInfo.viewportStartOffset + listState.layoutInfo.viewportEndOffset) / 2
                val nearest = listState.layoutInfo.visibleItemsInfo.minByOrNull { item ->
                    kotlin.math.abs(item.offset + item.size / 2 - center)
                }
                val value = nearest?.index?.let(displayValues::getOrNull)
                if (value != null && value != selected) onSelected(value)
            }
        }
    }

    Box(modifier = modifier.height((44 * (edgeRows * 2 + 1)).dp).clipToBounds()) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(displayValues) { _, value ->
                Box(Modifier.fillMaxWidth().height(44.dp), contentAlignment = Alignment.Center) {
                    if (value != null) {
                        Text(
                            "$value$suffix",
                            color = if (value == selected) C.gold else C.textSub,
                            style = if (value == selected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                            fontWeight = if (value == selected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(44.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
                    MaterialTheme.shapes.medium,
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.64f),
                    shape = MaterialTheme.shapes.medium,
                ),
        )
    }
}

@Composable
private fun ScheduleBackgroundImage(uri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf(scheduleImageCache.get(uri)) }
    LaunchedEffect(uri) {
        if (bitmap == null && uri.isNotBlank()) {
            bitmap = withContext(Dispatchers.IO) {
                scheduleImageCache.get(uri) ?: decodeScheduleImage(context, uri)?.also {
                    scheduleImageCache.put(uri, it)
                }
            }
        }
    }
    val image = bitmap
    if (image != null) {
        Image(bitmap = image, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
    } else {
        Box(modifier.background(LocalFortunePalette.current.panelAlt), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Image, contentDescription = null, tint = LocalFortunePalette.current.textSub)
        }
    }
}

private fun decodeScheduleImage(context: android.content.Context, uriText: String): ImageBitmap? = runCatching {
    val uri = Uri.parse(uriText)
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val width = info.size.width
            val height = info.size.height
            val longest = maxOf(width, height)
            if (longest > 1080) {
                val scale = 1080f / longest
                decoder.setTargetSize((width * scale).toInt().coerceAtLeast(1), (height * scale).toInt().coerceAtLeast(1))
            }
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } else {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > 1080 || bounds.outHeight / sample > 1080) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }
    bitmap?.asImageBitmap()
}.getOrNull()

private fun ScheduleItem.hasAdvancedScheduleContent(): Boolean =
    startTime.isNotBlank() || endDate.isNotBlank() || endTime.isNotBlank() || location.isNotBlank() ||
        participants.isNotBlank() || highlightColor.isNotBlank() || backgroundImageUri.isNotBlank() || pinned || done

private fun scheduleAccent(item: ScheduleItem, palette: FortunePalette): Color =
    scheduleColor(item.highlightColor, palette.gold)

private fun scheduleColor(value: String, fallback: Color): Color =
    if (value.isBlank()) fallback else runCatching { Color(android.graphics.Color.parseColor(value)) }.getOrDefault(fallback)

private fun formatScheduleFullDate(date: LocalDate): String {
    val weekday = listOf("一", "二", "三", "四", "五", "六", "日")[date.dayOfWeek.value - 1]
    return "${date.year}年${date.monthValue}月${date.dayOfMonth}日 周$weekday"
}

private enum class ScheduleEditorPicker { StartDate, StartTime, EndDateTime }
