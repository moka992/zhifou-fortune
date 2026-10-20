package com.zhifou.fortune

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File

/** Real application UI with a separate empty data store. Shell access only; never opens user preferences. */
class ShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dark = intent.getBooleanExtra("dark", false)
        val isolated = ShowcaseApplication(applicationContext)
        isolated.getSharedPreferences("zhifou_fortune", MODE_PRIVATE).edit().clear()
            .putString("theme_mode", if (dark) ThemeMode.Dark.name else ThemeMode.Light.name)
            .putBoolean("haptics_enabled", false)
            .putBoolean("glass_motion_enabled", false)
            .commit()
        val vm = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass == FortuneViewModel::class.java)
                return FortuneViewModel(isolated) as T
            }
        })[FortuneViewModel::class.java]
        val bars = if (dark) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = bars, navigationBarStyle = bars)
        setContent {
            ZhiFouTheme(darkTheme = dark) {
                val haptics = rememberAppHaptics(false)
                val lamp = remember { GlassLightController() }
                CompositionLocalProvider(LocalAppHaptics provides haptics, LocalGlassLightController provides lamp) {
                    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        FortuneAppMd3(vm)
                    }
                }
            }
        }
    }
}

private class ShowcaseApplication(private val base: Context) : Application() {
    init { attachBaseContext(base) }
    override fun getApplicationContext(): Context = this
    override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
        base.getSharedPreferences("showcase_$name", mode)
    override fun getCacheDir(): File = File(base.cacheDir, "showcase").apply { mkdirs() }
}
