package com.zeroplayer.presentation.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeroplayer.domain.model.Video
import com.zeroplayer.domain.usecase.GetDeviceVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FolderVideosUiState(
    val isLoading: Boolean = true,
    val folderName: String = "",
    val videos: List<Video> = emptyList(),
    val enableAnimations: Boolean = true,
)

@HiltViewModel
class FolderVideosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDeviceVideos: GetDeviceVideosUseCase,
    settingsRepository: com.zeroplayer.domain.repository.SettingsRepository,
) : ViewModel() {
    private val bucketId: Long = savedStateHandle.get<String>("bucketId")?.toLongOrNull() ?: -1L
    private val folderNameArg: String = savedStateHandle.get<String>("folderName").orEmpty()

    val uiState: StateFlow<FolderVideosUiState> =
        combine(
            getDeviceVideos(),
            settingsRepository.settings.map { it.enableAnimations },
        ) { allVideos, enableAnimations ->
            val videos = allVideos.filter { it.bucketId == bucketId }
            val resolvedName = folderNameArg.ifBlank { videos.firstOrNull()?.bucketName.orEmpty() }
            FolderVideosUiState(
                isLoading = false,
                folderName = resolvedName.ifBlank { "Folder" },
                videos = videos,
                enableAnimations = enableAnimations,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FolderVideosUiState())
}

