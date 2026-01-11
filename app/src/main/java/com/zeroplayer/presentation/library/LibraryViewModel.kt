package com.zeroplayer.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeroplayer.domain.model.Video
import com.zeroplayer.domain.usecase.GetDeviceVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class LibraryUiState(
    val isLoading: Boolean = true,
    val videos: List<Video> = emptyList(),
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    getDeviceVideos: GetDeviceVideosUseCase,
) : ViewModel() {
    val uiState: StateFlow<LibraryUiState> = getDeviceVideos()
        .map { LibraryUiState(isLoading = false, videos = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState())
}

