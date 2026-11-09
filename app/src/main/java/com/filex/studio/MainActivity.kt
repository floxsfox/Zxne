package com.filex.studio

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filex.studio.theme.FileXTheme
import com.filex.studio.theme.resolveDark
import com.filex.studio.ui.navigation.FxApp

class MainActivity : ComponentActivity() {

    private val container: AppContainer
        get() = (application as FileXApp).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val config by container.themeRepository.config.collectAsStateWithLifecycle()
            val dark = config.resolveDark()
            DisposableEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                )
                onDispose {}
            }
            FileXTheme(config) {
                FxApp(container)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        container.permissionManager.refresh()
        container.shizukuController.refresh()
    }
}
