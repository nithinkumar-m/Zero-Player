package com.zeroplayer.presentation.player

import android.app.Activity
import android.net.Uri
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uri = remember(uriString) { Uri.parse(uriString) }

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

    Box(modifier = Modifier.fillMaxSize()) {
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

        // Minimal overlay actions (you'll likely replace with Compose controls + gestures).
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

