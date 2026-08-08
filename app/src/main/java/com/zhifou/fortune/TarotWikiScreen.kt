package com.zhifou.fortune

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private const val TAROT_CARD_ASPECT_RATIO = 0.58f

@Composable
fun TarotWikiScreen(onBack: () -> Unit) {
    var selectedCardId by rememberSaveable { mutableStateOf<String?>(null) }
    var sectionIndex by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var filterName by rememberSaveable { mutableStateOf(TarotWikiFilter.ALL.name) }
    val listState = rememberLazyListState()

    BackHandler {
        if (selectedCardId != null) {
            selectedCardId = null
        } else {
            onBack()
        }
    }

    val selectedEntry = selectedCardId?.let(TarotWikiLibrary::entry)
    if (selectedEntry != null) {
        TarotWikiDetail(
            entry = selectedEntry,
            onBack = { selectedCardId = null },
            onSelectCard = { selectedCardId = it },
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TarotWikiTopBar(title = "韦特塔罗 Wiki", onBack = onBack) {
            Text(
                "78 张",
                color = LocalFortunePalette.current.gold,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            listOf("牌库", "体系").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = sectionIndex == index,
                    onClick = { sectionIndex = index },
                    shape = SegmentedButtonDefaults.itemShape(index, 2),
                    label = { Text(label) },
                )
            }
        }

        if (sectionIndex == 0) {
            TarotWikiLibraryView(
                query = query,
                onQueryChange = { query = it },
                selectedFilter = TarotWikiFilter.valueOf(filterName),
                onFilterChange = { filterName = it.name },
                listState = listState,
                onOpenCard = { selectedCardId = it.card.id },
            )
        } else {
            TarotWikiSystemView()
        }
    }
}

@Composable
private fun TarotWikiLibraryView(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedFilter: TarotWikiFilter,
    onFilterChange: (TarotWikiFilter) -> Unit,
    listState: LazyListState,
    onOpenCard: (TarotWikiEntry) -> Unit,
) {
    val C = LocalFortunePalette.current
    val results = remember(query, selectedFilter) {
        TarotWikiLibrary.search(query, selectedFilter)
    }
    LaunchedEffect(query, selectedFilter) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "清空搜索")
                    }
                }
            } else {
                null
            },
            placeholder = { Text("搜索牌名、关键词或含义") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 4.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TarotWikiFilter.entries.forEach { filter ->
                val selected = filter == selectedFilter
                Surface(
                    onClick = { onFilterChange(filter) },
                    color = if (selected) C.accentFill else C.panel,
                    contentColor = if (selected) C.onAccentFill else C.textSub,
                    border = BorderStroke(1.dp, if (selected) C.gold else C.line),
                    shape = RoundedCornerShape(6.dp),
                ) {
                    Text(
                        filter.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    )
                }
            }
        }

        if (results.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = C.textSub,
                    modifier = Modifier.size(34.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("没有找到相关牌面", color = C.textMain, style = MaterialTheme.typography.titleMedium)
                Text("可尝试牌名、英文名或关键词", color = C.textSub, style = MaterialTheme.typography.bodyMedium)
            }
            return@Column
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "count") {
                Text(
                    "${results.size} 张牌",
                    color = C.textSub,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }
            items(
                items = results.chunked(2),
                key = { row -> row.joinToString(separator = "-") { it.card.id } },
            ) { rowEntries ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    rowEntries.forEach { entry ->
                        TarotWikiCardTile(
                            entry = entry,
                            onClick = { onOpenCard(entry) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowEntries.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TarotWikiCardTile(
    entry: TarotWikiEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val C = LocalFortunePalette.current
    Surface(
        onClick = onClick,
        color = C.panel,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, C.line),
        modifier = modifier,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (C.isLight) Color(0xFFE5DED0) else Color(0xFF0C0E12))
                    .padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(entry.card.imageRes),
                    contentDescription = entry.card.nameZh,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .aspectRatio(TAROT_CARD_ASPECT_RATIO)
                        .clip(RoundedCornerShape(3.dp)),
                )
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    entry.card.nameZh,
                    color = C.textMain,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    entry.card.nameEn,
                    color = C.textSub,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    entry.card.uprightKeywords.joinToString(" · "),
                    color = C.gold,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun TarotWikiSystemView() {
    val C = LocalFortunePalette.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item(key = "visual") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (C.isLight) Color(0xFFE5DED0) else Color(0xFF0C0E12)),
            ) {
                Image(
                    painter = painterResource(R.drawable.tarot_major_02),
                    contentDescription = "女祭司",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(170.dp)
                        .aspectRatio(TAROT_CARD_ASPECT_RATIO)
                        .clip(RoundedCornerShape(3.dp)),
                )
                Image(
                    painter = painterResource(R.drawable.tarot_major_00),
                    contentDescription = "愚人",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-76).dp, y = 8.dp)
                        .height(150.dp)
                        .aspectRatio(TAROT_CARD_ASPECT_RATIO)
                        .clip(RoundedCornerShape(3.dp)),
                )
                Image(
                    painter = painterResource(R.drawable.tarot_major_21),
                    contentDescription = "世界",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 76.dp, y = 8.dp)
                        .height(150.dp)
                        .aspectRatio(TAROT_CARD_ASPECT_RATIO)
                        .clip(RoundedCornerShape(3.dp)),
                )
            }
            Text(
                "Rider-Waite-Smith · 1909",
                color = C.textMain,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
        }
        items(TarotWikiLibrary.overviewTopics, key = TarotWikiTopic::title) { topic ->
            TarotWikiTextSection(title = topic.title, body = topic.body)
        }
        item(key = "sources") {
            TarotWikiTextSection(
                title = "主要来源",
                body = "Arthur Edward Waite：《The Pictorial Key to the Tarot》；Pamela Colman Smith：Rider-Waite-Smith 78 张原始牌面；Victoria and Albert Museum：《A history of tarot cards》；The Metropolitan Museum of Art：《Before Fortune-Telling: The History and Structure of Tarot Cards》。牌面图片与原始文本已进入公有领域，应用内资料可完全离线阅读。",
            )
        }
    }
}

