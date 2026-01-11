package com.zeroplayer

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
                ZeroPlayerAppRoot(
                    onEnterPip = { tryEnterPip() },
                )
            }
        }
    }

    private fun tryEnterPip() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        enterPipO()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPipO() {
        try {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        } catch (_: Throwable) {
            // best-effort
        }
    }
}

