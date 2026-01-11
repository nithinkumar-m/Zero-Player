package com.zeroplayer.player

import android.app.PictureInPictureParams
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.zeroplayer.presentation.playerhost.PlayerHostRoot
import com.zeroplayer.presentation.theme.ZeroPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uriString = intent?.getStringExtra(EXTRA_URI).orEmpty()

        setContent {
            ZeroPlayerTheme {
                PlayerHostRoot(
                    uriString = uriString,
                    onBack = { finish() },
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

    companion object {
        private const val EXTRA_URI = "extra_uri"

        fun intent(context: android.content.Context, uriString: String): Intent =
            Intent(context, PlayerActivity::class.java).putExtra(EXTRA_URI, uriString)
    }
}

