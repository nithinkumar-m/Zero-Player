package com.zeroplayer.playback

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.zeroplayer.domain.playback.PlaybackCommander
import com.zeroplayer.domain.playback.PlaybackState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class Media3PlaybackComponent @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : PlaybackComponent, PlaybackCommander {

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var tickerJob: Job? = null

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state

    override val player: Player = exoPlayer

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { it.copy(isPlaying = isPlaying) }
            ensureTicker()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            // Update duration when ready.
            _state.update {
                it.copy(
                    durationMs = exoPlayer.duration.coerceAtLeast(0L),
                    bufferedPositionMs = exoPlayer.bufferedPosition.coerceAtLeast(0L),
                )
            }
            ensureTicker()
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            _state.update { it.copy(videoWidth = videoSize.width, videoHeight = videoSize.height) }
        }
    }

    init {
        exoPlayer.addListener(listener)
        ensureTicker()
    }

    override fun setMedia(uriString: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(uriString))
        exoPlayer.prepare()
        ensureTicker()
    }

    override fun play() {
        exoPlayer.playWhenReady = true
        exoPlayer.play()
        ensureTicker()
    }

    override fun pause() {
        exoPlayer.pause()
        ensureTicker()
    }

    override fun seekBy(deltaMs: Long) {
        val target = (exoPlayer.currentPosition + deltaMs).coerceAtLeast(0L)
        exoPlayer.seekTo(target)
    }

    override fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs.coerceAtLeast(0L))
    }

    override fun release() {
        tickerJob?.cancel()
        tickerJob = null
        exoPlayer.removeListener(listener)
        exoPlayer.release()
        scope.coroutineContext.cancel()
    }

    private fun ensureTicker() {
        if (tickerJob?.isActive == true) return
        tickerJob = scope.launch {
            while (true) {
                _state.update {
                    it.copy(
                        positionMs = exoPlayer.currentPosition.coerceAtLeast(0L),
                        bufferedPositionMs = exoPlayer.bufferedPosition.coerceAtLeast(0L),
                        durationMs = exoPlayer.duration.coerceAtLeast(0L),
                    )
                }
                delay(250)
            }
        }
    }
}

