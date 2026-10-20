package com.zhifou.fortune

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

private val CardShape = RoundedCornerShape(5.dp)

@Composable
internal fun FourCardTarotScreen(vm: FortuneViewModel, onBack: () -> Unit) {
    FourCardTarotExperience(
        history = vm.history,
        aiConfigured = isAiConfigurationComplete(vm.aiEndpoint, vm.aiApiKey, vm.aiModel),
        aiBusyIds = vm.fourCardAiBusyIds,
        onRecord = vm::recordFourCardTarot,
        onExplain = vm::explainFourCardTarot,
        onBack = onBack,
    )
}

/** Callbacks are injectable so the complete gesture flow can be checked without private data or APIs. */
@Composable
internal fun FourCardTarotExperience(
    history: List<FortuneReading>,
    aiConfigured: Boolean,
    aiBusyIds: Set<Long>,
    onRecord: (FourCardSession) -> Unit,
    onExplain: (Long) -> Unit,
    onBack: () -> Unit,
) {
    var question by rememberSaveable { mutableStateOf("") }
    var reversals by rememberSaveable { mutableStateOf(true) }
    var phase by rememberSaveable { mutableIntStateOf(0) }
    var encodedSession by rememberSaveable { mutableStateOf<String?>(null) }
    val session = remember(encodedSession) { encodedSession?.let(FourCardTarot::decode) }
    var revealed by rememberSaveable { mutableIntStateOf(0) }
    var recorded by rememberSaveable { mutableStateOf(false) }
    var detailPosition by rememberSaveable { mutableIntStateOf(0) }
    var showSources by rememberSaveable { mutableStateOf(false) }
    var confirmExit by remember { mutableStateOf(false) }
    var confirmNew by remember { mutableStateOf(false) }
    val shuffle = remember { Animatable(if (phase == 1) 0f else 1f) }
    val reveal = remember(session?.picked?.size) { Animatable(if ((session?.picked?.size ?: 0) > revealed) 0f else 1f) }
    val reduced = LocalReducedFortuneMotion.current
    val visible = LocalFortuneMotionVisible.current
    val haptics = rememberToolHaptics()
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val latestRecord by rememberUpdatedState(onRecord)
    val complete = session?.complete == true && revealed == 4
    val busy = phase == 1 || (session?.picked?.size ?: 0) > revealed
    val record = remember(session) { session?.takeIf { it.complete }?.let(FourCardTarot::reading) }
    val reading = history.firstOrNull { it.id == session?.id && it.kind == FourCardTarot.KIND } ?: record

    fun back() {
        if (session != null && !session.complete && session.picked.isNotEmpty()) confirmExit = true
        else onBack()
    }
    fun reset() {
        phase = 0
        encodedSession = null
        revealed = 0
        recorded = false
        detailPosition = 0
    }
    BackHandler { back() }

    LaunchedEffect(phase, visible, reduced) {
        if (phase != 1 || !visible) return@LaunchedEffect
        if (session == null) {
            val shuffled = withContext(Dispatchers.Default) { FourCardTarot.shuffled(question, reversals) }
            encodedSession = shuffled.encode()
        }
        shuffle.snapTo(0f)
        haptics.oracleTouch(OracleTouchCue(0, OracleTouch.Shuffle))
        var shuffleCue = 0
        shuffle.animateTo(1f, tween(if (reduced) 0 else FourCardTarotMotion.SHUFFLE_MS, easing = FastOutSlowInEasing)) {
            if (!reduced && shuffleCue < 3 && value >= .2f + shuffleCue * .2f) {
                shuffleCue++
                haptics.oracleTouch(OracleTouchCue(0, OracleTouch.Shuffle))
            }
        }
        phase = 2
    }
    LaunchedEffect(session?.picked?.size, revealed, visible, reduced) {
        val count = session?.picked?.size ?: 0
        if (phase != 2 || count <= revealed || !visible) return@LaunchedEffect
        reveal.snapTo(0f)
        haptics.oracleTouch(OracleTouchCue(0, OracleTouch.CardDraw))
        var turned = false
        reveal.animateTo(1f, tween(if (reduced) 0 else FourCardTarotMotion.REVEAL_MS, easing = LinearEasing)) {
            if (!turned && value >= .46f) {
                turned = true
                haptics.oracleTouch(OracleTouchCue(0, OracleTouch.CardTurn))
            }
        }
        revealed = count
        haptics.oracleTouch(OracleTouchCue(0, if (count == 4) OracleTouch.Complete else OracleTouch.CardSettle))
    }
    LaunchedEffect(session?.id, session?.complete) {
        session?.takeIf { it.complete && !recorded }?.let {
            latestRecord(it)
            recorded = true
        }
    }

    val basePalette = LocalFortunePalette.current
    val atelier = tarotAtelierPalette(basePalette.isLight)
    val atelierColors = MaterialTheme.colorScheme.copy(
        primary = atelier.accent, onPrimary = atelier.background,
        primaryContainer = if (atelier.light) Color(0xFFDCEAF0) else Color(0xFF2A424D),
        onPrimaryContainer = atelier.ink,
        secondary = atelier.accent, onSecondary = atelier.background,
        background = atelier.background, onBackground = atelier.ink,
        surface = atelier.control, onSurface = atelier.ink,
        onSurfaceVariant = atelier.muted, outlineVariant = atelier.line,
        surfaceContainer = atelier.control, surfaceContainerLow = atelier.control,
    )
    val backdrop = rememberGlassBackdropState()
    val glassScene = rememberGlassSceneState()
    MaterialTheme(colorScheme = atelierColors) {
    CompositionLocalProvider(LocalTarotAtelier provides atelier, LocalContentColor provides atelier.ink,
        LocalFortunePalette provides basePalette.copy(ink = atelier.background, panel = atelier.control,
            panelAlt = atelier.control, line = atelier.line, textMain = atelier.ink, textSub = atelier.muted),
        LocalGlassBackdrop provides backdrop, LocalGlassScene provides glassScene) {
    BoxWithConstraints(Modifier.fillMaxSize().background(atelier.background).imePadding()
        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))) {
            val landscape = maxWidth > maxHeight * 1.35f && maxWidth >= 560.dp
            val headerHeight = 62.dp
            val availableHeight = (maxHeight - headerHeight).coerceAtLeast(1.dp)
            val resultStageHeight = (availableHeight * .80f).coerceIn(320.dp, 560.dp)
            val stage: @Composable (Modifier) -> Unit = { modifier ->
                TarotRitualStage(session, revealed, phase == 1, shuffle.asState(), reveal.asState(), reduced,
                    onDetail = { detailPosition = it }, modifier = modifier)
            }
            val controls: @Composable () -> Unit = {
                when {
                    phase == 0 -> TarotIntention(question, { question = it }, reversals, { reversals = it }, landscape) {
                        focus.clearFocus(force = true)
                        keyboard?.hide()
                        phase = 1
                    }
                    phase == 1 -> Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("洗牌中", style = MaterialTheme.typography.titleMedium, color = atelier.ink)
                        LinearProgressIndicator(progress = { shuffle.value }, modifier = Modifier.fillMaxWidth())
                    }
                    !complete -> {
                        val current = session
                        if (current != null) TarotPickDeck(current, busy) { slot ->
                            if (!busy && slot !in current.picked && !current.complete) {
                                encodedSession = current.pick(slot).encode()
                            }
                        }
                    }
                    reading != null -> TarotReadingText(reading, aiConfigured,
                        history.any { it.id == reading.id }, reading.id in aiBusyIds,
                        onExplain = { onExplain(reading.id) }, onNew = { confirmNew = true })
                }
            }
            Box(Modifier.fillMaxSize().captureGlassBackdrop(backdrop)) {
            if (landscape) {
                Row(Modifier.fillMaxSize().padding(top = headerHeight), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    stage(Modifier.weight(.52f).fillMaxHeight())
                    Column(Modifier.weight(.48f).fillMaxHeight().verticalScroll(rememberScrollState())) { controls() }
                }
            } else if (complete) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(headerHeight))
                    stage(Modifier.fillMaxWidth().height(resultStageHeight))
                    controls()
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    Spacer(Modifier.height(headerHeight))
                    stage(Modifier.weight(1f).fillMaxWidth())
                    Column(Modifier.fillMaxWidth().heightIn(max = minOf(270.dp, availableHeight))
                        .verticalScroll(rememberScrollState())) { controls() }
                }
            }
            }
            RealtimeGlassScene(glassScene, backdrop, Modifier.fillMaxSize())
            TarotAtelierHeader(complete, ::back, { confirmNew = true }, { showSources = true }, Modifier.align(Alignment.TopCenter))
    }
    if (showSources) TarotSourcesDialog { showSources = false }
    val detail = session?.draws?.firstOrNull { it.positionIndex == detailPosition && detailPosition <= revealed }
    if (detail != null) TarotFourDetailSheet(detail) { detailPosition = 0 }
    if (confirmExit || confirmNew) AlertDialog(
        onDismissRequest = { confirmExit = false; confirmNew = false },
        title = { Text(if (confirmExit) "离开本次抽牌？" else "开始新的抽牌？") },
        text = { Text(if (confirmExit) "尚未完成的抽牌不会保存。" else "本次结果已保存在占卜记录中。新一轮将重新洗牌。") },
        confirmButton = { TextButton(onClick = {
            if (confirmExit) { confirmExit = false; onBack() }
            else { confirmNew = false; reset() }
        }) { Text(if (confirmExit) "离开" else "重新开始") } },
        dismissButton = { TextButton(onClick = { confirmExit = false; confirmNew = false }) { Text("取消") } },
    )
    }
    }
}

