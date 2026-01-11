package com.zeroplayer.presentation.player

import android.app.Activity
import android.net.Uri
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.zeroplayer.R

@Composable
fun PlayerScreen(
    uriString: String,
    onBack: () -> Unit,
    onEnterPip: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uri = remember(uriString) { Uri.parse(uriString) }
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val player = remember(uriString) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player, activity) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                val w = videoSize.width
                val h = videoSize.height
                if (w <= 0 || h <= 0) return

                // Requirement:
                // - If the video is landscape (including 16:9), lock landscape.
                // - If the video is portrait, lock portrait.
                activity?.requestedOrientation =
                    if (w >= h) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    else ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            player.release()
        }
    }

    val seekToast = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(seekToast.value) {
        if (seekToast.value != null) {
            kotlinx.coroutines.delay(650)
            seekToast.value = null
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = with(androidx.compose.ui.platform.LocalDensity.current) { maxWidth.toPx() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(settings.doubleTapSeekMs, widthPx) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            val seekMs = settings.doubleTapSeekMs
                            val isLeft = offset.x < (widthPx / 2f)
                            val delta = if (isLeft) -seekMs else seekMs
                            val target = (player.currentPosition + delta).coerceAtLeast(0L)
                            player.seekTo(target)
                            seekToast.value = if (isLeft) "-${seekMs / 1000}s" else "+${seekMs / 1000}s"
                        },
                    )
                },
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        this.player = player
                        useController = true
                    }
                },
                update = { view ->
                    view.player = player
                },
            )

            // Tiny on-screen feedback for double-tap seek.
            val toast = seekToast.value
            if (toast != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        text = toast,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            // Minimal overlay actions.
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(onClick = onBack) {
                    Text(text = "Back", style = MaterialTheme.typography.labelLarge)
                }
                IconButton(onClick = onEnterPip) {
                    Text(
                        text = stringResource(id = R.string.enter_pip),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

