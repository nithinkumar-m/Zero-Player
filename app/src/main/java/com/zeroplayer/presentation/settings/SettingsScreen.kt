package com.zeroplayer.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zeroplayer.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.TouchApp
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentSeekSeconds = (settings.doubleTapSeekMs / 1000f).coerceIn(5f, 30f)
    val sliderValue = remember { mutableFloatStateOf(currentSeekSeconds) }

    LaunchedEffect(currentSeekSeconds) {
        // Keep UI in sync if changed elsewhere.
        sliderValue.floatValue = currentSeekSeconds
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SettingsCard(
            icon = Icons.Default.Animation,
            title = stringResource(id = R.string.settings_enable_animations),
            subtitle = "Disable this if you feel lag on low-end devices.",
            trailing = {
                Switch(
                    checked = settings.enableAnimations,
                    onCheckedChange = viewModel::setEnableAnimations,
                )
            },
        )

        SettingsCard(
            icon = Icons.Default.TouchApp,
            title = stringResource(id = R.string.settings_double_tap_seek),
            subtitle = "Seek seconds when you double-tap the left/right side of the video.",
            trailing = {
                Text(
                    text = "${currentSeekSeconds.roundToInt()}s",
                    style = MaterialTheme.typography.labelLarge,
                )
            },
            extraContent = {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "5s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "30s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Slider(
                        value = sliderValue.floatValue,
                        onValueChange = { sliderValue.floatValue = it },
                        valueRange = 5f..30f,
                        steps = 24, // 1-second steps
                        colors = SliderDefaults.colors(),
                        onValueChangeFinished = {
                            val rounded = sliderValue.floatValue.roundToInt().toLong()
                            viewModel.setDoubleTapSeekMs(rounded * 1000L)
                        },
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    extraContent: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            ListItem(
                leadingContent = { Icon(imageVector = icon, contentDescription = null) },
                headlineContent = { Text(text = title) },
                supportingContent = { Text(text = subtitle) },
                trailingContent = { trailing() },
            )
            if (extraContent != null) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    extraContent()
                }
            }
        }
    }
}

