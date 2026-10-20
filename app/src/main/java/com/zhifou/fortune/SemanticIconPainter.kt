package com.zhifou.fortune

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

@Composable
internal fun rememberSemanticIconPainter(image: ImageVector, phase: State<Float>): Painter {
    val source = rememberVectorPainter(image)
    val mirrored = image.autoMirror && LocalLayoutDirection.current == LayoutDirection.Rtl
    return remember(source, phase, mirrored, image) {
        SemanticIconPainter(source, semanticIconKind(image.name), phase, image.name, mirrored)
    }
}

/** The original VectorPainter draws every frame, including both sides of the moving/resting boundary. */
internal class SemanticIconPainter(
    private val source: Painter,
    private val kind: SemanticIconKind,
    private val phase: State<Float>,
    private val name: String,
    private val mirrored: Boolean,
) : Painter() {
    private val regions = semanticIconRegions(kind, name)
    private val edges = FloatArray(regions.size * 4)
    private var preparedSize = Size.Unspecified
    private var iconAlpha = 1f
    private var iconFilter: ColorFilter? = null

    override val intrinsicSize: Size get() = source.intrinsicSize

    override fun applyAlpha(alpha: Float): Boolean { iconAlpha = alpha; return true }
    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean { iconFilter = colorFilter; return true }

    override fun DrawScope.onDraw() {
        val progress = phase.value
        if (!progress.isFinite() || progress <= 0f || progress >= ICON_PART_REST_START || regions.isEmpty()) {
            original()
            return
        }
        if (preparedSize != size) prepareEdges(size)
        val unitX = size.width / 24f
        val unitY = size.height / 24f

        withTransform({
            regions.indices.forEach { index ->
                val edge = index * 4
                clipRect(edges[edge], edges[edge + 1], edges[edge + 2], edges[edge + 3], ClipOp.Difference)
            }
        }) { original() }

        regions.indices.forEach { index ->
            val pose = semanticIconPose(kind, index, progress, name)
            val edge = index * 4
            val direction = if (mirrored) -1f else 1f
            val pivot = Offset((if (mirrored) 24f - pose.pivotX else pose.pivotX) * unitX, pose.pivotY * unitY)
            withTransform({
                translate(pose.x * unitX * direction, pose.y * unitY)
                rotate(pose.rotation * direction, pivot)
                scale(pose.scaleX, pose.scaleY, pivot)
            }) {
                clipRect(edges[edge], edges[edge + 1], edges[edge + 2], edges[edge + 3]) {
                    original(pose.opacity)
                }
            }
        }
    }

    private fun DrawScope.original(opacity: Float = 1f) {
        with(source) { draw(size, iconAlpha * opacity, iconFilter) }
    }

    private fun prepareEdges(size: Size) {
        regions.forEachIndexed { index, region ->
            val edge = index * 4
            // Integer pixel boundaries make the masks an exact partition, without antialias seams.
            edges[edge] = ((if (mirrored) 24f - region.right else region.left) * size.width / 24f).roundToInt().toFloat()
            edges[edge + 1] = (region.top * size.height / 24f).roundToInt().toFloat()
            edges[edge + 2] = ((if (mirrored) 24f - region.left else region.right) * size.width / 24f).roundToInt().toFloat()
            edges[edge + 3] = (region.bottom * size.height / 24f).roundToInt().toFloat()
        }
        preparedSize = size
    }
}
