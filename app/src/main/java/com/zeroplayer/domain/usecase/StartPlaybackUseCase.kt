package com.zeroplayer.domain.usecase

import com.zeroplayer.domain.playback.PlaybackCommander
import javax.inject.Inject

class StartPlaybackUseCase @Inject constructor(
    private val playbackCommander: PlaybackCommander,
) {
    operator fun invoke(uriString: String) {
        playbackCommander.setMedia(uriString)
        playbackCommander.play()
    }
}

