package com.lilinth.questionsolver.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lilinth.questionsolver.data.model.ApiConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * 配置仓库，负责存储和读取 API 配置
 */
class ConfigRepository(private val context: Context) {
    companion object {
        private val KEY_BASE_URL = stringPreferencesKey("base_url")
        private val KEY_API_KEY = stringPreferencesKey("api_key")
        private val KEY_MODEL_NAME = stringPreferencesKey("model_name")
    }

    val apiConfigFlow: Flow<ApiConfig> = context.dataStore.data.map { preferences ->
        ApiConfig(
            baseUrl = preferences[KEY_BASE_URL] ?: "",
            apiKey = preferences[KEY_API_KEY] ?: "",
            modelName = preferences[KEY_MODEL_NAME] ?: ""
        )
    }

    suspend fun saveApiConfig(config: ApiConfig) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BASE_URL] = config.baseUrl
            preferences[KEY_API_KEY] = config.apiKey
            preferences[KEY_MODEL_NAME] = config.modelName
        }
    }
}
