package com.zhifou.fortune

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.effect
import kotlin.math.abs
import kotlin.math.min

private const val MAX_GLASS_SURFACES = 12

/** The live GPU background that the single scene compositor samples. */
internal val LocalGlassBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/** Geometry registry shared by every glass surface in the current window. */
internal val LocalGlassScene = staticCompositionLocalOf<GlassSceneState?> { null }

@ChecksSdkIntAtLeast(Build.VERSION_CODES.TIRAMISU)
internal fun supportsBackdropRefraction(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@Composable
internal fun rememberGlassBackdropState(): LayerBackdrop = rememberLayerBackdrop()

@Composable
internal fun rememberGlassSceneState(): GlassSceneState = remember { GlassSceneState() }

/** Records the source into a reusable GPU layer without copying it into a bitmap. */
internal fun Modifier.captureGlassBackdrop(state: LayerBackdrop): Modifier = this.layerBackdrop(state)

private class GlassSurface {
    var left = 0f
    var top = 0f
    var width = 0f
    var height = 0f
    var topLeftRadius = 0f
    var topRightRadius = 0f
    var bottomRightRadius = 0f
    var bottomLeftRadius = 0f
    var opticalDepth = 0f
    var refractionAmount = 0f
    var tint = Color.Transparent
    var tintAlpha = 0f
    var lightMode = 0f
    var shape: Shape? = null
    var density = 0f
    var layoutDirection: androidx.compose.ui.unit.LayoutDirection? = null
}

@Stable
internal class GlassSceneState {
    private val surfaces = LinkedHashMap<Any, GlassSurface>()
    private var revision by mutableIntStateOf(0)

    private var viewportLeft by mutableFloatStateOf(0f)
    private var viewportTop by mutableFloatStateOf(0f)
    private var viewportWidth by mutableFloatStateOf(1f)
    private var viewportHeight by mutableFloatStateOf(1f)

    private val rectUniforms = FloatArray(MAX_GLASS_SURFACES * 4)
    private val radiusUniforms = FloatArray(MAX_GLASS_SURFACES * 4)
    private val styleUniforms = FloatArray(MAX_GLASS_SURFACES * 4)
    private val tintUniforms = FloatArray(MAX_GLASS_SURFACES * 4)

    internal fun updateViewport(position: Offset, size: IntSize) {
        val width = size.width.toFloat().coerceAtLeast(1f)
        val height = size.height.toFloat().coerceAtLeast(1f)
        var changed = false
        if (abs(viewportLeft - position.x) > 0.25f) {
            viewportLeft = position.x
            changed = true
        }
        if (abs(viewportTop - position.y) > 0.25f) {
            viewportTop = position.y
            changed = true
        }
        if (abs(viewportWidth - width) > 0.25f) {
            viewportWidth = width
            changed = true
        }
        if (abs(viewportHeight - height) > 0.25f) {
            viewportHeight = height
            changed = true
        }
        if (changed) revision++
    }

    internal fun removeSurface(key: Any) {
        if (surfaces.remove(key) != null) revision++
    }

    internal fun registerSurface(
        key: Any,
        position: Offset,
        size: IntSize,
        shape: Shape,
        tint: Color,
        isLight: Boolean,
        density: Density,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
    ) {
        if (
            size.width <= 0 || size.height <= 0 ||
            !position.x.isFinite() || !position.y.isFinite()
        ) {
            removeSurface(key)
            return
        }

        val width = size.width.toFloat()
        val height = size.height.toFloat()
        val surface = surfaces[key] ?: GlassSurface().also { surfaces[key] = it }
        val appearanceChanged =
            surface.width != width || surface.height != height || surface.shape != shape ||
                surface.tint != tint || surface.lightMode != (if (isLight) 1f else 0f) ||
                surface.density != density.density || surface.layoutDirection != layoutDirection
        val positionChanged =
            abs(surface.left - position.x) > 0.1f || abs(surface.top - position.y) > 0.1f
        if (!appearanceChanged && !positionChanged) return

        surface.left = position.x
        surface.top = position.y
        if (appearanceChanged) {
            surface.width = width
            surface.height = height
            surface.shape = shape
            surface.tint = tint
            surface.tintAlpha = if (isLight) 0.040f else 0.032f
            surface.lightMode = if (isLight) 1f else 0f
            surface.density = density.density
            surface.layoutDirection = layoutDirection

            val floatSize = Size(width, height)
            val outline = shape.createOutline(floatSize, layoutDirection, density)
            val fallbackRadius = floatSize.minDimension * 0.18f
            when (outline) {
                is Outline.Rounded -> {
                    surface.topLeftRadius = outline.roundRect.topLeftCornerRadius.x
                    surface.topRightRadius = outline.roundRect.topRightCornerRadius.x
                    surface.bottomRightRadius = outline.roundRect.bottomRightCornerRadius.x
                    surface.bottomLeftRadius = outline.roundRect.bottomLeftCornerRadius.x
                }
                is Outline.Rectangle -> {
                    surface.topLeftRadius = 0f
                    surface.topRightRadius = 0f
                    surface.bottomRightRadius = 0f
                    surface.bottomLeftRadius = 0f
                }
                is Outline.Generic -> {
                    surface.topLeftRadius = fallbackRadius
                    surface.topRightRadius = fallbackRadius
                    surface.bottomRightRadius = fallbackRadius
                    surface.bottomLeftRadius = fallbackRadius
                }
            }
            val minimumDimension = floatSize.minDimension
            surface.opticalDepth = min(with(density) { 28.dp.toPx() }, minimumDimension * 0.34f)
                .coerceAtLeast(with(density) { 6.dp.toPx() })
            surface.refractionAmount = min(
                with(density) { 40.dp.toPx() },
                surface.opticalDepth * 1.72f,
            )
        }
        revision++
    }

    /** Packs only surfaces that intersect the current viewport into fixed shader arrays. */
    internal fun packVisibleSurfaces(): Int {
        revision
        var count = 0
        for (surface in surfaces.values) {
            val left = surface.left - viewportLeft
            val top = surface.top - viewportTop
            val right = left + surface.width
            val bottom = top + surface.height
            if (right < -2f || bottom < -2f || left > viewportWidth + 2f || top > viewportHeight + 2f) {
                continue
            }
            if (count >= MAX_GLASS_SURFACES) break

            val index = count * 4
            rectUniforms[index] = left
            rectUniforms[index + 1] = top
            rectUniforms[index + 2] = surface.width
            rectUniforms[index + 3] = surface.height

            radiusUniforms[index] = surface.topLeftRadius
            radiusUniforms[index + 1] = surface.topRightRadius
            radiusUniforms[index + 2] = surface.bottomRightRadius
            radiusUniforms[index + 3] = surface.bottomLeftRadius

            styleUniforms[index] = surface.opticalDepth
            styleUniforms[index + 1] = surface.refractionAmount
            styleUniforms[index + 2] = surface.tintAlpha
            styleUniforms[index + 3] = surface.lightMode

            tintUniforms[index] = surface.tint.red
            tintUniforms[index + 1] = surface.tint.green
            tintUniforms[index + 2] = surface.tint.blue
            tintUniforms[index + 3] = 1f
            count++
        }
        return count
    }

    internal fun rects(): FloatArray = rectUniforms
    internal fun radii(): FloatArray = radiusUniforms
    internal fun styles(): FloatArray = styleUniforms
    internal fun tints(): FloatArray = tintUniforms
}

/**
 * Registers a surface instead of rendering an individual off-screen backdrop. This keeps every
 * card dynamic while avoiding one GPU render target per card.
 */
@Composable
internal fun LiquidGlassOptics(
    shape: Shape,
    tint: Color,
    isLight: Boolean,
    modifier: Modifier = Modifier,
) {
    val scene = LocalGlassScene.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val surfaceKey = remember { Any() }

    DisposableEffect(scene, surfaceKey) {
        onDispose { scene?.removeSurface(surfaceKey) }
    }

    if (scene != null && supportsBackdropRefraction()) {
        Box(
            modifier = modifier.onGloballyPositioned { coordinates ->
                scene.registerSurface(
                    key = surfaceKey,
                    position = coordinates.positionInRoot(),
                    size = coordinates.size,
                    shape = shape,
                    tint = tint,
                    isLight = isLight,
                    density = density,
                    layoutDirection = layoutDirection,
                )
            },
        )
    } else {
        Box(
            modifier = modifier.background(
                color = tint.copy(alpha = if (isLight) 0.075f else 0.055f),
                shape = shape,
            ),
        )
    }
}

/** One full-screen optical pass for all registered surfaces. */
@Composable
internal fun RealtimeGlassScene(
    state: GlassSceneState,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
) {
    val light = LocalGlassLightController.current
    val renderer = if (supportsBackdropRefraction()) {
        remember { RealtimeGlassRenderer() }
    } else {
        null
    }
    val trackedModifier = modifier.onGloballyPositioned { coordinates ->
        state.updateViewport(coordinates.positionInRoot(), coordinates.size)
    }

    if (!supportsBackdropRefraction()) {
        Box(trackedModifier)
        return
    }

    Box(
        modifier = trackedModifier.drawBackdrop(
            backdrop = backdrop,
            shape = { RectangleShape },
            effects = {
                realtimeGlassScene(
                    renderer = checkNotNull(renderer),
                    state = state,
                    light = Offset(light.x, light.y),
                )
            },
            highlight = null,
            shadow = null,
            innerShadow = null,
            onDrawSurface = {},
        ),
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class RealtimeGlassRenderer {
    private val shader = RuntimeShader(REALTIME_GLASS_SCENE_SHADER)
    val renderEffect: RenderEffect = RenderEffect.createRuntimeShaderEffect(shader, "content")

    fun update(
        size: Size,
        surfaceCount: Int,
        state: GlassSceneState,
        light: Offset,
    ) {
        shader.setFloatUniform("sceneSize", size.width, size.height)
        shader.setIntUniform("surfaceCount", surfaceCount)
        shader.setFloatUniform("rects", state.rects())
        shader.setFloatUniform("cornerRadii", state.radii())
        shader.setFloatUniform("styles", state.styles())
        shader.setFloatUniform("tints", state.tints())
        shader.setFloatUniform("light", light.x, light.y)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun BackdropEffectScope.realtimeGlassScene(
    renderer: RealtimeGlassRenderer,
    state: GlassSceneState,
    light: Offset,
) {
    val surfaceCount = state.packVisibleSurfaces()
    renderer.update(
        size = size,
        surfaceCount = surfaceCount,
        state = state,
        light = light,
    )
    effect(renderer.renderEffect)
}

private const val REALTIME_GLASS_SCENE_SHADER = """
    uniform shader content;
    uniform float2 sceneSize;
    uniform int surfaceCount;
    uniform float4 rects[12];
    uniform float4 cornerRadii[12];
    uniform float4 styles[12];
    uniform float4 tints[12];
    uniform float2 light;

    float radiusAt(float2 coord, float4 radii) {
        if (coord.x >= 0.0) {
            return coord.y <= 0.0 ? radii.y : radii.z;
        }
        return coord.y <= 0.0 ? radii.x : radii.w;
    }

    float sdRoundedRect(float2 coord, float2 halfSize, float radius) {
        float2 cornerCoord = abs(coord) - (halfSize - float2(radius));
        float outside = length(max(cornerCoord, 0.0)) - radius;
        float inside = min(max(cornerCoord.x, cornerCoord.y), 0.0);
        return outside + inside;
    }

    float2 gradRoundedRect(float2 coord, float2 halfSize, float radius) {
        float2 cornerCoord = abs(coord) - (halfSize - float2(radius));
        if (cornerCoord.x >= 0.0 || cornerCoord.y >= 0.0) {
            return sign(coord) * normalize(max(cornerCoord, 0.0) + float2(0.0001));
        }
        float gradX = step(cornerCoord.y, cornerCoord.x);
        return sign(coord) * float2(gradX, 1.0 - gradX);
    }

    float circleMap(float value) {
        return 1.0 - sqrt(max(0.0, 1.0 - value * value));
    }

    half4 main(float2 coord) {
        float coverage = 0.0;
        float2 displacement = float2(0.0);
        float2 dispersion = float2(0.0);
        float tintWeight = 0.0;
        float3 tintSum = float3(0.0);
        float highlight = 0.0;
        float shade = 0.0;
        float edgeEnergy = 0.0;
        float2 attitude = normalize(light + float2(-0.10, -0.14));

        for (int i = 0; i < 12; ++i) {
            if (i >= surfaceCount) break;
            float4 rect = rects[i];
            float2 local = coord - rect.xy;
            if (local.x < -1.5 || local.y < -1.5 || local.x > rect.z + 1.5 || local.y > rect.w + 1.5) {
                continue;
            }

            float2 halfSize = rect.zw * 0.5;
            float2 centered = local - halfSize;
            float radius = radiusAt(centered, cornerRadii[i]);
            float distance = sdRoundedRect(centered, halfSize, radius);
            float surfaceCoverage = smoothstep(1.25, -1.25, distance);
            if (surfaceCoverage <= 0.0) continue;

            coverage = max(coverage, surfaceCoverage);
            float4 style = styles[i];
            float layerTint = style.z * surfaceCoverage;
            tintSum += tints[i].rgb * layerTint;
            tintWeight += layerTint;

            float insideDistance = max(-distance, 0.0);
            float edge = clamp(1.0 - insideDistance / max(style.x, 1.0), 0.0, 1.0);
            if (edge <= 0.0001) continue;

            float lens = circleMap(edge) * surfaceCoverage;
            float gradRadius = min(radius * 1.5, min(halfSize.x, halfSize.y));
            float2 normal = normalize(
                gradRoundedRect(centered, halfSize, gradRadius) +
                normalize(centered + float2(0.0001)) * 0.58
            );
            displacement += normal * (-style.y * lens);
            dispersion += normal * ((1.45 + style.y * 0.075) * lens);
            edgeEnergy = max(edgeEnergy, lens);

            float facing = dot(normal, -attitude);
            highlight += pow(max(facing, 0.0), 2.35) * edge * surfaceCoverage;
            shade += pow(max(-facing, 0.0), 2.0) * edge * surfaceCoverage *
                (style.w > 0.5 ? 0.78 : 1.0);
        }

        if (coverage <= 0.0) return half4(0.0);

        float2 sampleCoord = clamp(
            coord + displacement,
            float2(0.5),
            sceneSize - float2(0.5)
        );
        float2 tangent = normalize(float2(-dispersion.y, dispersion.x) + float2(0.0001));
        float blurRadius = min(1.15, edgeEnergy * 1.15);
        half4 centerSample = content.eval(sampleCoord);
        half3 clearSample = centerSample.rgb;

        if (blurRadius > 0.01) {
            clearSample =
                centerSample.rgb * half(0.42) +
                content.eval(clamp(sampleCoord + tangent * blurRadius, float2(0.5), sceneSize - float2(0.5))).rgb * half(0.145) +
                content.eval(clamp(sampleCoord - tangent * blurRadius, float2(0.5), sceneSize - float2(0.5))).rgb * half(0.145) +
                content.eval(clamp(sampleCoord + normalize(dispersion + float2(0.0001)) * blurRadius * 0.72, float2(0.5), sceneSize - float2(0.5))).rgb * half(0.145) +
                content.eval(clamp(sampleCoord - normalize(dispersion + float2(0.0001)) * blurRadius * 0.72, float2(0.5), sceneSize - float2(0.5))).rgb * half(0.145);
        }

        float2 chroma = dispersion;
        half3 color = half3(
            content.eval(clamp(sampleCoord + chroma, float2(0.5), sceneSize - float2(0.5))).r,
            clearSample.g,
            content.eval(clamp(sampleCoord - chroma, float2(0.5), sceneSize - float2(0.5))).b
        );
        float safeTintWeight = max(tintWeight, 0.0001);
        half3 tintColor = half3(tintSum / safeTintWeight);
        color = mix(color, tintColor, half(clamp(tintWeight, 0.0, 0.16)));
        color = mix(color, half3(1.0), half(clamp(highlight * 0.145, 0.0, 0.25)));
        color = mix(color, half3(0.0), half(clamp(shade * 0.052, 0.0, 0.095)));
        return half4(color, half(coverage));
    }
"""
