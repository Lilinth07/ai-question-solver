package com.lilinth.questionsolver.data.api

import com.lilinth.questionsolver.data.model.ChatRequest
import com.lilinth.questionsolver.data.model.ChatResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * OpenAI 兼容 API 接口
 */
interface ApiService {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse

    @Streaming
    @POST("chat/completions")
    suspend fun chatCompletionStream(@Body request: ChatRequest): ResponseBody
}
