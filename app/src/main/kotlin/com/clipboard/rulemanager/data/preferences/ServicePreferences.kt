package com.clipboard.rulemanager.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_DATASTORE_NAME = "clipboard_settings"

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATASTORE_NAME
)

class ServicePreferences(private val context: Context) {
    companion object {
        val SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
    }

    val isServiceEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences -> preferences[SERVICE_ENABLED] ?: false }

    suspend fun setServiceEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[SERVICE_ENABLED] = enabled
        }
    }
}
