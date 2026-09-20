package com.lilinth.questionsolver.data.repository

import android.util.Base64
import com.google.gson.Gson
import com.lilinth.questionsolver.data.api.ApiService
import com.lilinth.questionsolver.data.api.RetrofitClient
import com.lilinth.questionsolver.data.model.ApiConfig
import com.lilinth.questionsolver.data.model.ChatRequest
import com.lilinth.questionsolver.data.model.ContentPart
import com.lilinth.questionsolver.data.model.ImageUrl
import com.lilinth.questionsolver.data.model.Message
import com.lilinth.questionsolver.data.model.StreamResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 问题解答仓库，负责调用 API 解答问题
 */
class QuestionRepository {
    private val gson = Gson()

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
                                text = "请仔细分析这道题目，并给出详细的解答过程和最终答案。如果涉及数学公式，请使用 LaTeX 格式（用 $ 或 $$ 包裹）。"
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

    /**
     * 流式解答问题（SSE）
     */
    fun solveQuestionStream(
        imageBytes: ByteArray,
        apiConfig: ApiConfig
    ): Flow<String> = flow {
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
                            text = "请仔细分析这道题目，并给出详细的解答过程和最终答案。如果涉及数学公式，请使用 LaTeX 格式（用 $ 或 $$ 包裹）。"
                        ),
                        ContentPart(
                            type = "image_url",
                            imageUrl = ImageUrl(url = imageUrl)
                        )
                    )
                )
            ),
            stream = true
        )

        val responseBody = apiService.chatCompletionStream(request)
        val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))

        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue

                // SSE 格式: data: {...}
                if (currentLine.startsWith("data: ")) {
                    val jsonData = currentLine.substring(6).trim()

                    // 流结束标志
                    if (jsonData == "[DONE]") {
                        break
                    }

                    try {
                        val streamResponse = gson.fromJson(jsonData, StreamResponse::class.java)
                        val content = streamResponse.choices?.firstOrNull()?.delta?.content

                        if (content != null) {
                            emit(content)
                        }
                    } catch (e: Exception) {
                        // 忽略解析错误，继续处理下一行
                    }
                }
            }
        } finally {
            reader.close()
            responseBody.close()
        }
    }.flowOn(Dispatchers.IO)
}
