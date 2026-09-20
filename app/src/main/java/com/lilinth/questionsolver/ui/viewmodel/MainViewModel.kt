package com.lilinth.questionsolver.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lilinth.questionsolver.data.model.ApiConfig
import com.lilinth.questionsolver.data.repository.ConfigRepository
import com.lilinth.questionsolver.data.repository.QuestionRepository
import com.lilinth.questionsolver.util.ImageUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * 主页面 ViewModel
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val configRepository = ConfigRepository(application)
    private val questionRepository = QuestionRepository()

    private val _apiConfig = MutableStateFlow(ApiConfig())
    val apiConfig: StateFlow<ApiConfig> = _apiConfig.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _answer = MutableStateFlow<String>("")
    val answer: StateFlow<String> = _answer.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    init {
        loadApiConfig()
    }

    private fun loadApiConfig() {
        viewModelScope.launch {
            configRepository.apiConfigFlow.collect { config ->
                _apiConfig.value = config
            }
        }
    }

    fun saveApiConfig(config: ApiConfig) {
        viewModelScope.launch {
            configRepository.saveApiConfig(config)
        }
    }

    fun selectImage(uri: Uri) {
        _selectedImageUri.value = uri
        _answer.value = ""
        _errorMessage.value = null
    }

    /**
     * 流式解答问题（推荐）
     */
    fun solveQuestionStream() {
        val uri = _selectedImageUri.value
        val config = _apiConfig.value

        if (uri == null) {
            _errorMessage.value = "请先选择图片"
            return
        }

        if (!config.isValid()) {
            _errorMessage.value = "请先配置 API 信息"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _isStreaming.value = true
            _errorMessage.value = null
            _answer.value = ""

            val imageBytes = ImageUtil.compressImage(getApplication(), uri)
            if (imageBytes == null) {
                _errorMessage.value = "图片加载失败"
                _isLoading.value = false
                _isStreaming.value = false
                return@launch
            }

            questionRepository.solveQuestionStream(imageBytes, config)
                .catch { error ->
                    _errorMessage.value = "解答失败: ${error.message}"
                    _isStreaming.value = false
                }
                .collect { chunk ->
                    _isLoading.value = false
                    _answer.value += chunk
                }

            _isStreaming.value = false
        }
    }

    /**
     * 非流式解答（备用）
     */
    fun solveQuestion() {
        val uri = _selectedImageUri.value
        val config = _apiConfig.value

        if (uri == null) {
            _errorMessage.value = "请先选择图片"
            return
        }

        if (!config.isValid()) {
            _errorMessage.value = "请先配置 API 信息"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _answer.value = ""

            val imageBytes = ImageUtil.compressImage(getApplication(), uri)
            if (imageBytes == null) {
                _errorMessage.value = "图片加载失败"
                _isLoading.value = false
                return@launch
            }

            val result = questionRepository.solveQuestion(imageBytes, config)
            _isLoading.value = false

            result.onSuccess { answer ->
                _answer.value = answer
            }.onFailure { error ->
                _errorMessage.value = "解答失败: ${error.message}"
            }
        }
    }

    fun clearAnswer() {
        _answer.value = ""
        _selectedImageUri.value = null
        _errorMessage.value = null
        _isStreaming.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
