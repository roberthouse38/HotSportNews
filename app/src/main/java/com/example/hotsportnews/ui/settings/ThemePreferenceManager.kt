// ThemePreferenceManager.kt
package com.example.mysubmissionawal.ui.settings

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Initialize DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemePreferenceManager private constructor(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = booleanPreferencesKey("theme_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val isDarkMode = preferences[THEME_KEY] ?: false
            Log.d("Settings", "Dark Mode Active: $isDarkMode")
            isDarkMode
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ThemePreferenceManager? = null

        fun getInstance(dataStore: DataStore<Preferences>): ThemePreferenceManager {
            return INSTANCE ?: synchronized(this) {
                val instance = ThemePreferenceManager(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
