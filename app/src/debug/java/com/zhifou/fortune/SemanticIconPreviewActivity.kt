package com.zhifou.fortune

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt

/** Debug-only raster/GPU comparisons. No repository, account data, permissions, or API clients. */
class SemanticIconPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preview = if (intent.hasExtra("phase")) intent.getFloatExtra("phase", 1f) else null
        setContent { MaterialTheme { IconRasterPreview(window, preview) } }
    }
}

private fun iconExamples(): List<ImageVector> = listOf(
    Icons.Filled.Home, Icons.Outlined.Home, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth,
    Icons.Filled.DateRange, Icons.Filled.Today, Icons.Filled.AutoAwesome, Icons.Filled.GridView, Icons.Outlined.GridView,
    Icons.Filled.PersonOutline, Icons.Outlined.PersonOutline, Icons.Filled.Person, Icons.Filled.AccountCircle,
    Icons.Filled.Book, Icons.AutoMirrored.Filled.MenuBook, Icons.Filled.AutoStories,
    Icons.Filled.Toll, Icons.Filled.Paid, Icons.Filled.Payments, Icons.Filled.Style, Icons.Filled.Casino,
    Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, Icons.Filled.WorkOutline, Icons.Filled.Work,
    Icons.Filled.FavoriteBorder, Icons.Filled.Favorite, Icons.Filled.Settings, Icons.Filled.Schedule, Icons.Filled.AccessTime,
    Icons.Filled.Search, Icons.Filled.Edit, Icons.Filled.Mic, Icons.Filled.Vibration, Icons.Filled.Wallpaper,
    Icons.Filled.Image, Icons.Filled.AddPhotoAlternate, Icons.Filled.Shield, Icons.Filled.Check, Icons.Filled.CheckCircle,
    Icons.Filled.Add, Icons.AutoMirrored.Filled.ArrowBack, Icons.AutoMirrored.Filled.KeyboardArrowLeft,
    Icons.AutoMirrored.Filled.KeyboardArrowRight, Icons.Filled.ChevronRight, Icons.Filled.Refresh,
    Icons.AutoMirrored.Filled.Send,
)

