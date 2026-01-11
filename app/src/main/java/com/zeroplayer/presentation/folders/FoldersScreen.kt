package com.zeroplayer.presentation.folders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zeroplayer.domain.model.VideoFolder

@Composable
fun FoldersScreen(
    onOpenFolder: (bucketId: Long, folderName: String) -> Unit,
    viewModel: FoldersViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Folders",
            style = MaterialTheme.typography.titleLarge,
        )

        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        } else {
            AnimatedVisibility(
                visible = true,
                enter = if (state.enableAnimations) fadeIn() else fadeIn(initialAlpha = 1f),
                exit = if (state.enableAnimations) fadeOut() else fadeOut(targetAlpha = 0f),
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = state.folders,
                        key = { it.bucketId },
                        contentType = { "folder" },
                    ) { folder ->
                        FolderRow(
                            folder = folder,
                            onClick = { onOpenFolder(folder.bucketId, folder.name) },
                            enableAnimations = state.enableAnimations,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderRow(
    folder: VideoFolder,
    onClick: () -> Unit,
    enableAnimations: Boolean,
) {
    Row(
        modifier = Modifier
            .then(if (enableAnimations) Modifier.animateItemPlacementCompat() else Modifier)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = folder.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${folder.videoCount} videos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.animateItemPlacementCompat(): Modifier = this
