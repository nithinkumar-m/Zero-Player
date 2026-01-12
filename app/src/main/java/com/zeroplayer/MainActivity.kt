package com.zeroplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zeroplayer.presentation.app.ZeroPlayerAppRoot
import com.zeroplayer.presentation.theme.ZeroPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            ZeroPlayerTheme {
                ZeroPlayerAppRoot()
            }
        }
    }
}

