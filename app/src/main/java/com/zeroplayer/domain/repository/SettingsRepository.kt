package com.zeroplayer.domain.repository

import kotlinx.coroutines.flow.Flow

data class PlayerSettings(
    val doubleTapSeekMs: Long = 10_000L,
    val enableAnimations: Boolean = true,
)

interface SettingsRepository {
    val settings: Flow<PlayerSettings>
    suspend fun setDoubleTapSeekMs(value: Long)
    suspend fun setEnableAnimations(value: Boolean)
}

