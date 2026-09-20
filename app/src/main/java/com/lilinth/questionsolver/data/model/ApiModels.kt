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
    val temperature: Double = 0.7,

    @SerializedName("stream")
    val stream: Boolean = false
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

/**
 * 流式响应模型（SSE）
 */
data class StreamResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("object")
    val objectType: String? = null,

    @SerializedName("created")
    val created: Long? = null,

    @SerializedName("model")
    val model: String? = null,

    @SerializedName("choices")
    val choices: List<StreamChoice>? = null
)

data class StreamChoice(
    @SerializedName("index")
    val index: Int,

    @SerializedName("delta")
    val delta: Delta,

    @SerializedName("finish_reason")
    val finishReason: String? = null
)

data class Delta(
    @SerializedName("role")
    val role: String? = null,

    @SerializedName("content")
    val content: String? = null
)
