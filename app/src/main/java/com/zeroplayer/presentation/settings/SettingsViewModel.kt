package com.zeroplayer.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeroplayer.domain.repository.PlayerSettings
import com.zeroplayer.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settings: StateFlow<PlayerSettings> =
        settingsRepository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerSettings())

    fun setEnableAnimations(value: Boolean) {
        viewModelScope.launch { settingsRepository.setEnableAnimations(value) }
    }

    fun setDoubleTapSeekMs(value: Long) {
        viewModelScope.launch { settingsRepository.setDoubleTapSeekMs(value) }
    }
}

