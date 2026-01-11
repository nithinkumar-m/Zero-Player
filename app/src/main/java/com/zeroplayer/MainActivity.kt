package com.zeroplayer

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.zeroplayer.presentation.app.ZeroPlayerAppRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    ZeroPlayerAppRoot(
                        onEnterPip = { tryEnterPip() },
                    )
                }
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

