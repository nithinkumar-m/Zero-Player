package com.zeroplayer.presentation.playerhost

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.SeekParameters
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeroplayer.data.db.PlaybackResumeDao
import com.zeroplayer.data.db.PlaybackResumeEntity
import com.zeroplayer.domain.playback.PlaybackState
import com.zeroplayer.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlayerHostViewModel @Inject constructor(
    @ApplicationContext context: Context,
    settingsRepository: SettingsRepository,
    private val playbackResumeDao: PlaybackResumeDao,
) : ViewModel() {
    val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        seekParameters = SeekParameters.DEFAULT
    }

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState

    val settings = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = com.zeroplayer.domain.repository.PlayerSettings(),
    )

    private var currentUri: String? = null
    private var restoreSeekJob: Job? = null
    private var tickerJob: Job? = null
    private var released = false

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _playbackState.update {
                it.copy(
                    durationMs = player.duration.coerceAtLeast(0L),
                    bufferedPositionMs = player.bufferedPosition.coerceAtLeast(0L),
                )
            }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            _playbackState.update { it.copy(videoWidth = videoSize.width, videoHeight = videoSize.height) }
        }
    }

    init {
        player.addListener(listener)
        // Lightweight position ticker for UI/orientation decisions.
        tickerJob = viewModelScope.launch {
            while (true) {
                _playbackState.update {
                    it.copy(
                        positionMs = player.currentPosition.coerceAtLeast(0L),
                        bufferedPositionMs = player.bufferedPosition.coerceAtLeast(0L),
                        durationMs = player.duration.coerceAtLeast(0L),
                    )
                }
                kotlinx.coroutines.delay(250)
            }
        }
    }

    fun onStart(uriString: String) {
        if (released || uriString.isBlank()) return
        viewModelScope.launch {
            val isSame = currentUri == uriString
            currentUri = uriString

            if (!isSame) {
                player.setMediaItem(MediaItem.fromUri(uriString))
                player.prepare()

                // Restore resume point + speed from Room.
                val resume = withContext(Dispatchers.IO) { playbackResumeDao.get(uriString) }
                if (resume != null) {
                    player.setPlaybackSpeed(resume.playbackSpeed.coerceAtLeast(0.25f))
                    player.seekTo(resume.positionMs.coerceAtLeast(0L))
                }
            }

            player.playWhenReady = true
            player.play()
        }
    }

    fun fastSeekBy(deltaMs: Long) {
        // Snappy scrubbing: sync-frame seeking during bursts, then restore DEFAULT.
        player.seekParameters = SeekParameters.PREVIOUS_SYNC
        val target = (player.currentPosition + deltaMs).coerceAtLeast(0L)
        player.seekTo(target)

        restoreSeekJob?.cancel()
        restoreSeekJob = viewModelScope.launch {
            kotlinx.coroutines.delay(750)
            player.seekParameters = SeekParameters.DEFAULT
        }
    }

    fun onStop(isFinishing: Boolean) {
        if (released) return

        // Pause/release immediately (avoid churn; match Activity lifecycle).
        if (isFinishing) {
            released = true
            player.release()
        } else {
            player.pause()
        }

        // Persist resume asynchronously.
        val uri = currentUri ?: return
        val entity = PlaybackResumeEntity(
            uriString = uri,
            positionMs = player.currentPosition.coerceAtLeast(0L),
            playbackSpeed = player.playbackParameters.speed,
            updatedAtEpochMs = System.currentTimeMillis(),
        )
        viewModelScope.launch(Dispatchers.IO) { playbackResumeDao.upsert(entity) }
    }

    override fun onCleared() {
        player.removeListener(listener)
        tickerJob?.cancel()
        restoreSeekJob?.cancel()
        super.onCleared()
    }
}

