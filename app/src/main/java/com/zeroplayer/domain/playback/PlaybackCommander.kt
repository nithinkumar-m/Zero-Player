package com.zeroplayer.domain.playback

import kotlinx.coroutines.flow.StateFlow

/**
 * Domain-facing playback commands (no Android/Media3 types here).
 */
interface PlaybackCommander {
    val state: StateFlow<PlaybackState>

    fun setMedia(uriString: String)
    fun play()
    fun pause()
    fun seekBy(deltaMs: Long)
    fun seekTo(positionMs: Long)
    fun release()
}

