package com.lilinth.questionsolver.data.repository

import android.util.Base64
import com.lilinth.questionsolver.data.api.ApiService
import com.lilinth.questionsolver.data.api.RetrofitClient
import com.lilinth.questionsolver.data.model.ApiConfig
import com.lilinth.questionsolver.data.model.ChatRequest
import com.lilinth.questionsolver.data.model.ContentPart
import com.lilinth.questionsolver.data.model.ImageUrl
import com.lilinth.questionsolver.data.model.Message

/**
 * 问题解答仓库，负责调用 API 解答问题
 */
class QuestionRepository {
    suspend fun solveQuestion(
        imageBytes: ByteArray,
        apiConfig: ApiConfig
    ): Result<String> {
        return try {
            val apiService = RetrofitClient.createApiService(
                baseUrl = apiConfig.baseUrl,
                apiKey = apiConfig.apiKey
            )

            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            val imageUrl = "data:image/jpeg;base64,$base64Image"

            val request = ChatRequest(
                model = apiConfig.modelName,
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            ContentPart(
                                type = "text",
                                text = "请仔细分析这道题目，并给出详细的解答过程和最终答案。"
                            ),
                            ContentPart(
                                type = "image_url",
                                imageUrl = ImageUrl(url = imageUrl)
                            )
                        )
                    )
                )
            )

            val response = apiService.chatCompletion(request)
            val answer = response.choices.firstOrNull()?.message?.content
                ?: throw Exception("API 返回的答案为空")

            Result.success(answer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
