package com.zeroplayer.presentation.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeroplayer.domain.model.VideoFolder
import com.zeroplayer.domain.usecase.GetDeviceVideoFoldersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FoldersUiState(
    val isLoading: Boolean = true,
    val folders: List<VideoFolder> = emptyList(),
    val enableAnimations: Boolean = true,
)

@HiltViewModel
class FoldersViewModel @Inject constructor(
    getDeviceVideoFolders: GetDeviceVideoFoldersUseCase,
    settingsRepository: com.zeroplayer.domain.repository.SettingsRepository,
) : ViewModel() {
    val uiState: StateFlow<FoldersUiState> = getDeviceVideoFolders()
        .let { foldersFlow ->
            settingsRepository.settings.map { it.enableAnimations }.let { animFlow ->
                combine(foldersFlow, animFlow) { folders, enableAnimations ->
                    FoldersUiState(isLoading = false, folders = folders, enableAnimations = enableAnimations)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FoldersUiState())
}

