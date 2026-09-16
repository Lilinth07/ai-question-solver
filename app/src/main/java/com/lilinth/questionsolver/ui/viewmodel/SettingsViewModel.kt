package com.lilinth.questionsolver.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lilinth.questionsolver.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 设置页面 ViewModel
 */
class SettingsViewModel(
    private val context: Context
) : ViewModel() {

    private val settingsRepository = SettingsRepository(context)

    private val _apiBaseUrl = MutableStateFlow("")
    val apiBaseUrl: StateFlow<String> = _apiBaseUrl

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey

    private val _modelName = MutableStateFlow("")
    val modelName: StateFlow<String> = _modelName

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _apiBaseUrl.value = settingsRepository.apiBaseUrl.first()
            _apiKey.value = settingsRepository.apiKey.first()
            _modelName.value = settingsRepository.modelName.first()
        }
    }

    fun onApiBaseUrlChanged(url: String) {
        _apiBaseUrl.value = url
    }

    fun onApiKeyChanged(key: String) {
        _apiKey.value = key
    }

    fun onModelNameChanged(model: String) {
        _modelName.value = model
    }

    fun saveSettings() {
        viewModelScope.launch {
            _saveStatus.value = SaveStatus.Saving

            try {
                settingsRepository.saveApiBaseUrl(_apiBaseUrl.value)
                settingsRepository.saveApiKey(_apiKey.value)
                settingsRepository.saveModelName(_modelName.value)
                _saveStatus.value = SaveStatus.Success
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "保存失败")
            }
        }
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }
}

sealed class SaveStatus {
    object Idle : SaveStatus()
    object Saving : SaveStatus()
    object Success : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}