@Composable
private fun TarotAtelierHeader(complete: Boolean, onBack: () -> Unit, onNew: () -> Unit,
    onInfo: () -> Unit, modifier: Modifier) {
    val c = LocalTarotAtelier.current
    Row(modifier.fillMaxWidth().height(62.dp).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AtelierIconButton(Icons.AutoMirrored.Filled.ArrowBack, "返回", onBack)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text("四牌塔罗", color = c.ink, style = MaterialTheme.typography.titleMedium)
            Text("Rider–Waite–Smith", color = c.muted, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
        if (complete) AtelierIconButton(Icons.Default.Refresh, "重新开始", onNew)
        AtelierIconButton(Icons.Default.Info, "牌阵与来源", onInfo)
    }
}

@Composable
private fun AtelierIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    val c = LocalTarotAtelier.current
    Box(Modifier.size(44.dp)) {
        LiquidGlassOptics(CircleShape, c.control, c.light, Modifier.matchParentSize())
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()
            .border(.6.dp, c.line.copy(.7f), CircleShape).background(c.control.copy(.18f), CircleShape)) {
            FortuneIcon(icon, label, tint = c.ink, modifier = Modifier.size(21.dp))
        }
    }
}

@Composable
private fun TarotIntention(question: String, onQuestion: (String) -> Unit, reversals: Boolean,
    onReversals: (Boolean) -> Unit, compact: Boolean, onStart: () -> Unit) {
    val c = LocalTarotAtelier.current
    Column(Modifier.padding(horizontal = if (compact) 16.dp else 24.dp, vertical = if (compact) 8.dp else 16.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)) {
        if (!compact) Text("此刻，想问些什么？", style = MaterialTheme.typography.titleLarge, color = c.ink)
        GeminiGlassTextField(
            value = question, onValueChange = { onQuestion(it.take(2000)) },
            placeholder = { Text("写下问题，或在心中默念", color = c.muted, maxLines = if (compact) 1 else 2,
                overflow = TextOverflow.Ellipsis, style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge) },
            minLines = 1, maxLines = 3, modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        FilterChip(selected = reversals, onClick = hapticAction(if (reversals) HapticCue.ToggleOff else HapticCue.ToggleOn) { onReversals(!reversals) }, label = { Text(if (reversals) "正逆位" else "仅正位") },
            leadingIcon = if (reversals) {{ Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }} else null,
            modifier = Modifier.heightIn(min = 48.dp), shape = MaterialTheme.shapes.medium)
        Button(onClick = onStart, modifier = Modifier.weight(1f).heightIn(min = 52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = c.ink, contentColor = c.background),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
            FortuneIcon(Icons.Default.Shuffle, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("开始抽牌", maxLines = 1)
        }
        }
    }
}

@Composable
private fun TarotPickDeck(session: FourCardSession, busy: Boolean, onPick: (Int) -> Unit) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current.density
    val cardWidth = 76.dp
    val overlap = 12.dp
    HapticTarotDeckFeedback(listState, (cardWidth - overlap).value * density,
        enabled = !busy && LocalFortuneMotionVisible.current)
    val c = LocalTarotAtelier.current
    val lamp = LocalGlassLightController.current
    Column(Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(if (busy) "牌面正在展开" else "选择第 ${session.picked.size + 1} 张牌",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite })
            Text("${session.picked.size} / 4", color = c.accent,
                style = MaterialTheme.typography.labelLarge)
        }
        LazyRow(state = listState, contentPadding = PaddingValues(horizontal = 26.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(-overlap), modifier = Modifier.fillMaxWidth().height(156.dp)) {
            items((0..77).toList(), key = { it }) { slot ->
                val selected = slot in session.picked
                Box(Modifier.width(cardWidth).height(130.dp)
                    .graphicsLayer {
                        val info = listState.layoutInfo
                        val item = info.visibleItemsInfo.firstOrNull { it.index == slot }
                        val middle = (info.viewportStartOffset + info.viewportEndOffset) / 2f
                        val distance = if (item == null) 0f else ((item.offset + item.size / 2f - middle) / 260f).coerceIn(-1f, 1f)
                        rotationZ = distance * 9f
                        rotationY = -distance * 24f
                        rotationX = -5f
                        cameraDistance = 14f * density
                        translationY = abs(distance) * 14f * density
                        alpha = if (selected) .35f else 1f
                        shadowElevation = if (selected) 0f else 4f * density
                        shape = CardShape
                    }
                    .clip(CardShape).background(Color(0xFF20313A)).border(.6.dp, c.metal.copy(.6f), CardShape)
                    .clickable(enabled = !busy && !selected, onClickLabel = "抽取第 ${slot + 1} 张牌背") { onPick(slot) }
                    .semantics { contentDescription = if (selected) "第 ${slot + 1} 张牌，已抽取" else "第 ${slot + 1} 张牌，未翻开" },
                    contentAlignment = Alignment.Center) {
                    TarotAtelierBack(phase = { .25f }, tilt = { Offset(lamp.x, lamp.y) }, modifier = Modifier.fillMaxSize())
                    if (selected) Surface(color = c.control, shape = CircleShape) {
                        Icon(Icons.Default.Check, null, tint = c.accent, modifier = Modifier.padding(6.dp).size(20.dp))
                    }
                }
            }
        }
        Text("${78 - session.picked.size} 张待选", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@Composable
private fun TarotRitualStage(session: FourCardSession?, revealed: Int, shuffling: Boolean,
    shuffle: State<Float>, reveal: State<Float>, reduced: Boolean, onDetail: (Int) -> Unit, modifier: Modifier) {
    val density = LocalDensity.current.density
    val c = LocalTarotAtelier.current
    val lamp = LocalGlassLightController.current
    val count = session?.picked?.size ?: 0
    val active = count > revealed
    val hero = session == null || shuffling
    val heroVisibility = animateFloatAsState(if (hero) 1f else 0f,
        tween(if (reduced) 0 else 460, easing = FastOutSlowInEasing), label = "deck-to-spread")
    val labelVisibility = animateFloatAsState(if (active) 0f else 1f,
        tween(if (reduced) 0 else 160, easing = FastOutSlowInEasing), label = "spread-labels")
    val drawDeck by remember(hero) { derivedStateOf { hero || heroVisibility.value > .001f } }
    BoxWithConstraints(modifier.clipToBounds()) {
        val width = maxWidth
        val height = maxHeight
        val compactSpread = height < 260.dp
        val cardWidth = if (compactSpread) minOf(width * .19f, (height - 46.dp).coerceAtLeast(48.dp) * .58f)
            else minOf(width * .35f, height * .24f)
        val cardHeight = cardWidth / .58f
        RitualEngraving(active, reveal, reduced, Modifier.fillMaxSize())
        if (drawDeck) {
            val stackHeight = minOf((height - 24.dp).coerceAtLeast(40.dp) * .94f, width * .86f)
            // A small number of cached layers suggests a full deck without drawing 78 textures per frame.
            repeat(9) { index ->
                Box(Modifier.align(Alignment.Center).offset(y = if (compactSpread) 0.dp else (-10).dp).height(stackHeight).aspectRatio(.58f)
                    .graphicsLayer {
                        alpha = heroVisibility.value
                        scaleX = .86f + heroVisibility.value * .14f
                        scaleY = scaleX
                        val t = if (shuffling && !reduced) shuffle.value else 0f
                        val fan = sin(t * PI.toFloat())
                        val lane = index - 4
                        rotationZ = lane * (3.8f + fan * 11f)
                        rotationX = if (reduced) 0f else -10f + lamp.y * 5f
                        rotationY = if (reduced) 0f else lamp.x * 6f + sin(t * PI.toFloat() * 4f + index) * fan * 26f
                        translationX = (lane * 5f + sin(t * PI.toFloat() * 4f + index) * fan * 48f) * density
                        translationY = (abs(lane) * 1.5f - fan * 18f + (1f - heroVisibility.value) * 32f) * density
                        shadowElevation = (4f + index * 1.2f) * density
                        shape = CardShape
                        cameraDistance = 12f * density
                    }.clip(CardShape).background(Color(0xFFE8DDC4)).border(.7.dp, c.metal.copy(.55f), CardShape)) {
                    TarotAtelierBack(phase = { if (shuffling && !reduced) shuffle.value else .25f },
                        tilt = { if (reduced) Offset.Zero else Offset(lamp.x, lamp.y) }, modifier = Modifier.fillMaxSize())
                }
            }
        }
        if (!hero) {
            // Captions stay on the table plane, never in the flying card's elevated layer.
            val labelWidth = maxOf(cardWidth + 16.dp, 70.dp)
                .coerceAtMost(width * if (compactSpread) .24f else .44f)
            FourCardTarot.positions.forEach { position ->
                val anchor = tarotAtelierAnchor(position.index, compactSpread)
                Text("${position.index} · ${position.name}", color = c.muted,
                    style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.offset(x = width * anchor.first - labelWidth / 2,
                        y = height * anchor.second + cardHeight / 2 - 3.dp)
                        .width(labelWidth).graphicsLayer { alpha = labelVisibility.value })
            }
            FourCardTarot.positions.forEach { position ->
                // A short window keeps the same numbered positions in a readable linear presentation.
                val anchor = tarotAtelierAnchor(position.index, compactSpread)
                val draw = session.draws.getOrNull(position.index - 1)
                val current = draw != null && position.index > revealed
                val progress = if (current) reveal else remember { mutableFloatStateOf(1f) }
                Box(Modifier.offset(x = width * anchor.first - cardWidth / 2,
                        y = height * anchor.second - cardHeight / 2 - 8.dp).width(cardWidth).zIndex(if (current) 3f else 2f)) {
                    if (draw == null) {
                        Box(Modifier.fillMaxWidth().aspectRatio(.58f),
                            contentAlignment = Alignment.Center) {
                            Text("0${position.index}", color = c.muted.copy(.35f), style = MaterialTheme.typography.labelLarge)
                        }
                    } else RitualPlayingCard(draw, progress, reduced, current,
                        showcaseScale = ((height - 40.dp) / cardHeight).coerceIn(1f, 1.72f),
                        centerDelta = Offset((width * (.5f - anchor.first)).value * density,
                            (height * (.5f - anchor.second) + 8.dp).value * density),
                        onClick = { if (!current) onDetail(position.index) }, modifier = Modifier.fillMaxWidth().aspectRatio(.58f))
                }
            }
        }
    }
}

@Composable
private fun RitualPlayingCard(draw: TarotDraw, progress: State<Float>, reduced: Boolean, active: Boolean,
    showcaseScale: Float, centerDelta: Offset, onClick: () -> Unit, modifier: Modifier) {
    val face = rememberTarotArtwork(draw.card.imageRes)
    val front by remember(active, reduced, progress) { derivedStateOf {
        !active || FourCardTarotMotion.pose(progress.value, reduced).turn >= .5f
    } }
    val density = LocalDensity.current.density
    val c = LocalTarotAtelier.current
    val lamp = LocalGlassLightController.current
    Box(modifier.graphicsLayer {
        val pose = FourCardTarotMotion.pose(if (active) progress.value else 1f, reduced)
        alpha = if (active) (pose.travel * 4f).coerceAtMost(1f) else 1f
        rotationY = if (front) (pose.turn - 1f) * 180f else pose.turn * 180f
        rotationX = if (reduced) 0f else lamp.y * 2f
        translationX = if (active) centerDelta.x * (1f - pose.settle) else 0f
        translationY = if (active) centerDelta.y * (1f - pose.settle) + ((1f - pose.travel) * 90f - pose.lift) * density else 0f
        rotationZ = if (active && !reduced) (1f - pose.travel) * -18f else 0f
        scaleX = 1f + (if (active) (showcaseScale - 1f) * pose.travel * (1f - pose.settle) else 0f)
        scaleY = scaleX
        cameraDistance = 14f * density
        shadowElevation = (5f + pose.lift) * density
        shape = CardShape
    }.drawWithCache {
        onDrawBehind {
            drawRoundRect(Color(0xFFB9AE96), topLeft = Offset(1.dp.toPx(), 2.dp.toPx()),
                size = size, cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx()))
        }
    }.clip(CardShape).background(Color(0xFFEEE9D5)).border(.6.dp, c.metal.copy(.6f), CardShape)
        .clickable(enabled = !active, onClickLabel = "查看${draw.card.nameZh}牌义", onClick = onClick)
        .semantics { contentDescription = if (active) "正在翻开第${draw.positionIndex}张牌" else
            "${FourCardTarot.position(draw).name}，${draw.card.nameZh}，${draw.orientationLabel}" }) {
        if (!front) TarotAtelierBack(phase = { if (active && !reduced) progress.value else 1f },
            tilt = { if (reduced) Offset.Zero else Offset(lamp.x, lamp.y) }, modifier = Modifier.fillMaxSize())
        else if (face != null) Image(face, null, contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = if (front && draw.reversed) 180f else 0f }
                .tarotClearCoat(phase = { if (active) progress.value else 1f },
                    tilt = { Offset(lamp.x, lamp.y) }, enabled = !reduced))
    }
}

