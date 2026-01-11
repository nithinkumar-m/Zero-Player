package com.zeroplayer.presentation.playerhost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeroplayer.domain.repository.SettingsRepository
import com.zeroplayer.domain.usecase.SeekByUseCase
import com.zeroplayer.domain.usecase.StartPlaybackUseCase
import com.zeroplayer.domain.usecase.StopPlaybackUseCase
import com.zeroplayer.playback.PlaybackComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlayerHostViewModel @Inject constructor(
    private val startPlayback: StartPlaybackUseCase,
    private val stopPlayback: StopPlaybackUseCase,
    private val seekBy: SeekByUseCase,
    val playback: PlaybackComponent,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val settings = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = com.zeroplayer.domain.repository.PlayerSettings(),
    )

    fun start(uriString: String) {
        if (uriString.isBlank()) return
        startPlayback(uriString)
    }

    fun seekBy(deltaMs: Long) {
        seekBy(deltaMs)
    }

    override fun onCleared() {
        stopPlayback()
        super.onCleared()
    }
}

