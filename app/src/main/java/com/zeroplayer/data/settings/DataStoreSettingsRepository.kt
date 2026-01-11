package com.zeroplayer.data.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.zeroplayer.domain.repository.PlayerSettings
import com.zeroplayer.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
) : SettingsRepository {

    private object Keys {
        val DOUBLE_TAP_SEEK_MS = longPreferencesKey("double_tap_seek_ms")
        val ENABLE_ANIMATIONS = booleanPreferencesKey("enable_animations")
    }

    override val settings: Flow<PlayerSettings> =
        settingsDataStore.dataStore.data.map { prefs ->
            PlayerSettings(
                doubleTapSeekMs = prefs[Keys.DOUBLE_TAP_SEEK_MS] ?: 10_000L,
                enableAnimations = prefs[Keys.ENABLE_ANIMATIONS] ?: true,
            )
        }

    override suspend fun setDoubleTapSeekMs(value: Long) {
        settingsDataStore.dataStore.edit { it[Keys.DOUBLE_TAP_SEEK_MS] = value }
    }

    override suspend fun setEnableAnimations(value: Boolean) {
        settingsDataStore.dataStore.edit { it[Keys.ENABLE_ANIMATIONS] = value }
    }
}

