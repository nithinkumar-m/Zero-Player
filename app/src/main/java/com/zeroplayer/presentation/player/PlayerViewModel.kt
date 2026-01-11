package com.zeroplayer.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeroplayer.domain.repository.PlayerSettings
import com.zeroplayer.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val settings: StateFlow<PlayerSettings> =
        settingsRepository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerSettings())
}

