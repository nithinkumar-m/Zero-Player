package com.zeroplayer.domain.usecase

import com.zeroplayer.domain.playback.PlaybackCommander
import javax.inject.Inject

class SeekByUseCase @Inject constructor(
    private val playbackCommander: PlaybackCommander,
) {
    operator fun invoke(deltaMs: Long) {
        playbackCommander.seekBy(deltaMs)
    }
}

