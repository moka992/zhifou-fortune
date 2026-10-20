package com.zhifou.fortune

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Matrix
import android.view.Choreographer
import android.view.Surface
import android.view.TextureView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.Gltfio
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

@Composable
internal fun CoinModelScene(angles: List<Float>, rolling: Boolean, description: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var model by remember { mutableStateOf<ByteBuffer?>(null) }
    var failed by remember { mutableStateOf(false) }
    var nativeView by remember { mutableStateOf<CoinFilamentView?>(null) }
    LaunchedEffect(Unit) {
        model = withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open("models/zhifou_coin.glb").use { input ->
                    val bytes = input.readBytes()
                    ByteBuffer.allocateDirect(bytes.size).order(ByteOrder.nativeOrder()).apply { put(bytes); flip() }
                }
            }.getOrNull()
        }
        failed = model == null
    }
    DisposableEffect(nativeView, lifecycle) {
        val observer = LifecycleEventObserver { _, _ ->
            nativeView?.setActive(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        }
        lifecycle.addObserver(observer)
        nativeView?.setActive(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        onDispose { lifecycle.removeObserver(observer); nativeView?.setActive(false) }
    }
    Box(modifier.semantics { contentDescription = description }, contentAlignment = Alignment.Center) {
        if (model != null && !failed) {
            AndroidView(
                factory = { ctx -> CoinFilamentView(ctx, model!!.duplicate()).also { nativeView = it } },
                update = { it.updateCoins(angles, rolling) },
                onRelease = { it.dispose(); nativeView = null },
                modifier = Modifier.fillMaxSize(),
            )
        } else if (failed) Text("硬币模型加载失败，请重新打开页面")
        else CircularProgressIndicator()
    }
}

/** One GPU scene for all coins; geometry and PBR materials are shared across ten GLB nodes. */
@SuppressLint("ViewConstructor") // Created by AndroidView with a packaged model, never inflated from XML.
internal class CoinFilamentView(context: Context, model: ByteBuffer) : TextureView(context), Choreographer.FrameCallback {
    private val engine: Engine
    private val renderer: Renderer
    private val scene: Scene
    private val view: View
    private val camera: Camera
    private val provider: UbershaderProvider
    private val loader: AssetLoader
    private val resources: ResourceLoader
    private val ui = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
    private val displayHelper = DisplayHelper(context)
    private val choreographer = Choreographer.getInstance()
    private var swapChain: SwapChain? = null
    private var active = false
    private var disposed = false
    private var scheduled = false
    private var dirtyFrames = 8
    private var angles = listOf(0f)
    private var rolling = false
    private val matrix = FloatArray(16)
    private val ready = IntArray(32)
    private val lights = IntArray(2)
    private val reflection: Texture
    private val indirect: IndirectLight
    private val asset: com.google.android.filament.gltfio.FilamentAsset
    private val coins: IntArray

    init {
        Filament.init()
        Gltfio.init()
        isOpaque = false
        engine = Engine.create()
        renderer = engine.createRenderer().apply {
            clearOptions = Renderer.ClearOptions().apply { clear = true; clearColor = floatArrayOf(0f, 0f, 0f, 0f) }
        }
        scene = engine.createScene()
        camera = engine.createCamera(EntityManager.get().create()).apply {
            setExposure(12f, 1f / 60f, 100f)
            lookAt(0.0, 0.0, 12.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        }
        view = engine.createView().apply {
            this.scene = this@CoinFilamentView.scene
            this.camera = this@CoinFilamentView.camera
            blendMode = View.BlendMode.TRANSLUCENT
            antiAliasing = View.AntiAliasing.FXAA
            setShadowingEnabled(false)
            isPostProcessingEnabled = true
        }
        reflection = createStudioReflection(engine)
        indirect = IndirectLight.Builder().reflections(reflection).intensity(25_000f).build(engine)
        scene.indirectLight = indirect
        repeat(2) { index ->
            lights[index] = EntityManager.get().create()
            LightManager.Builder(LightManager.Type.DIRECTIONAL)
                .color(if (index == 0) 1f else .90f, if (index == 0) .98f else .96f, if (index == 0) .94f else 1f)
                .intensity(if (index == 0) 38_000f else 24_000f)
                .direction(if (index == 0) -1f else 1f, -.6f, -1.5f)
                .castShadows(false).build(engine, lights[index])
            scene.addEntity(lights[index])
        }
        provider = UbershaderProvider(engine)
        loader = AssetLoader(engine, provider, EntityManager.get())
        resources = ResourceLoader(engine, true)
        asset = requireNotNull(loader.createAsset(model)) { "Invalid coin asset" }
        coins = IntArray(10) { index ->
            asset.entities.first { asset.getName(it) == "Coin_${(index + 1).toString().padStart(2, '0')}" }
        }
        resources.asyncBeginLoad(asset)
        asset.releaseSourceData()
        ui.isOpaque = false
        ui.renderCallback = object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                swapChain?.let(engine::destroySwapChain)
                swapChain = engine.createSwapChain(surface, ui.swapChainFlags)
                displayHelper.attach(renderer, display)
                requestFrames()
            }
            override fun onDetachedFromSurface() {
                displayHelper.detach()
                swapChain?.let { engine.destroySwapChain(it); engine.flushAndWait() }
                swapChain = null
            }
            override fun onResized(width: Int, height: Int) {
                view.viewport = Viewport(0, 0, width, height)
                requestFrames()
            }
        }
        ui.attachTo(this)
    }

    fun updateCoins(newAngles: List<Float>, isRolling: Boolean) {
        if (disposed) return
        angles = newAngles
        rolling = isRolling
        requestFrames()
    }

    fun setActive(value: Boolean) {
        active = value
        if (value) requestFrames() else {
            choreographer.removeFrameCallback(this)
            scheduled = false
        }
    }

    private fun requestFrames() {
        dirtyFrames = 4
        scheduleFrame()
    }

    private fun scheduleFrame() {
        if (active && !disposed && !scheduled) {
            scheduled = true
            choreographer.postFrameCallback(this)
        }
    }

    override fun doFrame(frameTimeNanos: Long) {
        scheduled = false
        if (!active || disposed) return
        resources.asyncUpdateLoad()
        var count = asset.popRenderables(ready)
        while (count > 0) {
            scene.addEntities(ready.copyOf(count))
            count = asset.popRenderables(ready)
        }
        val viewport = view.viewport
        if (viewport.width > 0 && viewport.height > 0) {
            val rows = ceil(angles.size / 5.0).coerceAtLeast(1.0)
            val halfHeight = max(rows * 1.4, min(angles.size, 5) * 1.34 * viewport.height / viewport.width)
            val halfWidth = halfHeight * viewport.width / viewport.height
            camera.setProjection(Camera.Projection.ORTHO, -halfWidth, halfWidth, -halfHeight, halfHeight, .1, 30.0)
            coins.forEachIndexed { index, entity ->
                val renderable = engine.renderableManager.getInstance(entity)
                if (renderable != 0) engine.renderableManager.setLayerMask(renderable, 0xff, if (index < angles.size) 1 else 0)
                if (index < angles.size) {
                    val pose = coinScenePose(index, angles.size, angles[index], rolling)
                    Matrix.setIdentityM(matrix, 0)
                    Matrix.translateM(matrix, 0, pose.x, pose.y, 0f)
                    Matrix.rotateM(matrix, 0, pose.tilt, 1f, 0f, 0f)
                    Matrix.rotateM(matrix, 0, pose.rotation, 0f, 1f, 0f)
                    engine.transformManager.setTransform(engine.transformManager.getInstance(entity), matrix)
                }
            }
            if (ui.isReadyToRender && swapChain != null && renderer.beginFrame(swapChain!!, frameTimeNanos)) {
                renderer.render(view)
                renderer.endFrame()
                dirtyFrames--
            }
        }
        if (dirtyFrames > 0 || resources.asyncGetLoadProgress() < 1f) scheduleFrame()
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        choreographer.removeFrameCallback(this)
        ui.detach()
        resources.asyncCancelLoad()
        scene.removeEntities(asset.entities)
        loader.destroyAsset(asset)
        resources.destroy()
        loader.destroy()
        provider.destroyMaterials()
        provider.destroy()
        engine.destroyIndirectLight(indirect)
        engine.destroyTexture(reflection)
        lights.forEach { engine.destroyEntity(it); EntityManager.get().destroy(it) }
        engine.destroyView(view)
        engine.destroyScene(scene)
        val cameraEntity = camera.entity
        engine.destroyCameraComponent(cameraEntity)
        EntityManager.get().destroy(cameraEntity)
        engine.destroyRenderer(renderer)
        engine.destroy()
    }
}

