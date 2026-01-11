package com.zeroplayer.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zeroplayer.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SettingRow(
            title = stringResource(id = R.string.settings_enable_animations),
            subtitle = "Smooth list animations (disable if you feel lag)",
            trailing = {
                Switch(
                    checked = settings.enableAnimations,
                    onCheckedChange = viewModel::setEnableAnimations,
                )
            },
        )

        SettingRow(
            title = stringResource(id = R.string.settings_double_tap_seek),
            subtitle = "Double-tap left/right to seek (${settings.doubleTapSeekMs / 1000}s)",
            trailing = {
                // Lightweight toggle between common values
                Text(
                    text = "${settings.doubleTapSeekMs / 1000}s",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(8.dp),
                )
            },
            onClick = {
                val next = when (settings.doubleTapSeekMs) {
                    5_000L -> 10_000L
                    10_000L -> 15_000L
                    else -> 5_000L
                }
                viewModel.setDoubleTapSeekMs(next)
            },
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) { trailing() }
    }
}

