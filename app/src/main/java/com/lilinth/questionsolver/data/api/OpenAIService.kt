package com.lilinth.questionsolver.data.api

import com.lilinth.questionsolver.data.api.model.ChatRequest
import com.lilinth.questionsolver.data.api.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {
    @POST("/v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): ChatResponse
}