/** Light is projected from the moving card; static screens contain no decorative particle loop. */
@Composable
private fun RitualEngraving(active: Boolean, reveal: State<Float>, reduced: Boolean, modifier: Modifier) {
    val c = LocalTarotAtelier.current
    Canvas(modifier.drawWithCache {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) * .43f
        onDrawBehind {
            drawRect(c.background)
            if (active && !reduced) {
                val t = reveal.value
                val glow = sin(t * PI.toFloat()).coerceAtLeast(0f)
                repeat(5) { i ->
                    val ray = Path().apply {
                        moveTo(center.x - radius, center.y + radius * (.5f + i * .07f))
                        cubicTo(center.x - radius * .4f, center.y + radius * (.5f - t),
                            center.x + radius * .4f, center.y + radius * (t - .3f),
                            center.x + radius, center.y + radius * (.3f + i * .09f))
                    }
                    drawPath(ray, Brush.horizontalGradient(listOf(Color.Transparent, c.metal.copy(glow * .16f), Color.Transparent)),
                        style = Stroke((.4f + i * .10f).dp.toPx()))
                }
            }
        }
    }) {}
}

@Composable
private fun TarotReadingText(reading: FortuneReading, aiConfigured: Boolean, recordAvailable: Boolean, busy: Boolean,
    onExplain: () -> Unit, onNew: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val c = LocalTarotAtelier.current
    var showCards by rememberSaveable(reading.id) { mutableStateOf(false) }
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("牌阵已展开", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        if (reading.question.isNotBlank()) Text(reading.question, style = MaterialTheme.typography.bodyLarge)
        TextButton(onClick = hapticAction(if (showCards) HapticCue.MenuClose else HapticCue.MenuOpen) { showCards = !showCards }, contentPadding = PaddingValues(0.dp)) {
            Text(if (showCards) "收起逐牌牌义" else "查看四张牌的牌义", color = c.accent)
        }
        FortuneAnimatedVisibility(showCards) { FourCardReadingSummary(reading) }
        HorizontalDivider(color = c.line.copy(.5f))
        Text("本地解读", style = MaterialTheme.typography.titleMedium)
        SelectionContainer { Text(reading.body, style = MaterialTheme.typography.bodyLarge) }
        if (reading.aiInterpretation.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FortuneIcon(Icons.Default.AutoAwesome, null, tint = colors.primary, modifier = Modifier.size(20.dp))
                Text("AI 解读", style = MaterialTheme.typography.titleMedium)
            }
            SelectionContainer { Text(reading.aiInterpretation, style = MaterialTheme.typography.bodyLarge) }
        } else {
            Button(onClick = onExplain, enabled = aiConfigured && recordAvailable && !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                if (busy) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else FortuneIcon(Icons.Default.AutoAwesome, null)
                Spacer(Modifier.width(8.dp))
                Text(if (busy) "正在解读" else if (reading.aiStatus.startsWith("AI 解读失败")) "重试 AI 解读" else "AI 深入解读")
            }
            Text(if (!recordAvailable) "本次记录已删除，可开启新的抽牌。" else if (!aiConfigured) "请配置 AI 模型获得完整功能" else
                "仅在你点击后，将问题、四张牌与昵称、关注词发送至所配置的 AI 平台。",
                style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            if (reading.aiStatus.startsWith("AI 解读失败")) Text(reading.aiStatus,
                style = MaterialTheme.typography.bodySmall, color = colors.error)
        }
        TextButton(onClick = onNew, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            FortuneIcon(Icons.Default.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text("开启新的抽牌")
        }
    }
}

@Composable
internal fun FourCardReadingSummary(reading: FortuneReading) {
    if (runCatching { FourCardTarot.validate(reading.tarotDraws) }.isFailure) {
        Text("牌位资料不完整，请以已保存的解读文字为准。", style = MaterialTheme.typography.bodyMedium)
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        reading.tarotDraws.sortedBy { it.positionIndex }.forEach { draw ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                val image = rememberTarotArtwork(draw.card.imageRes)
                if (image != null) Image(image, "${draw.card.nameZh}${draw.orientationLabel}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(48.dp).aspectRatio(.58f).clip(CardShape)
                        .graphicsLayer { rotationZ = if (draw.reversed) 180f else 0f })
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${FourCardTarot.position(draw).name} · ${draw.card.nameZh} ${draw.orientationLabel}",
                        style = MaterialTheme.typography.titleSmall)
                    Text(draw.keywords.joinToString(" · "), style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Text(draw.meaning, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarotFourDetailSheet(draw: TarotDraw, onDismiss: () -> Unit) {
    val c = tarotAtelierPalette(LocalFortunePalette.current.isLight)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shortWide = LocalFortuneLayout.current.shortWide
    val compactHeight = (LocalConfiguration.current.screenHeightDp.dp - 104.dp).coerceAtLeast(120.dp)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState,
        containerColor = c.control, contentColor = c.ink) {
        if (shortWide) {
            Row(Modifier.fillMaxWidth().heightIn(max = compactHeight).padding(horizontal = 24.dp).padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                val art = rememberTarotArtwork(draw.card.imageRes)
                if (art != null) Image(art, draw.card.nameZh, contentScale = ContentScale.Fit,
                    modifier = Modifier.width(94.dp).fillMaxHeight()
                        .graphicsLayer { rotationZ = if (draw.reversed) 180f else 0f })
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("${FourCardTarot.position(draw).name} · ${draw.card.nameZh}", style = MaterialTheme.typography.titleLarge)
                    Text("${draw.orientationLabel} · ${draw.card.classificationLabel}", style = MaterialTheme.typography.labelLarge)
                    SelectionContainer { Text(FourCardTarot.positionReading(draw), style = MaterialTheme.typography.bodyLarge) }
                    Text("牌面：${draw.card.imageDescription}", style = MaterialTheme.typography.bodyMedium,
                        color = c.muted)
                }
            }
        } else {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${FourCardTarot.position(draw).name} · ${draw.card.nameZh}", style = MaterialTheme.typography.titleLarge)
            val art = rememberTarotArtwork(draw.card.imageRes)
            if (art != null) Image(art, draw.card.nameZh, contentScale = ContentScale.Fit,
                modifier = Modifier.heightIn(max = 300.dp).width(170.dp).aspectRatio(.58f)
                    .graphicsLayer { rotationZ = if (draw.reversed) 180f else 0f })
            Text("${draw.orientationLabel} · ${draw.card.classificationLabel}", style = MaterialTheme.typography.labelLarge)
            SelectionContainer { Text(FourCardTarot.positionReading(draw), style = MaterialTheme.typography.bodyLarge) }
            Text("牌面：${draw.card.imageDescription}", style = MaterialTheme.typography.bodyMedium,
                color = c.muted)
        }
        }
    }
}

@Composable
private fun TarotSourcesDialog(onDismiss: () -> Unit) {
    val uri = LocalUriHandler.current
    val c = tarotAtelierPalette(LocalFortunePalette.current.isLight)
    AlertDialog(onDismissRequest = onDismiss, containerColor = c.control,
        titleContentColor = c.ink, textContentColor = c.ink, title = { Text("牌阵与来源") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(FourCardTarot.METHOD_NOTE)
                Text("从完整 78 张韦特牌中无放回抽取。洗牌时固定顺序与正逆位；逆位可在开始前关闭。所选位置按抽取先后确定，翻牌动画不会改变结果。")
                Text("正面沿用已收录的历史版韦特图像，牌背为应用原创设计。双列或横排仅改变展示排版，不改变编号牌位的含义。牌义参考韦特原著，中文概括与现实应用为本应用编写，并非逐字翻译。")
                Text("塔罗没有经科学验证的预测效力。本玩法采用清楚、一致、可复核的牌阵规则，供文化体验与自省，不替代专业意见或现实判断。")
                TextButton(onClick = { uri.openUri(FourCardTarot.METHOD_URL) }) { Text("四牌十字布局参考") }
                TextButton(onClick = { uri.openUri(FourCardTarot.WAITE_URL) }) { Text("韦特原著与牌义背景") }
            }
        }, confirmButton = { TextButton(onClick = onDismiss) { Text("完成") } })
}

@Composable
internal fun FourCardTarotToolArtwork(modifier: Modifier = Modifier) {
    val c = tarotAtelierPalette(LocalFortunePalette.current.isLight)
    BoxWithConstraints(modifier.background(c.background), contentAlignment = Alignment.Center) {
        val w = minOf(maxWidth * .24f, maxHeight * .40f)
        listOf(R.drawable.tarot_major_17, R.drawable.tarot_major_02, null, R.drawable.tarot_major_19)
            .forEachIndexed { i, res ->
                val art = res?.let { rememberTarotArtwork(it) }
                Box(Modifier.offset(x = w * (i - 1.5f) * .67f, y = if (i == 0 || i == 3) 12.dp else 0.dp)
                    .width(w).aspectRatio(.58f).graphicsLayer {
                        rotationZ = (i - 1.5f) * 10f
                        shadowElevation = 8.dp.toPx()
                        shape = CardShape
                    }.clip(CardShape).border(.7.dp, c.metal.copy(.65f), CardShape)) {
                    if (res == null) TarotAtelierBack({ .25f }, { Offset.Zero }, Modifier.fillMaxSize())
                    else if (art != null) Image(art, null, contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize())
                }
            }
    }
}
