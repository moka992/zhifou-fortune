package com.zhifou.fortune

import android.graphics.BitmapFactory
import android.os.SystemClock
import android.util.LruCache
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

internal suspend fun awaitOracleMoment(startedAt: Long, atMs: Long) {
    val remaining = startedAt + atMs - SystemClock.uptimeMillis()
    if (remaining > 0L) delay(remaining)
}

@Composable
internal fun OracleMotionFeedback(startedAt: Long, kind: OracleMotionKind) {
    val haptics = LocalAppHaptics.current
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(startedAt, kind, owner) {
        if (startedAt <= 0L) return@LaunchedEffect
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            try {
                for (cue in OracleMotion.cues(kind)) {
                    val target = startedAt + cue.atMs
                    val remaining = target - SystemClock.uptimeMillis()
                    if (remaining < -80L) continue
                    if (remaining > 0L) delay(remaining)
                    // Never replay a backlog after a stall or a return from background.
                    if (SystemClock.uptimeMillis() - target <= 80L) haptics.oracleTouch(cue)
                }
            } finally {
                haptics.cancel()
            }
        }
    }
}

@Composable
internal fun rememberOracleElapsed(startedAt: Long, kind: OracleMotionKind): State<Float> {
    val owner = LocalLifecycleOwner.current
    val elapsed = remember(startedAt) {
        mutableFloatStateOf((SystemClock.uptimeMillis() - startedAt).coerceAtLeast(0L).toFloat())
    }
    LaunchedEffect(startedAt, owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            do {
                withFrameNanos {
                    elapsed.floatValue = (SystemClock.uptimeMillis() - startedAt).coerceAtLeast(0L).toFloat()
                }
            } while (elapsed.floatValue < OracleMotion.duration(kind))
        }
    }
    return elapsed
}

@Composable
private fun rememberReducedOracleMotion(): Boolean {
    return LocalReducedFortuneMotion.current
}

@Composable
internal fun CopperCoinTossStage(line: CoinLineResult?, previous: CoinLineResult?, elapsed: State<Float>) {
    val density = LocalDensity.current.density
    val reduced = rememberReducedOracleMotion()
    val round = (line?.position ?: 1) - 1
    val compact = LocalFortuneLayout.current.shortWide
    val stageHeight = if (compact) 92.dp else 112.dp
    val liftScale = if (compact) .76f else 1f
    Box(Modifier.fillMaxWidth().height(stageHeight), contentAlignment = Alignment.BottomCenter) {
        Row(Modifier.fillMaxWidth().height(stageHeight), horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom) {
            repeat(3) { index ->
                val value = line?.coinValues?.getOrNull(index) ?: 3
                val previousValue = previous?.coinValues?.getOrNull(index) ?: 3
                val face by remember(line, previous, reduced) {
                    derivedStateOf {
                        if (reduced) value == 3 else OracleMotion.coinPose(
                            elapsed.value - OracleMotion.coinRoundStart(round), index, value, previousValue).frontIsYang
                    }
                }
                Box(Modifier.size(width = 62.dp, height = if (compact) 86.dp else 98.dp), contentAlignment = Alignment.BottomCenter) {
                    Canvas(Modifier.fillMaxSize()) {
                        val pose = OracleMotion.coinPose(elapsed.value - OracleMotion.coinRoundStart(round), index, value, previousValue)
                        drawCopperCoinShadow(copperCoinShadow(pose.copy(liftDp = pose.liftDp * liftScale), reduced))
                    }
                    CopperCoinArtwork(
                        yang = face,
                        rotationY = {
                            if (reduced) { if (value == 3) 0f else 180f }
                            else OracleMotion.coinPose(elapsed.value - OracleMotion.coinRoundStart(round), index, value, previousValue).rotationY
                        },
                        modifier = Modifier.size(if (compact) 42.dp else 46.dp).graphicsLayer {
                            val pose = OracleMotion.coinPose(elapsed.value - OracleMotion.coinRoundStart(round), index, value, previousValue)
                            translationY = -14f * density - if (reduced) 0f else pose.liftDp * liftScale * density
                            translationX = if (reduced) 0f else pose.driftDp * density
                            rotationZ = if (reduced) 0f else pose.rotationZ
                        },
                    )
                }
            }
        }
    }
}

