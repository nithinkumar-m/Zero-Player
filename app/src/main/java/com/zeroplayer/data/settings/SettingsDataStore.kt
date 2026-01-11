package com.zeroplayer.data.settings

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("settings") },
    )
}

