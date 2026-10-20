package com.zhifou.fortune

import android.os.Build
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import kotlin.math.abs
import kotlin.math.sin

@Immutable
internal data class TarotAtelierPalette(
    val background: Color, val ink: Color, val muted: Color, val line: Color,
    val metal: Color, val accent: Color, val control: Color, val light: Boolean,
)

internal fun tarotAtelierPalette(light: Boolean) = if (light) TarotAtelierPalette(
    Color(0xFFF3F6F8), Color(0xFF1E2932), Color(0xFF60707B), Color(0xFFD0DADF),
    Color(0xFF8D7654), Color(0xFF286579), Color(0xFFFDFEFF), true,
) else TarotAtelierPalette(
    Color(0xFF12161B), Color(0xFFEEF3F6), Color(0xFFA6B6BF), Color(0xFF35434D),
    Color(0xFFD4BE93), Color(0xFFB0DEEA), Color(0xFF202930), false,
)

internal val LocalTarotAtelier = staticCompositionLocalOf { tarotAtelierPalette(true) }

internal fun tarotAtelierAnchor(position: Int, compact: Boolean): Pair<Float, Float> {
    require(position in 1..4)
    return if (compact) (position - .5f) / 4f to .5f
        else (if (position % 2 == 1) .275f else .725f) to (if (position <= 2) .255f else .755f)
}

/** Original foil-backed card art; historical fronts remain unmodified. */
@Composable
internal fun TarotAtelierBack(phase: () -> Float, tilt: () -> Offset, modifier: Modifier = Modifier) {
    val renderer = if (Build.VERSION.SDK_INT >= 33) remember { runCatching { TarotBackRenderer() }.getOrNull() } else null
    Canvas(modifier) {
        if (renderer != null && Build.VERSION.SDK_INT >= 33) {
            renderer.update(size.width, size.height, phase(), tilt())
            drawIntoCanvas { it.nativeCanvas.drawRect(0f, 0f, size.width, size.height, renderer.paint) }
        } else {
            drawRect(Color(0xFF20313A))
            val gold = Color(0xFFDCC89E)
            listOf(.045f, .075f, .105f).forEach { inset ->
                drawRoundRect(gold.copy(.75f), topLeft = Offset(size.width * inset, size.width * inset),
                    size = androidx.compose.ui.geometry.Size(size.width * (1 - inset * 2), size.height - size.width * inset * 2),
                    cornerRadius = CornerRadius(3f), style = Stroke(.8f))
            }
            listOf(.17f, .22f, .27f).forEach { r -> drawCircle(gold, size.width * r, style = Stroke(.8f)) }
            val center = center
            drawLine(gold, center - Offset(0f, size.width * .3f), center + Offset(0f, size.width * .3f), 1.2f)
            drawLine(gold, center - Offset(size.width * .3f, 0f), center + Offset(size.width * .3f, 0f), 1.2f)
        }
    }
}

@RequiresApi(33)
private class TarotBackRenderer {
    private val shader = RuntimeShader(TAROT_BACK_SHADER)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { this.shader = this@TarotBackRenderer.shader }
    fun update(width: Float, height: Float, phase: Float, tilt: Offset) {
        shader.setFloatUniform("extent", width.coerceAtLeast(1f), height.coerceAtLeast(1f))
        shader.setFloatUniform("lamp", tilt.x.coerceIn(-1f, 1f), tilt.y.coerceIn(-1f, 1f))
        shader.setFloatUniform("phase", phase.coerceIn(0f, 1f))
    }
}

internal const val TAROT_BACK_SHADER = """
uniform float2 extent;
uniform float2 lamp;
uniform float phase;
float boxLine(float2 p, float2 b) {
    float2 q = abs(p)-b;
    return abs(length(max(q, 0.0)) + min(max(q.x,q.y),0.0));
}
half4 main(float2 xy) {
    float2 p=(xy-.5*extent)/extent.x;
    float aspect=extent.y/extent.x;
    float r=length(p);
    float a=atan(p.y,p.x+0.000001);
    float aa=1.0/extent.x;
    float border=min(boxLine(p,float2(.46,aspect*.5-.04)),boxLine(p,float2(.43,aspect*.5-.07)));
    float star=.12+.072*pow(abs(cos(a*2.0)),6.0);
    float medallion=min(abs(r-.265), min(abs(r-.235), min(abs(r-star),abs(r-.035))));
    float2 diamond=float2(abs(p.x+p.y),abs(p.x-p.y))*.70710678;
    float filigree=boxLine(diamond,float2(.165));
    float ray=abs(sin(a*16.0))*r;
    ray=max(ray, max(.27-r,r-.35));
    float corner=abs(length(abs(p)-float2(.365,aspect*.5-.135))-.037);
    float glyph=min(min(border,medallion), min(min(filigree,ray),corner));
    float mask=1.0-smoothstep(aa*.3,aa*1.4,glyph);
    float3 n=normalize(float3(cos(a)*.16,sin(a)*.16,1.0));
    float sweep=sin(phase*3.14159265);
    float3 l=normalize(float3(-.7+lamp.x+sweep*.8,-.9+lamp.y*.7,1.8));
    float spec=pow(max(dot(n,normalize(l+float3(0,0,1))),0.0),65.0);
    float ribbon=exp(-pow((p.x+p.y*.3-lamp.x*.25-sweep*.55)*7.0,2.0));
    float3 foil=float3(.70,.59,.39)+spec*float3(.30,.35,.42)+ribbon*float3(.18,.20,.23);
    float3 base=float3(.115,.140,.153)+ribbon*.055+spec*.018;
    float engraving=(1.0-smoothstep(aa*1.4,aa*3.0,glyph))*(1.0-mask);
    base-=engraving*.035;
    return half4(half3(mix(base,min(foil,float3(1)),mask)),1);
}
"""