private object TarotAnimationArt {
    private val cache = object : LruCache<Int, ImageBitmap>(8 * 1024 * 1024) {
        override fun sizeOf(key: Int, value: ImageBitmap) = value.width * value.height * 4
    }
    fun peek(id: Int): ImageBitmap? = cache.get(id)
    @Synchronized
    fun load(resources: android.content.res.Resources, id: Int): ImageBitmap? {
        cache.get(id)?.let { return it }
        return BitmapFactory.decodeResource(resources, id, BitmapFactory.Options().apply { inSampleSize = 2 })
            ?.asImageBitmap()?.also { cache.put(id, it) }
    }
}

@Composable
internal fun rememberTarotArtwork(id: Int): ImageBitmap? {
    val resources = LocalResources.current
    val bitmap by produceState(TarotAnimationArt.peek(id), id, resources) {
        value = withContext(Dispatchers.IO) { TarotAnimationArt.load(resources, id) }
    }
    return bitmap
}

@Composable
internal fun TarotResultArtwork(draw: TarotDraw, modifier: Modifier) {
    val art = rememberTarotArtwork(draw.card.imageRes)
    if (art != null) Image(art, "${draw.position.name}：${draw.card.nameZh}${draw.orientationLabel}",
        contentScale = ContentScale.Fit, modifier = modifier.graphicsLayer { rotationZ = if (draw.reversed) 180f else 0f })
    else Box(modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh))
}

@Composable
internal fun TarotAnimatedCard(draw: TarotDraw, elapsed: State<Float>, modifier: Modifier) {
    val density = LocalDensity.current.density
    val reduced = rememberReducedOracleMotion()
    val index = draw.positionIndex - 1
    val front by remember(elapsed, index, reduced) {
        derivedStateOf { OracleMotion.cardPose(elapsed.value, index, reduced).rotationY > 90f }
    }
    val face = rememberTarotArtwork(draw.card.imageRes)
    val back = rememberTarotArtwork(R.drawable.tarot_card_back)
    val edge = MaterialTheme.colorScheme.outlineVariant
    Box(modifier.border(1.dp, edge.copy(alpha = .35f), RoundedCornerShape(5.dp))) {
        Box(Modifier.fillMaxSize().graphicsLayer {
            val pose = OracleMotion.cardPose(elapsed.value, index, reduced)
            alpha = pose.alpha
            translationX = (2 - index % 5) * (size.width + 6f * density) * (1f - pose.deal)
            translationY = ((if (index < 5) -22f * density else -size.height - 28f * density) * (1f - pose.deal)) - pose.liftDp * density
            rotationZ = (index % 5 - 2) * 5f * (1f - pose.deal)
            rotationY = pose.rotationY
            cameraDistance = 18f * density
            scaleX = .86f + pose.deal * .14f
            scaleY = scaleX
            shadowElevation = (2f + pose.liftDp * .45f) * density
            shape = RoundedCornerShape(5.dp)
            clip = true
        }.background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .drawWithContent {
                drawContent()
                val pose = OracleMotion.cardPose(elapsed.value, index, reduced)
                val shine = sin(pose.rotationY * PI.toFloat() / 180f).coerceAtLeast(0f)
                if (shine > .01f) {
                    val x = (pose.rotationY / 180f) * size.width * 1.8f - size.width * .4f
                    drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = .23f * shine), Color.Transparent),
                        start = Offset(x - size.width * .3f, 0f), end = Offset(x + size.width * .3f, size.height)))
                }
            }) {
            val art = if (front) face else back
            if (art != null) Image(art,
                contentDescription = if (front) "${draw.position.name}：${draw.card.nameZh}${draw.orientationLabel}" else "未翻开的塔罗牌",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().graphicsLayer {
                    rotationY = if (front) 180f else 0f
                    rotationZ = if (front && draw.reversed) 180f else 0f
                })
        }
    }
}

@Composable
internal fun TarotShuffleStack(elapsed: State<Float>, modifier: Modifier = Modifier) {
    val back = rememberTarotArtwork(R.drawable.tarot_card_back)
    val reduced = rememberReducedOracleMotion()
    val density = LocalDensity.current.density
    Box(modifier.size(width = 36.dp, height = 50.dp), contentAlignment = Alignment.Center) {
        repeat(3) { layer ->
            if (back != null) Image(back, contentDescription = null, contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().graphicsLayer {
                    val progress = (elapsed.value / OracleMotion.shuffleMs).coerceIn(0f, 1f)
                    val fan = if (reduced) 0f else sin(progress * PI.toFloat())
                    rotationZ = (layer - 1) * (5f + 19f * fan)
                    translationX = (layer - 1) * (2f + 9f * fan) * density
                    translationY = abs(layer - 1) * 2f * density
                    shadowElevation = 2f * density
                    shape = RoundedCornerShape(3.dp)
                    clip = true
                })
        }
    }
}
