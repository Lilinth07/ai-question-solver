package com.lilinth.questionsolver.data.remote

import com.lilinth.questionsolver.data.model.ChatRequest
import com.lilinth.questionsolver.data.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * OpenAI 兼容 API 接口
 */
interface ApiService {
    @POST("chat/completions")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