@Composable
private fun TarotWikiDetail(
    entry: TarotWikiEntry,
    onBack: () -> Unit,
    onSelectCard: (String) -> Unit,
) {
    val C = LocalFortunePalette.current
    val detailListState = rememberLazyListState()
    val index = TarotWikiLibrary.entries.indexOfFirst { it.card.id == entry.card.id }
    val previous = TarotWikiLibrary.entries.getOrNull(index - 1)
    val next = TarotWikiLibrary.entries.getOrNull(index + 1)
    LaunchedEffect(entry.card.id) {
        detailListState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TarotWikiTopBar(title = entry.card.nameZh, onBack = onBack) {
            Text(
                "${index + 1} / 78",
                color = C.textSub,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        LazyColumn(
            state = detailListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 32.dp,
            ),
        ) {
            item(key = "hero") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(entry.card.imageRes),
                        contentDescription = entry.card.nameZh,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(142.dp)
                            .aspectRatio(TAROT_CARD_ASPECT_RATIO)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            entry.card.classificationLabel,
                            color = C.gold,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            entry.card.nameZh,
                            color = C.textMain,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            entry.card.nameEn,
                            color = C.textSub,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            entry.card.uprightKeywords.joinToString(" · "),
                            color = C.textMain,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
            item(key = "role") {
                TarotWikiTextSection("分类与作用", entry.deckRole)
            }
            item(key = "artwork") {
                TarotWikiTextSection("牌面与象征", entry.artworkAndSymbols)
            }
            item(key = "usage") {
                TarotWikiTextSection("在牌阵中的用途", entry.usage)
            }
            item(key = "upright") {
                TarotWikiMeaningSection(
                    title = "正位",
                    keywords = entry.card.uprightKeywords,
                    meaning = entry.card.uprightMeaning,
                    accent = C.mint,
                )
            }
            item(key = "reversed") {
                TarotWikiMeaningSection(
                    title = "逆位",
                    keywords = entry.card.reversedKeywords,
                    meaning = entry.card.reversedMeaning,
                    accent = C.rose,
                )
            }
            item(key = "interpretation") {
                TarotWikiTextSection("解释方法", entry.interpretation)
            }
            item(key = "history") {
                TarotWikiTextSection("背景与历史", entry.historicalBackground)
            }
            item(key = "distinction") {
                TarotWikiTextSection("与相似牌的区别", entry.distinction)
            }
            item(key = "questions") {
                TarotWikiTextSection(
                    "自省问题",
                    entry.reflectionPrompts.joinToString(separator = "\n") { "· $it" },
                )
            }
            item(key = "navigation") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TarotWikiAdjacentButton(
                        label = previous?.card?.nameZh ?: "已经是第一张",
                        iconLeft = true,
                        enabled = previous != null,
                        onClick = { previous?.let { onSelectCard(it.card.id) } },
                        modifier = Modifier.weight(1f),
                    )
                    TarotWikiAdjacentButton(
                        label = next?.card?.nameZh ?: "已经是最后一张",
                        iconLeft = false,
                        enabled = next != null,
                        onClick = { next?.let { onSelectCard(it.card.id) } },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun TarotWikiTopBar(
    title: String,
    onBack: () -> Unit,
    trailing: @Composable () -> Unit,
) {
    val C = LocalFortunePalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = C.gold)
        }
        Text(
            title,
            color = C.textMain,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
        )
        Box(modifier = Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
            trailing()
        }
    }
}

@Composable
private fun TarotWikiTextSection(title: String, body: String) {
    val C = LocalFortunePalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .background(C.gold, RoundedCornerShape(2.dp)),
            )
            Text(
                title,
                color = C.textMain,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
        Text(
            body,
            color = C.textSub,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 13.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(1.dp)
                .background(C.line),
        )
    }
}

@Composable
private fun TarotWikiMeaningSection(
    title: String,
    keywords: List<String>,
    meaning: String,
    accent: Color,
) {
    val C = LocalFortunePalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(C.panel, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            title,
            color = accent,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            keywords.joinToString(" · "),
            color = C.textMain,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(meaning, color = C.textSub, style = MaterialTheme.typography.bodyLarge)
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun TarotWikiAdjacentButton(
    label: String,
    iconLeft: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val C = LocalFortunePalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        color = C.panel,
        contentColor = if (enabled) C.textMain else C.textSub.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, C.line),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (iconLeft) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
            }
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            if (!iconLeft) {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun TarotWikiToolCard(onClick: () -> Unit) {
    val C = LocalFortunePalette.current
    val compactWidth = LocalConfiguration.current.screenWidthDp < 360
    Surface(
        onClick = onClick,
        color = C.panel,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, C.line),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compactWidth) 142.dp else 176.dp)
                    .background(if (C.isLight) Color(0xFFE5DED0) else Color(0xFF0C0E12)),
            ) {
                Image(
                    painter = painterResource(R.drawable.tarot_major_02),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(if (compactWidth) 128.dp else 160.dp)
                        .aspectRatio(TAROT_CARD_ASPECT_RATIO)
                        .clip(RoundedCornerShape(3.dp)),
                )
                Image(
                    painter = painterResource(R.drawable.tarot_major_00),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = if (compactWidth) (-70).dp else (-88).dp, y = 12.dp)
                        .height(if (compactWidth) 108.dp else 138.dp)
                        .aspectRatio(TAROT_CARD_ASPECT_RATIO)
                        .clip(RoundedCornerShape(3.dp)),
                )
                Image(
                    painter = painterResource(R.drawable.tarot_major_21),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = if (compactWidth) 70.dp else 88.dp, y = 12.dp)
                        .height(if (compactWidth) 108.dp else 138.dp)
                        .aspectRatio(TAROT_CARD_ASPECT_RATIO)
                        .clip(RoundedCornerShape(3.dp)),
                )
                Surface(
                    color = C.ink.copy(alpha = 0.88f),
                    contentColor = C.gold,
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(if (compactWidth) 9.dp else 12.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("78 张完整牌库", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Column(
                modifier = Modifier.padding(if (compactWidth) 12.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    "韦特塔罗 Wiki",
                    color = C.textMain,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "按类别查阅 78 张原始牌面、象征、牌义、用途与历史。",
                    color = C.textSub,
                    style = if (compactWidth) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
