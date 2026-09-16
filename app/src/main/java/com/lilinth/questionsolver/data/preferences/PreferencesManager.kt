package com.lilinth.questionsolver.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        private val BASE_URL_KEY = stringPreferencesKey("base_url")
        private val API_KEY_KEY = stringPreferencesKey("api_key")
        private val MODEL_KEY = stringPreferencesKey("model")
    }

    val baseUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[BASE_URL_KEY] ?: ""
    }

    val apiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[API_KEY_KEY] ?: ""
    }

    val model: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[MODEL_KEY] ?: ""
    }

    suspend fun saveConfig(baseUrl: String, apiKey: String, model: String) {
        context.dataStore.edit { preferences ->
            preferences[BASE_URL_KEY] = baseUrl
            preferences[API_KEY_KEY] = apiKey
            preferences[MODEL_KEY] = model
        }
    }

    suspend fun getBaseUrl(): String {
        return baseUrl.first()
    }

    suspend fun getApiKey(): String {
        return apiKey.first()
    }

    suspend fun getModel(): String {
        return model.first()
    }
}
