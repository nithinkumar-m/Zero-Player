package com.zeroplayer.domain.usecase

import com.zeroplayer.domain.playback.PlaybackCommander
import javax.inject.Inject

class StopPlaybackUseCase @Inject constructor(
    private val playbackCommander: PlaybackCommander,
) {
    operator fun invoke() {
        playbackCommander.release()
    }
}