@Composable
private fun IconRasterPreview(window: Window, preview: Float?) {
    val examples = remember { iconExamples() }
    val sources = examples.map { rememberVectorPainter(it) }
    val frozen = remember { mutableFloatStateOf(preview ?: .8199f) }
    var batch by remember { mutableIntStateOf(0) }
    var status by remember { mutableStateOf("正在逐像素检查图标") }
    var playing by remember { mutableStateOf(false) }
    var replay by remember { mutableIntStateOf(0) }
    val bounds = remember { mutableMapOf<String, Rect>() }
    val perBatch = 12

    LaunchedEffect(Unit) {
        if (preview != null) {
            status = "Frozen preview · original on the left, animated on the right"
            return@LaunchedEffect
        }
        var checks = 0
        var failures = 0
        val details = mutableListOf<String>()
        fun report(label: String, different: Int) {
            checks++
            if (different > 0) {
                failures++
                if (details.size < 5) details += "$label: $different pixels"
            }
        }
        try {
            val samples = listOf(0f, .001f, .79f, .8199f, .82f, .999f, 1f)
            val dimensions = listOf(20 to 20, 24 to 24, 32 to 32, 53 to 53, 70 to 70, 53 to 37)
            val tints = listOf(Color(0xFF216451), Color(0xFFB7CCD9).copy(alpha = .42f))
            examples.forEachIndexed { index, image ->
                val phase = mutableFloatStateOf(1f)
                LayoutDirection.entries.forEach { direction ->
                    val animated = SemanticIconPainter(sources[index], semanticIconKind(image.name), phase,
                        image.name, image.autoMirror && direction == LayoutDirection.Rtl)
                    dimensions.forEach { (width, height) -> tints.forEach { tint ->
                        val expected = renderIconPixels(sources[index], width, height, direction, tint)
                        check(expected.any { it != 0 }) { "Empty source vector: ${image.name}" }
                        samples.forEach { sample ->
                            phase.floatValue = sample
                            val actual = renderIconPixels(animated, width, height, direction, tint)
                            report("${image.name}/$width/$height/$direction/$sample", differentPixels(expected, actual))
                        }
                    } }
                }
                withFrameNanos { }
            }

            for (sample in listOf(.001f, .8199f, .999f)) {
                frozen.floatValue = sample
                repeat((examples.size + perBatch - 1) / perBatch) { page ->
                    batch = page
                    withFrameNanos { }
                    withFrameNanos { }
                    delay(100)
                    val bitmap = copyWindow(window)
                    try {
                        repeat(minOf(perBatch, examples.size - page * perBatch)) { slot ->
                            val left = checkNotNull(bounds["left-$slot"])
                            val right = checkNotNull(bounds["right-$slot"])
                            check(left.width() == right.width() && left.height() == right.height()) { "Icon layout sizes differ" }
                            report("GPU/${examples[page * perBatch + slot].name}/$sample",
                                differentPixels(cropPixels(bitmap, left), cropPixels(bitmap, right)))
                        }
                    } finally { bitmap.recycle() }
                }
            }
            status = if (failures == 0) "PASS · ${examples.size} icons · $checks comparisons · 0 differing pixels"
            else "FAIL · $failures/$checks comparisons · ${details.joinToString()}"
        } catch (error: Exception) {
            status = "FAIL · ${error.javaClass.simpleName}: ${error.message}"
        }
        Log.i("ZhifouIconRaster", status)
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF4F6F5)).safeDrawingPadding().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Canonical icon comparison", style = MaterialTheme.typography.titleMedium)
        Text(status, Modifier.height(76.dp).semantics { contentDescription = status },
            color = if (status.startsWith("FAIL")) Color.Red else Color(0xFF216451),
            style = MaterialTheme.typography.bodySmall)
        val visible = examples.drop(batch * perBatch).take(perBatch)
        visible.chunked(3).forEachIndexed { row, group ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                group.forEachIndexed { column, image ->
                    val slot = row * 3 + column
                    val index = batch * perBatch + slot
                    Column(Modifier.weight(1f).height(78.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(image.name.substringAfterLast('.'), style = MaterialTheme.typography.labelSmall,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(sources[index], null, Modifier.size(24.dp).captureIconBounds(bounds, "left-$slot"), tint = Color(0xFF216451))
                            if (playing) FortuneIcon(image, null, Modifier.size(24.dp), tint = Color(0xFF216451), replayKey = replay)
                            else {
                                val painter = rememberSemanticIconPainter(image, frozen)
                                Icon(painter, null, Modifier.size(24.dp).captureIconBounds(bounds, "right-$slot"), tint = Color(0xFF216451))
                            }
                        }
                    }
                }
                repeat(3 - group.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { playing = true; replay++ }) { Text("播放动效") }
            TextButton(onClick = { batch = (batch + 1) % ((examples.size + perBatch - 1) / perBatch) }) { Text("下一组") }
        }
    }
}

private fun Modifier.captureIconBounds(bounds: MutableMap<String, Rect>, key: String) = onGloballyPositioned { layout ->
    val rect = layout.boundsInWindow()
    bounds[key] = Rect(rect.left.roundToInt(), rect.top.roundToInt(), rect.right.roundToInt(), rect.bottom.roundToInt())
}

private fun renderIconPixels(painter: Painter, width: Int, height: Int, direction: LayoutDirection, tint: Color): IntArray {
    val image = ImageBitmap(width, height)
    CanvasDrawScope().draw(Density(1f), direction, Canvas(image), Size(width.toFloat(), height.toFloat())) {
        with(painter) { draw(size, colorFilter = ColorFilter.tint(tint)) }
    }
    return cropPixels(image.asAndroidBitmap(), Rect(0, 0, width, height)).also { image.asAndroidBitmap().recycle() }
}

private fun cropPixels(bitmap: Bitmap, bounds: Rect): IntArray = IntArray(bounds.width() * bounds.height()).also {
    bitmap.getPixels(it, 0, bounds.width(), bounds.left, bounds.top, bounds.width(), bounds.height())
}

private fun differentPixels(expected: IntArray, actual: IntArray): Int =
    expected.indices.count { expected[it] != actual[it] }

private suspend fun copyWindow(window: Window): Bitmap = suspendCancellableCoroutine { continuation ->
    val view = window.decorView
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    PixelCopy.request(window, bitmap, { result ->
        if (!continuation.isActive) bitmap.recycle()
        else if (result == PixelCopy.SUCCESS) continuation.resume(bitmap)
        else {
            bitmap.recycle()
            continuation.resumeWithException(IllegalStateException("PixelCopy returned $result"))
        }
    }, Handler(Looper.getMainLooper()))
}
