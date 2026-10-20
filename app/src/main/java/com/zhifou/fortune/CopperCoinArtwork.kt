package com.zhifou.fortune

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private data class CopperCoinFaces(val yang: Bitmap, val yin: Bitmap)

private object CopperCoinArtCache {
    @Volatile private var faces: CopperCoinFaces? = null
    fun peek() = faces

    @Synchronized
    fun load(resources: Resources): CopperCoinFaces {
        faces?.let { return it }
        fun decode(id: Int) = checkNotNull(BitmapFactory.decodeResource(resources, id,
            BitmapFactory.Options().apply { inSampleSize = 2; inPreferredConfig = Bitmap.Config.ARGB_8888 }))
        return CopperCoinFaces(decode(R.drawable.oracle_coin_yang), decode(R.drawable.oracle_coin_yin))
            .also { faces = it }
    }
}

private class CopperCoinDrawing {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    val matrix = Matrix()
    val source = FloatArray(8)
    val face = FloatArray(8)
    val wall = FloatArray(8)
    val path = Path()
    val circle = FloatArray(130).apply {
        for (index in 0..64) {
            val angle = index * 2.0 * PI / 64
            this[index * 2] = cos(angle).toFloat()
            this[index * 2 + 1] = sin(angle).toFloat()
        }
    }

    fun wallPath(): Path = path.apply {
        reset()
        moveTo(wall[0], wall[1])
        lineTo(wall[2], wall[3])
        lineTo(wall[4], wall[5])
        lineTo(wall[6], wall[7])
        close()
    }
}

internal fun DrawScope.drawCopperCoinShadow(pose: CopperCoinShadowPose) {
    val density = density
    val ground = Offset(size.width / 2f, size.height - 14f * density)
    fun ellipse(center: Offset, radiusX: Float, radiusY: Float, alpha: Float) {
        val radius = radiusX * density
        scale(1f, radiusY / radiusX, pivot = center) {
            drawCircle(Brush.radialGradient(
                0f to Color.Black.copy(alpha = alpha),
                .24f to Color.Black.copy(alpha = alpha * .87f),
                .52f to Color.Black.copy(alpha = alpha * .46f),
                .78f to Color.Black.copy(alpha = alpha * .12f),
                1f to Color.Transparent,
                center = center, radius = radius,
            ), radius, center)
        }
    }
    ellipse(ground + Offset(pose.offsetX * density, pose.offsetY * density),
        pose.radiusX, pose.radiusY, pose.opacity)
    ellipse(ground + Offset(pose.contactX * density, 0f), pose.contactRadiusX, 1.15f, pose.contactOpacity)
}

/** Two model-rendered faces plus projected outer/inner walls; no per-frame bitmap work. */
@Composable
internal fun CopperCoinArtwork(yang: Boolean, rotationY: () -> Float, modifier: Modifier = Modifier) {
    val resources = LocalResources.current
    val art by produceState(CopperCoinArtCache.peek(), resources) {
        value = withContext(Dispatchers.IO) { CopperCoinArtCache.load(resources) }
    }
    val drawing = remember { CopperCoinDrawing() }
    Canvas(modifier.semantics { contentDescription = if (yang) "铜钱阳面：日纹" else "铜钱阴面：月纹" }) {
        val diameter = size.minDimension
        val angle = (rotationY() % 360f) * PI / 180.0
        val sine = sin(angle).toFloat()
        val cosine = cos(angle).toFloat()
        val half = CopperCoinProjection.halfThickness
        fun project(x: Float, y: Float, z: Float, index: Int) = CopperCoinProjection.writePoint(
            x, y, z, sine, cosine, diameter, drawing.wall, index)
        // The near outer wall remains visible when a face turns edge-on.
        for (index in 0 until 64) {
            val x = drawing.circle[index * 2]
            val y = drawing.circle[index * 2 + 1]
            val nextX = drawing.circle[index * 2 + 2]
            val nextY = drawing.circle[index * 2 + 3]
            val facing = -sine * (x + nextX) / 2f
            if (facing <= 0f) continue
            project(x, y, -half, 0)
            project(nextX, nextY, -half, 2)
            project(nextX, nextY, half, 4)
            project(x, y, half, 6)
            val light = (.32f + .42f * max(0f, -x * .55f - y * .65f) + .20f * facing).coerceIn(0f, 1f)
            drawPath(drawing.wallPath(), Color(.40f + .48f * light, .24f + .43f * light, .10f + .28f * light))
        }
        // The square shaft has real depth, rather than an opaque black square decal.
        val hole = CopperCoinProjection.holeHalf
        val nearX = if (sine > 0f) hole else -hole
        project(nearX, -hole, -half, 0)
        project(nearX, hole, -half, 2)
        project(nearX, hole, half, 4)
        project(nearX, -hole, half, 6)
        drawPath(drawing.wallPath(), Color(0xFF98632B))
        val bitmap = art?.let { if (yang) it.yang else it.yin }
        if (bitmap == null) {
            val radius = diameter / CopperCoinProjection.artworkSpan
            val center = Offset(diameter / 2f, diameter / 2f)
            drawing.path.reset()
            drawing.path.fillType = PathFillType.EvenOdd
            drawing.path.addOval(Rect(center - Offset(radius, radius), center + Offset(radius, radius)))
            val opening = radius * hole
            drawing.path.addRect(Rect(center - Offset(opening, opening), center + Offset(opening, opening)))
            drawPath(drawing.path, Brush.linearGradient(if (yang)
                listOf(Color(0xFFF3CC77), Color(0xFFB17A32)) else listOf(Color(0xFF78988A), Color(0xFF365E54))))
            drawing.path.fillType = PathFillType.NonZero
        } else if (kotlin.math.abs(cosine) > .001f) {
            CopperCoinProjection.writeFace(yang, sine, cosine, diameter, drawing.face)
            val width = bitmap.width.toFloat()
            val height = bitmap.height.toFloat()
            with(drawing.source) {
                this[0] = 0f; this[1] = 0f; this[2] = width; this[3] = 0f
                this[4] = width; this[5] = height; this[6] = 0f; this[7] = height
            }
            drawing.matrix.setPolyToPoly(drawing.source, 0, drawing.face, 0, 4)
            drawIntoCanvas { it.nativeCanvas.drawBitmap(bitmap, drawing.matrix, drawing.paint) }
        }
    }
}
