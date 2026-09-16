package com.lilinth.questionsolver.data.model

/**
 * API 配置数据类
 */
data class ApiConfig(
    val baseUrl: String = "",
    val apiKey: String = "",
    val modelName: String = ""
) {
    fun isValid(): Boolean {
        return baseUrl.isNotBlank() && apiKey.isNotBlank() && modelName.isNotBlank()
    }
}