// A small neutral studio cubemap. Broadening the softboxes per mip gives rough metal soft reflections.
@Suppress("DEPRECATION")
private fun createStudioReflection(engine: Engine): Texture {
    val texture = Texture.Builder().width(64).height(64).levels(7)
        .sampler(Texture.Sampler.SAMPLER_CUBEMAP).format(Texture.InternalFormat.R11F_G11F_B10F).build(engine)
    repeat(7) { level ->
        val size = max(1, 64 shr level)
        val pixels = ByteBuffer.allocateDirect(size * size * 6 * 3 * 4).order(ByteOrder.nativeOrder())
        repeat(6) { face -> repeat(size) { y -> repeat(size) { x ->
            val u = 2f * (x + .5f) / size - 1f
            val v = 2f * (y + .5f) / size - 1f
            val direction = when (face) {
                0 -> floatArrayOf(1f, -v, -u)
                1 -> floatArrayOf(-1f, -v, u)
                2 -> floatArrayOf(u, 1f, v)
                3 -> floatArrayOf(u, -1f, -v)
                4 -> floatArrayOf(u, -v, 1f)
                else -> floatArrayOf(-u, -v, -1f)
            }
            val length = sqrt(direction.sumOf { (it * it).toDouble() }).toFloat()
            val dx = direction[0] / length; val dy = direction[1] / length; val dz = direction[2] / length
            val sharpness = 40f / (1f + level * level * 1.4f)
            // Preserve softbox energy as rougher mip levels broaden each reflection lobe.
            val left = max(0f, dx * -.6f + dy * .5f + dz * .6245f).pow(sharpness) * (sharpness + 1f) / 41f
            val right = max(0f, dx * .8f + dy * .25f + dz * .5454f).pow(sharpness * .8f) * (sharpness * .8f + 1f) / 33f
            val top = max(0f, dy * .93f - dz * .367f).pow(sharpness * .55f) * (sharpness * .55f + 1f) / 23f
            val ambient = .10f + max(0f, dy) * .18f
            pixels.putFloat(ambient + left * 2.7f + right * 1.5f + top * 1.1f)
            pixels.putFloat(ambient + left * 2.6f + right * 1.6f + top * 1.1f)
            pixels.putFloat(ambient + left * 2.5f + right * 1.9f + top * 1.15f)
        } } }
        pixels.flip()
        // This Filament release's 3D JNI upload scales explicit mip heights a second time.
        // The six-face overload retains the correct byte count for every mip level.
        texture.setImage(engine, level,
            Texture.PixelBufferDescriptor(pixels, Texture.Format.RGB, Texture.Type.FLOAT),
            IntArray(6) { it * size * size * 3 * 4 })
    }
    return texture
}
