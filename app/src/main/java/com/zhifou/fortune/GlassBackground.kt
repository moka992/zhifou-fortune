package com.zhifou.fortune

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

enum class AppBackgroundStyle(
    val label: String,
    val drawableRes: Int? = null,
) {
    Solid("纯色"),
    JadeSilk("墨玉流光", R.drawable.background_jade_silk),
    PearlPaper("珍珠纸", R.drawable.background_pearl_paper),
    CarbonFiber("碳纤维纹理", R.drawable.background_carbon_fiber),
    BlackSuede("黑色绒面", R.drawable.background_black_suede),
    WhiteSuede("白色绒面", R.drawable.background_white_suede),
    BlackMarble("黑色大理石", R.drawable.background_black_marble),
    WhiteMarble("白色大理石", R.drawable.background_white_marble),
    IceBlueCrystal("淡蓝色水晶", R.drawable.background_ice_blue_crystal),
    LavenderCrystal("淡紫色水晶", R.drawable.background_lavender_crystal),
    ChromeSteel("镀铬不锈钢", R.drawable.background_chrome_steel),
    AnodizedSilver("哑光阳极氧化银色铝", R.drawable.background_anodized_silver),
    HammeredSilver("锤纹银", R.drawable.background_hammered_silver),
    Custom("自选图片"),
}

/** Stable state read from draw lambdas, so sensor changes invalidate drawing instead of layouts. */
@Stable
internal class GlassLightController {
    var x by mutableFloatStateOf(-0.18f)
        private set
    var y by mutableFloatStateOf(-0.24f)
        private set

    fun update(nextX: Float, nextY: Float) {
        x = nextX.coerceIn(-1f, 1f)
        y = nextY.coerceIn(-1f, 1f)
    }
}

internal val LocalGlassLightController = staticCompositionLocalOf { GlassLightController() }

@Composable
internal fun rememberGlassLightController(enabled: Boolean): GlassLightController {
    val context = LocalContext.current
    val controller = remember { GlassLightController() }

    DisposableEffect(context, enabled) {
        if (!enabled) {
            controller.update(-0.18f, -0.24f)
            onDispose { }
        } else {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
                ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            var filteredX = controller.x
            var filteredY = controller.y
            var lastDispatchNanos = 0L
            val listener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.timestamp - lastDispatchNanos < 50_000_000L) return
                    lastDispatchNanos = event.timestamp
                    val gravityX = (-event.values[0] / SensorManager.GRAVITY_EARTH).coerceIn(-1f, 1f)
                    val gravityY = (event.values[1] / SensorManager.GRAVITY_EARTH).coerceIn(-1f, 1f)
                    val (orientedX, orientedY) = orientForDisplay(context, gravityX, gravityY)
                    filteredX = filteredX * 0.78f + orientedX * 0.22f
                    filteredY = filteredY * 0.78f + orientedY * 0.22f
                    if (
                        kotlin.math.abs(filteredX - controller.x) >= 0.018f ||
                        kotlin.math.abs(filteredY - controller.y) >= 0.018f
                    ) {
                        controller.update(filteredX, filteredY)
                    }
                }
            }
            if (sensor != null) {
                sensorManager.registerListener(listener, sensor, 50_000)
            }
            onDispose { sensorManager.unregisterListener(listener) }
        }
    }
    return controller
}

private fun orientForDisplay(context: Context, x: Float, y: Float): Pair<Float, Float> {
    @Suppress("DEPRECATION")
    val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.display?.rotation ?: Surface.ROTATION_0
    } else {
        (context as? Activity)?.windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
    }
    return when (rotation) {
        Surface.ROTATION_90 -> -y to x
        Surface.ROTATION_180 -> -x to -y
        Surface.ROTATION_270 -> y to -x
        else -> x to y
    }
}

@Composable
internal fun AppBackgroundImage(
    style: AppBackgroundStyle,
    customImageUri: String,
    modifier: Modifier = Modifier,
) {
    when {
        style == AppBackgroundStyle.Solid -> Unit
        style.drawableRes != null -> Image(
            painter = painterResource(style.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize(),
        )
        style == AppBackgroundStyle.Custom -> {
            val image = rememberCustomBackground(customImageUri)
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = modifier.fillMaxSize(),
                )
            }
        }
        else -> Unit
    }
}

@Composable
private fun rememberCustomBackground(uriText: String): ImageBitmap? {
    val context = LocalContext.current
    val image by produceState<ImageBitmap?>(initialValue = null, context, uriText) {
        value = if (uriText.isBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) {
                runCatching { decodeBackground(context, Uri.parse(uriText)).asImageBitmap() }.getOrNull()
            }
        }
    }
    return image
}

private fun decodeBackground(context: Context, uri: Uri): Bitmap {
    val resolver = context.contentResolver
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(resolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val sourceWidth = info.size.width.coerceAtLeast(1)
            val sourceHeight = info.size.height.coerceAtLeast(1)
            val scale = min(1f, min(1440f / sourceWidth, 2560f / sourceHeight))
            decoder.setTargetSize(
                (sourceWidth * scale).toInt().coerceAtLeast(1),
                (sourceHeight * scale).toInt().coerceAtLeast(1),
            )
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    }

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
    var sample = 1
    while (bounds.outWidth / sample > 1440 || bounds.outHeight / sample > 2560) sample *= 2
    val options = BitmapFactory.Options().apply {
        inSampleSize = sample
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return resolver.openInputStream(uri).use { stream ->
        requireNotNull(BitmapFactory.decodeStream(stream, null, options))
    }
}
