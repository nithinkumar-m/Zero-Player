package com.zeroplayer.player

import android.app.PictureInPictureParams
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.activity.viewModels
import com.zeroplayer.presentation.playerhost.PlayerHostRoot
import com.zeroplayer.presentation.playerhost.PlayerHostViewModel
import com.zeroplayer.presentation.theme.ZeroPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {
    private val viewModel: PlayerHostViewModel by viewModels()
    private var uriString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uriString = intent?.getStringExtra(EXTRA_URI).orEmpty()

        setContent {
            ZeroPlayerTheme {
                PlayerHostRoot(
                    onBack = { finish() },
                    onEnterPip = { tryEnterPip() },
                    viewModel = viewModel,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Start/resume playback once per Activity start, not from recompositions.
        viewModel.onStart(uriString)
    }

    override fun onStop() {
        viewModel.onStop(isFinishing)
        super.onStop()
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

