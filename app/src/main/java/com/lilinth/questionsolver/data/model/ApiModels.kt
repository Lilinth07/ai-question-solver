package com.lilinth.questionsolver.data.model

import com.google.gson.annotations.SerializedName

/**
 * API 请求模型
 */
data class ChatRequest(
    @SerializedName("model")
    val model: String,

    @SerializedName("messages")
    val messages: List<Message>,

    @SerializedName("max_tokens")
    val maxTokens: Int = 2048,

    @SerializedName("temperature")
    val temperature: Double = 0.7
)

data class Message(
    @SerializedName("role")
    val role: String,

    @SerializedName("content")
    val content: List<ContentPart>
)

data class ContentPart(
    @SerializedName("type")
    val type: String,

    @SerializedName("text")
    val text: String? = null,

    @SerializedName("image_url")
    val imageUrl: ImageUrl? = null
)

data class ImageUrl(
    @SerializedName("url")
    val url: String
)

/**
 * API 响应模型
 */
data class ChatResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("object")
    val objectType: String,

    @SerializedName("created")
    val created: Long,

    @SerializedName("model")
    val model: String,

    @SerializedName("choices")
    val choices: List<Choice>
)

data class Choice(
    @SerializedName("index")
    val index: Int,

    @SerializedName("message")
    val message: ResponseMessage,

    @SerializedName("finish_reason")
    val finishReason: String
)

data class ResponseMessage(
    @SerializedName("role")
    val role: String,

    @SerializedName("content")
    val content: String
)
