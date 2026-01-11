package com.zeroplayer.playback

import androidx.media3.common.Player
import com.zeroplayer.domain.playback.PlaybackState
import kotlinx.coroutines.flow.StateFlow

/**
 * Playback layer surface for the UI.
 * - UI can attach [player] to a Media3 [androidx.media3.ui.PlayerView].
 * - State is exposed as Flow for Compose to render / for Activities to react (orientation, PiP, etc.).
 */
interface PlaybackComponent {
    val player: Player
    val state: StateFlow<PlaybackState>
}

