package com.zeroplayer.presentation.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeroplayer.domain.model.VideoFolder
import com.zeroplayer.domain.usecase.GetDeviceVideoFoldersUseCase
import com.zeroplayer.domain.usecase.RefreshDeviceVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoldersUiState(
    val isLoading: Boolean = true,
    val folders: List<VideoFolder> = emptyList(),
    val enableAnimations: Boolean = true,
    val isRefreshing: Boolean = false,
)

@HiltViewModel
class FoldersViewModel @Inject constructor(
    getDeviceVideoFolders: GetDeviceVideoFoldersUseCase,
    settingsRepository: com.zeroplayer.domain.repository.SettingsRepository,
    private val refreshDeviceVideos: RefreshDeviceVideosUseCase,
) : ViewModel() {
    private val refreshing = MutableStateFlow(false)

    val uiState: StateFlow<FoldersUiState> =
        combine(
            getDeviceVideoFolders(),
            settingsRepository.settings.map { it.enableAnimations },
            refreshing,
        ) { folders, enableAnimations, isRefreshing ->
            FoldersUiState(
                isLoading = false,
                folders = folders,
                enableAnimations = enableAnimations,
                isRefreshing = isRefreshing,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FoldersUiState())

    fun refresh() {
        if (refreshing.value) return
        viewModelScope.launch {
            refreshing.value = true
            try {
                refreshDeviceVideos()
            } finally {
                refreshing.value = false
            }
        }
    }
}