/** An art-preserving clear-coat shader, not a replacement for the historical card artwork. */
@Composable
internal fun Modifier.tarotClearCoat(
    phase: () -> Float,
    tilt: () -> Offset,
    enabled: Boolean = true,
): Modifier {
    val renderer = if (Build.VERSION.SDK_INT >= 33 && enabled) remember {
        runCatching { TarotClearCoatRenderer() }.getOrNull()
    } else null
    return if (renderer != null && Build.VERSION.SDK_INT >= 33) graphicsLayer {
        val offset = tilt()
        renderEffect = renderer.update(size.width, size.height, phase(), offset)
    } else drawWithCache {
        onDrawWithContent {
            drawContent()
            if (enabled) {
                val t = phase()
                val light = tilt()
                val strength = (.035f + abs(sin(t * 3.14159f)) * .11f).coerceIn(0f, .16f)
                val x = size.width * (.35f + light.x * .25f + t * .35f)
                drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(strength), Color.Transparent),
                    Offset(x - size.width * .35f, 0f), Offset(x + size.width * .35f, size.height)))
            }
        }
    }
}

@RequiresApi(33)
private class TarotClearCoatRenderer {
    private val shader = RuntimeShader(TAROT_COAT_SHADER)
    private var effect: androidx.compose.ui.graphics.RenderEffect? = null
    private var lastWidth = -1f
    private var lastHeight = -1f
    private var lastPhase = -1f
    private var lastTilt = Offset.Unspecified

    fun update(width: Float, height: Float, phase: Float, tilt: Offset): androidx.compose.ui.graphics.RenderEffect {
        if (effect == null || width != lastWidth || height != lastHeight || phase != lastPhase || tilt != lastTilt) {
            shader.setFloatUniform("extent", width.coerceAtLeast(1f), height.coerceAtLeast(1f))
            shader.setFloatUniform("lamp", tilt.x.coerceIn(-1f, 1f), tilt.y.coerceIn(-1f, 1f))
            shader.setFloatUniform("phase", phase.coerceIn(0f, 1f))
            // RenderEffect snapshots uniforms; create it only after all current values are set.
            effect = RenderEffect.createRuntimeShaderEffect(shader, "art").asComposeRenderEffect()
            lastWidth = width
            lastHeight = height
            lastPhase = phase
            lastTilt = tilt
        }
        return checkNotNull(effect)
    }
}

internal const val TAROT_COAT_SHADER = """
uniform shader art;
uniform float2 extent;
uniform float2 lamp;
uniform float phase;
half4 main(float2 xy) {
    half4 base = art.eval(xy);
    if (base.a < 0.001) return base;
    float2 uv = xy / extent;
    float turn = sin(phase * 3.14159265);
    float3 n = normalize(float3((uv - .5) * .045 + lamp * .12, 1.0));
    float3 l = normalize(float3(-.55 + lamp.x * .6 + turn * .9, -.8 + lamp.y * .4, 1.8));
    float3 h = normalize(l + float3(0.0, 0.0, 1.0));
    float specular = pow(max(dot(n, h), 0.0), 140.0);
    float stripe = uv.x * .80 + uv.y * .34 - .4 - lamp.x * .23 - turn * .58;
    float ribbon = exp(-stripe * stripe * 280.0);
    float edge = min(min(uv.x, 1.0-uv.x), min(uv.y, 1.0-uv.y));
    float bevel = exp(-edge * 170.0);
    float3 spectrum = .5 + .5 * cos(float3(0.0, 2.094, 4.188) + (uv.x + uv.y) * 15.0 + lamp.x * 4.0 + turn * 8.0);
    float coat = specular * .085 + ribbon * (.035 + turn * .10);
    float3 rgb = float3(base.rgb) + (float3(base.a) - float3(base.rgb)) * coat;
    rgb += spectrum * bevel * (.015 + turn * .035) * base.a;
    return half4(half3(min(rgb, float3(base.a))), base.a);
}
"""
