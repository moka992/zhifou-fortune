package com.zhifou.fortune

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
internal fun ObserveDiceShake(enabled: Boolean, onShake: (DiceThrowStrength) -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentEnabled by rememberUpdatedState(enabled)
    val currentOnShake by rememberUpdatedState(onShake)
    DisposableEffect(context, lifecycle) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        var detector: DiceShakeDetector? = null
        var listening = false
        var selectedType = -1
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (!listening || event.sensor.type != selectedType || event.values.size < 3) return
                detector?.sample(event.timestamp, event.values[0], event.values[1], event.values[2], currentEnabled)
                    ?.let(currentOnShake)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        fun stop() {
            listening = false
            manager?.unregisterListener(listener)
            detector?.reset()
        }
        fun start() {
            if (listening || manager == null) return
            for (type in intArrayOf(Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_ACCELEROMETER)) {
                val sensor = manager.getDefaultSensor(type) ?: continue
                detector = DiceShakeDetector(type == Sensor.TYPE_ACCELEROMETER, sensor.maximumRange)
                selectedType = type
                if (manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)) {
                    listening = true
                    break
                }
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> start()
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> stop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) start()
        onDispose { lifecycle.removeObserver(observer); stop() }
    }
}
